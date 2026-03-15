package mx.edu.utez.fixmycity_backend.services;

import mx.edu.utez.fixmycity_backend.dto.request.CuadrillaRequest;
import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.dto.response.CuadrillaResponse;
import mx.edu.utez.fixmycity_backend.modelos.*;
import mx.edu.utez.fixmycity_backend.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CuadrillaService {

    @Autowired
    private CuadrillaRepository cuadrillaRepository;

    @Autowired
    private miembrosCuadrillaRepository miembrosCuadrillaRepository;

    @Autowired
    private VoluntarioRepository voluntarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MunicipioRepository municipioRepository;

    @Transactional
    public ApiResponse crearCuadrilla(CuadrillaRequest request) {

        if (cuadrillaRepository.findIdByNombreCuadrilla(request.getNombreCuadrilla()).isPresent()) {
            return new ApiResponse(false, "Ya existe una cuadrilla con ese nombre");
        }

        Optional<Municipios> municipioOpt = municipioRepository.findById(request.getIdMunicipio());
        if (municipioOpt.isEmpty() || !municipioOpt.get().getEstado().equals("Activo")) {
            return new ApiResponse(false, "El municipio seleccionado no está disponible");
        }

        if (!request.getIdMiembros().contains(request.getIdLider())) {
            return new ApiResponse(false, "El líder debe ser uno de los 5 integrantes");
        }

        for (Integer idVoluntario : request.getIdMiembros()) {
            if (voluntarioRepository.findById(idVoluntario).isEmpty()) {
                return new ApiResponse(false, "El voluntario con id " + idVoluntario + " no existe");
            }
        }

        Optional<Voluntario> liderOpt = voluntarioRepository.findById(request.getIdLider());
        if (liderOpt.isEmpty()) {
            return new ApiResponse(false, "El líder seleccionado no es un voluntario válido");
        }

        Cuadrilla cuadrilla = new Cuadrilla();
        cuadrilla.setNombreCuadrilla(request.getNombreCuadrilla());
        cuadrilla.setIdMunicipio(municipioOpt.get());
        cuadrilla.setVoluntario(liderOpt.get());
        cuadrilla.setEstado("activa");
        cuadrillaRepository.save(cuadrilla);

        for (Integer idVoluntario : request.getIdMiembros()) {
            Voluntario voluntario = voluntarioRepository.findById(idVoluntario).get();
            miembrosCuadrilla miembro = new miembrosCuadrilla();
            miembro.setCuadrilla(cuadrilla);
            miembro.setUsuario(voluntario.getUsuario());
            miembro.setTipo(idVoluntario.equals(request.getIdLider()) ? "lider" : "miembro");
            miembrosCuadrillaRepository.save(miembro);
        }

        return new ApiResponse(true, "Cuadrilla creada correctamente");
    }

    public ApiResponse listarCuadrillas(String estado, int idAdmin) {
        Optional<Usuario> adminOpt = usuarioRepository.findById(idAdmin);
        if (adminOpt.isEmpty()) {
            return new ApiResponse(false, "Administrador no encontrado");
        }
        Usuario admin = adminOpt.get();

        List<Cuadrilla> cuadrillas;
        if (admin.getTipo().equals("superadmin")) {
            cuadrillas = cuadrillaRepository.findIdsByEstado(estado).stream()
                    .map(o -> ((Number) o).intValue())
                    .map(id -> cuadrillaRepository.findById(id).orElse(null))
                    .filter(c -> c != null)
                    .collect(Collectors.toList());
        } else {
            if (admin.getMunicipio() == null) {
                return new ApiResponse(false, "No se pudo obtener el municipio del administrador");
            }
            cuadrillas = cuadrillaRepository.findIdsByEstadoAndMunicipio(estado, admin.getMunicipio().getIdMunicipio()).stream()
                    .map(o -> ((Number) o).intValue())
                    .map(id -> cuadrillaRepository.findById(id).orElse(null))
                    .filter(c -> c != null)
                    .collect(Collectors.toList());
        }

        List<CuadrillaResponse> response = cuadrillas.stream().map(c -> {
            List<miembrosCuadrilla> miembros = miembrosCuadrillaRepository
                    .findByCuadrilla(c.getIdCuadrilla());

            List<String> nombresMiembros = miembros.stream()
                    .map(m -> m.getUsuario().getNombreUsuario())
                    .collect(Collectors.toList());

            return new CuadrillaResponse(
                    c.getIdCuadrilla(),
                    c.getNombreCuadrilla(),
                    c.getIdMunicipio().getNombre(),
                    c.getVoluntario().getUsuario().getNombreUsuario(),
                    c.getEstado(),
                    nombresMiembros
            );
        }).collect(Collectors.toList());

        return new ApiResponse(true, "Cuadrillas obtenidas correctamente", response);
    }

    public ApiResponse listarVoluntariosDisponibles(int idMunicipio) {
        List<Voluntario> voluntarios = voluntarioRepository.findAvailableIdsByMunicipio(idMunicipio).stream()
                .map(o -> ((Number) o).intValue())
                .map(id -> voluntarioRepository.findById(id).orElse(null))
                .filter(v -> v != null)
                .collect(Collectors.toList());

        List<String> nombres = voluntarios.stream()
                .map(v -> v.getUsuario().getNombreUsuario())
                .collect(Collectors.toList());

        return new ApiResponse(true, "Voluntarios disponibles obtenidos correctamente", nombres);
    }

    @Transactional
    public ApiResponse cambiarEstadoCuadrilla(int idCuadrilla, String estado) {

        if (cuadrillaRepository.findById(idCuadrilla).isEmpty()) {
            return new ApiResponse(false, "Cuadrilla no encontrada");
        }

        cuadrillaRepository.updateEstado(idCuadrilla, estado);
        return new ApiResponse(true, "Estado de cuadrilla actualizado correctamente");
    }
}