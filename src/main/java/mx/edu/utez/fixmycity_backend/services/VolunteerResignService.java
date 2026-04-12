package mx.edu.utez.fixmycity_backend.services;

import mx.edu.utez.fixmycity_backend.dto.request.SquadLeaveRequest;
import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.dto.response.PerfilActualizadoResponse;
import mx.edu.utez.fixmycity_backend.dto.response.PerfilResponse;
import mx.edu.utez.fixmycity_backend.modelos.Cuadrilla;
import mx.edu.utez.fixmycity_backend.modelos.Usuario;
import mx.edu.utez.fixmycity_backend.modelos.Voluntario;
import mx.edu.utez.fixmycity_backend.modelos.miembrosCuadrilla;
import mx.edu.utez.fixmycity_backend.repositories.CuadrillaRepository;
import mx.edu.utez.fixmycity_backend.repositories.UsuarioRepository;
import mx.edu.utez.fixmycity_backend.repositories.VoluntarioRepository;
import mx.edu.utez.fixmycity_backend.repositories.VotacionRepository;
import mx.edu.utez.fixmycity_backend.repositories.miembrosCuadrillaRepository;
import mx.edu.utez.fixmycity_backend.repositories.reporteAsignadoCuadrillaRepository;
import mx.edu.utez.fixmycity_backend.security.JwtUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Baja del programa de voluntariado y/o de la cuadrilla activa para el usuario autenticado.
 * <ul>
 *   <li>Sin cuadrilla activa: elimina fila {@code voluntario} y pasa {@code usuario.tipo} a {@code ciudadano}.</li>
 *   <li>Miembro no líder: sale de la cuadrilla; si ya no queda en ninguna activa, vuelve a ciudadano.</li>
 *   <li>Líder: transfiere liderazgo a otro miembro (menor {@code numeroVoluntario} distinto del líder);
 *       si la cuadrilla tiene reportes en Asignado/En camino/En curso, no permite salir (409).</li>
 * </ul>
 * Siempre devuelve JWT nuevo (el filtro usa claims del token, no el tipo leído en cada request desde BD).
 */
@Service
public class VolunteerResignService {

    private final VoluntarioRepository voluntarioRepository;
    private final miembrosCuadrillaRepository miembrosCuadrillaRepository;
    private final CuadrillaRepository cuadrillaRepository;
    private final reporteAsignadoCuadrillaRepository reporteAsignadoCuadrillaRepository;
    private final VotacionRepository votacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final PerfilService perfilService;
    private final JwtUtils jwtUtils;

    public VolunteerResignService(VoluntarioRepository voluntarioRepository,
                                  miembrosCuadrillaRepository miembrosCuadrillaRepository,
                                  CuadrillaRepository cuadrillaRepository,
                                  reporteAsignadoCuadrillaRepository reporteAsignadoCuadrillaRepository,
                                  VotacionRepository votacionRepository,
                                  UsuarioRepository usuarioRepository,
                                  PerfilService perfilService,
                                  JwtUtils jwtUtils) {
        this.voluntarioRepository = voluntarioRepository;
        this.miembrosCuadrillaRepository = miembrosCuadrillaRepository;
        this.cuadrillaRepository = cuadrillaRepository;
        this.reporteAsignadoCuadrillaRepository = reporteAsignadoCuadrillaRepository;
        this.votacionRepository = votacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.perfilService = perfilService;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    public ApiResponse salir(SquadLeaveRequest request, int idUsuario, String nombreUsuario) {
        Optional<Integer> idVolOpt = voluntarioRepository.findIdByUsuario(idUsuario)
                .map(o -> ((Number) o).intValue());
        if (idVolOpt.isEmpty()) {
            return new ApiResponse(false, "No eres voluntario");
        }
        int idVoluntario = idVolOpt.get();

        List<miembrosCuadrilla> todas = miembrosCuadrillaRepository.findByVoluntario_IdVoluntario(idVoluntario);
        List<miembrosCuadrilla> activas = todas.stream()
                .filter(m -> m.getCuadrilla() != null
                        && "activa".equalsIgnoreCase(m.getCuadrilla().getEstado()))
                .collect(Collectors.toList());

        if (activas.isEmpty()) {
            List<miembrosCuadrilla> todasMembresias = miembrosCuadrillaRepository.findByVoluntario_IdVoluntario(idVoluntario);
            for (miembrosCuadrilla m : todasMembresias) {
                if (m.getCuadrilla() != null) {
                    votacionRepository.deleteByVoluntarioAndCuadrilla(idVoluntario, m.getCuadrilla().getIdCuadrilla());
                }
            }
            miembrosCuadrillaRepository.deleteAll(todasMembresias);
            eliminarVoluntarioYVolverCiudadano(idVoluntario, idUsuario);
            return exitoConPerfilYToken(nombreUsuario);
        }

        Integer idCuadrillaParam = request != null ? request.getIdCuadrilla() : null;
        miembrosCuadrilla membresia;
        if (activas.size() == 1) {
            membresia = activas.get(0);
        } else if (idCuadrillaParam != null && idCuadrillaParam > 0) {
            membresia = activas.stream()
                    .filter(m -> m.getCuadrilla().getIdCuadrilla() == idCuadrillaParam)
                    .findFirst()
                    .orElse(null);
            if (membresia == null) {
                return new ApiResponse(false, "No perteneces a la cuadrilla indicada");
            }
        } else {
            return new ApiResponse(false, "Eres miembro de varias cuadrillas activas; envía idCuadrilla en el cuerpo");
        }

        int idCuadrilla = membresia.getCuadrilla().getIdCuadrilla();

        if (!reporteAsignadoCuadrillaRepository.findActiveByCuadrilla(idCuadrilla).isEmpty()) {
            return new ApiResponse(false, "No puedes salir mientras la cuadrilla tenga reportes asignados en curso");
        }

        boolean esLider = cuadrillaRepository.findIdsByIdLiderVoluntario(idVoluntario).stream()
                .map(o -> ((Number) o).intValue())
                .anyMatch(id -> id == idCuadrilla);

        if (esLider) {
            return salirComoLider(idVoluntario, idUsuario, idCuadrilla, membresia, nombreUsuario);
        }

        votacionRepository.deleteByVoluntarioAndCuadrilla(idVoluntario, idCuadrilla);
        miembrosCuadrillaRepository.delete(membresia);

        if (!tieneMembresiaActiva(idVoluntario)) {
            eliminarVoluntarioYVolverCiudadano(idVoluntario, idUsuario);
        }

        return exitoConPerfilYToken(nombreUsuario);
    }

    private ApiResponse salirComoLider(int idVoluntarioLider, int idUsuarioLider, int idCuadrilla,
                                       miembrosCuadrilla filaLider, String nombreUsuario) {
        List<miembrosCuadrilla> miembros = miembrosCuadrillaRepository
                .findByCuadrilla_IdCuadrillaOrderByNumeroVoluntarioAsc(idCuadrilla);
        Optional<miembrosCuadrilla> sucesorOpt = miembros.stream()
                .filter(m -> m.getVoluntario().getIdVoluntario() != idVoluntarioLider)
                .findFirst();
        if (sucesorOpt.isEmpty()) {
            return new ApiResponse(false, "No hay otro miembro para transferir el liderazgo");
        }

        miembrosCuadrilla sucesorRow = sucesorOpt.get();
        Voluntario nuevoLider = sucesorRow.getVoluntario();

        Cuadrilla cuadrilla = cuadrillaRepository.findById(idCuadrilla)
                .orElseThrow(() -> new IllegalStateException("Cuadrilla no encontrada"));
        cuadrilla.setVoluntario(nuevoLider);
        cuadrillaRepository.save(cuadrilla);

        sucesorRow.setTipo("lider");
        miembrosCuadrillaRepository.save(sucesorRow);

        votacionRepository.deleteByVoluntarioAndCuadrilla(idVoluntarioLider, idCuadrilla);
        miembrosCuadrillaRepository.delete(filaLider);

        eliminarVoluntarioYVolverCiudadano(idVoluntarioLider, idUsuarioLider);

        return exitoConPerfilYToken(nombreUsuario);
    }

    private void eliminarVoluntarioYVolverCiudadano(int idVoluntario, int idUsuario) {
        votacionRepository.deleteByVoluntario(idVoluntario);
        voluntarioRepository.deleteById(idVoluntario);
        usuarioRepository.updateTipo(idUsuario, "ciudadano");
    }

    private boolean tieneMembresiaActiva(int idVoluntario) {
        return miembrosCuadrillaRepository.findByVoluntario_IdVoluntario(idVoluntario).stream()
                .anyMatch(m -> m.getCuadrilla() != null
                        && "activa".equalsIgnoreCase(m.getCuadrilla().getEstado()));
    }

    private ApiResponse exitoConPerfilYToken(String nombreUsuario) {
        ApiResponse perfilResp = perfilService.obtenerPerfil(nombreUsuario);
        if (!perfilResp.isSuccess() || perfilResp.getData() == null) {
            return new ApiResponse(false, "No se pudo reconstruir el perfil tras la baja");
        }
        PerfilResponse perfil = (PerfilResponse) perfilResp.getData();
        Optional<Usuario> uOpt = usuarioRepository.buscarIdPorNombre(nombreUsuario)
                .map(o -> ((Number) o).intValue())
                .flatMap(usuarioRepository::findById);
        if (uOpt.isEmpty()) {
            return new ApiResponse(false, "Usuario no encontrado");
        }
        Usuario u = uOpt.get();
        int idMun = u.getMunicipio() != null ? u.getMunicipio().getIdMunicipio() : -1;
        String token = jwtUtils.generateToken(u.getNombreUsuario(), u.getTipo(), idMun);
        return new ApiResponse(true, "Baja procesada correctamente",
                new PerfilActualizadoResponse(perfil, token));
    }
}
