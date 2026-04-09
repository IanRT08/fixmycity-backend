package mx.edu.utez.fixmycity_backend.services;

import mx.edu.utez.fixmycity_backend.dto.request.CuadrillaRequest;
import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.dto.response.CuadrillaResponse;
import mx.edu.utez.fixmycity_backend.modelos.*;
import mx.edu.utez.fixmycity_backend.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CuadrillaService {

    private final CuadrillaRepository cuadrillaRepository;
    private final miembrosCuadrillaRepository miembrosCuadrillaRepository;
    private final VoluntarioRepository voluntarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final MunicipioRepository municipioRepository;

    public CuadrillaService(CuadrillaRepository cuadrillaRepository,
                            miembrosCuadrillaRepository miembrosCuadrillaRepository,
                            VoluntarioRepository voluntarioRepository,
                            UsuarioRepository usuarioRepository,
                            MunicipioRepository municipioRepository) {
        this.cuadrillaRepository = cuadrillaRepository;
        this.miembrosCuadrillaRepository = miembrosCuadrillaRepository;
        this.voluntarioRepository = voluntarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.municipioRepository = municipioRepository;
    }

    @Transactional
    public ApiResponse crearCuadrilla(CuadrillaRequest request) {
        if (cuadrillaRepository.findIdByNombreCuadrilla(request.getNombreCuadrilla()).isPresent())
            return new ApiResponse(false, "Ya existe una cuadrilla con ese nombre");
        Optional<Municipios> municipioOpt = municipioRepository.findById(request.getIdMunicipio());
        if (municipioOpt.isEmpty() || !municipioOpt.get().getEstado().equals("Activo"))
            return new ApiResponse(false, "El municipio seleccionado no está disponible");
        if (!request.getIdMiembros().contains(request.getIdLider()))
            return new ApiResponse(false, "El líder debe ser uno de los 5 integrantes");
        for (Integer idVol : request.getIdMiembros()) {
            if (voluntarioRepository.findById(idVol).isEmpty())
                return new ApiResponse(false, "El voluntario con id " + idVol + " no existe");
        }
        Optional<Voluntario> liderOpt = voluntarioRepository.findById(request.getIdLider());
        if (liderOpt.isEmpty()) return new ApiResponse(false, "El líder seleccionado no es válido");

        Cuadrilla cuadrilla = new Cuadrilla();
        cuadrilla.setNombreCuadrilla(request.getNombreCuadrilla());
        cuadrilla.setIdMunicipio(municipioOpt.get());
        cuadrilla.setVoluntario(liderOpt.get());
        cuadrilla.setEstado("activa");
        cuadrillaRepository.save(cuadrilla);

        for (Integer idVol : request.getIdMiembros()) {
            Voluntario voluntario = voluntarioRepository.findById(idVol).get();
            miembrosCuadrilla miembro = new miembrosCuadrilla();
            miembro.setCuadrilla(cuadrilla);
            miembro.setVoluntario(voluntario);
            miembro.setTipo(idVol.equals(request.getIdLider()) ? "lider" : "voluntario");
            miembrosCuadrillaRepository.save(miembro);
        }
        return new ApiResponse(true, "Cuadrilla creada correctamente");
    }

    @Transactional(readOnly = true)
    public ApiResponse listarCuadrillas(String estado, int idAdmin) {
        Optional<Usuario> adminOpt = usuarioRepository.findById(idAdmin);
        if (adminOpt.isEmpty()) return new ApiResponse(false, "Administrador no encontrado");
        Usuario admin = adminOpt.get();

        List<Object> ids;
        if (admin.getTipo().equals("superadmin")) {
            ids = cuadrillaRepository.findIdsByEstado(estado);
        } else {
            if (admin.getMunicipio() == null)
                return new ApiResponse(false, "No se pudo obtener el municipio del administrador");
            ids = cuadrillaRepository.findIdsByEstadoAndMunicipio(estado, admin.getMunicipio().getIdMunicipio());
        }

        List<CuadrillaResponse> response = ids.stream()
                .map(o -> ((Number) o).intValue())
                .map(id -> cuadrillaRepository.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .map(c -> {
                    List<miembrosCuadrilla> miembros = miembrosCuadrillaRepository.findByCuadrilla(c.getIdCuadrilla());
                    List<String> nombres = miembros.stream()
                            .map(m -> m.getVoluntario().getUsuario().getNombreUsuario())
                            .collect(Collectors.toList());
                    return new CuadrillaResponse(
                            c.getIdCuadrilla(), c.getNombreCuadrilla(),
                            c.getIdMunicipio().getNombre(),
                            c.getVoluntario().getUsuario().getNombreUsuario(),
                            c.getEstado(), nombres);
                }).collect(Collectors.toList());

        return new ApiResponse(true, "Cuadrillas obtenidas correctamente", response);
    }

    // Returns list of {idUsuario, nombreUsuario} for voluntarios without active squad
    @Transactional(readOnly = true)
    public ApiResponse listarVoluntariosDisponibles(int idMunicipio) {
        List<Object> ids = voluntarioRepository.findAvailableIdsByMunicipio(idMunicipio);
        List<Map<String, Object>> result = ids.stream()
                .map(o -> ((Number) o).intValue())
                .map(id -> voluntarioRepository.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .map(v -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("idUsuario", v.getIdVoluntario()); // idVoluntario for squad creation
                    m.put("nombreUsuario", v.getUsuario().getNombreUsuario());
                    m.put("tipo", "voluntario");
                    return m;
                }).collect(Collectors.toList());
        return new ApiResponse(true, "Voluntarios disponibles obtenidos correctamente", result);
    }

    @Transactional
    public ApiResponse cambiarEstadoCuadrilla(int idCuadrilla, String estado) {
        if (cuadrillaRepository.findById(idCuadrilla).isEmpty())
            return new ApiResponse(false, "Cuadrilla no encontrada");
        cuadrillaRepository.updateEstado(idCuadrilla, estado);
        return new ApiResponse(true, "Estado de cuadrilla actualizado correctamente");
    }
}
