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
import java.util.Base64;
import java.util.stream.Collectors;

@Service
public class CuadrillaService {

    @Autowired private CuadrillaRepository cuadrillaRepository;
    @Autowired private miembrosCuadrillaRepository miembrosCuadrillaRepository;
    @Autowired private VoluntarioRepository voluntarioRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private MunicipioRepository municipioRepository;
    @Autowired private reporteAsignadoCuadrillaRepository reporteAsignadoCuadrillaRepository;
    @Autowired private detallesReporteRepository detallesReporteRepository;
    @Autowired private reporteFinalizadoRepository reporteFinalizadoRepository;

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

    public ApiResponse listarCuadrillas(String estado, int idAdmin) {
        Optional<Usuario> adminOpt = usuarioRepository.findById(idAdmin);
        if (adminOpt.isEmpty()) return new ApiResponse(false, "Administrador no encontrado");
        Usuario admin = adminOpt.get();

        List<Object> ids;
        boolean todas = "todas".equalsIgnoreCase(estado);
        if (admin.getTipo().equals("superadmin")) {
            ids = todas ? cuadrillaRepository.findAllIds() : cuadrillaRepository.findIdsByEstado(estado);
        } else {
            if (admin.getMunicipio() == null)
                return new ApiResponse(false, "No se pudo obtener el municipio del administrador");
            int idMun = admin.getMunicipio().getIdMunicipio();
            ids = todas ? cuadrillaRepository.findAllIdsByMunicipio(idMun) : cuadrillaRepository.findIdsByEstadoAndMunicipio(estado, idMun);
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
                    CuadrillaResponse cr = new CuadrillaResponse(
                            c.getIdCuadrilla(), c.getNombreCuadrilla(),
                            c.getIdMunicipio().getIdMunicipio(),
                            c.getIdMunicipio().getNombre(),
                            c.getVoluntario().getUsuario().getNombreUsuario(),
                            c.getEstado(), nombres);
                    List<reporteAsignadoCuadrilla> activos = reporteAsignadoCuadrillaRepository.findActiveByCuadrilla(c.getIdCuadrilla());
                    if (!activos.isEmpty()) {
                        reporteAsignadoCuadrilla rac = activos.get(0);
                        int idRep = rac.getIdReporte().getIdReporte();
                        cr.setReporteActualId(idRep);
                        cr.setReporteActualTitulo(rac.getIdReporte().getTitulo());
                        detallesReporteRepository.findByReporte(idRep)
                                .ifPresent(d -> cr.setReporteActualEstado(d.getEstado()));
                    }
                    reporteFinalizadoRepository.findLastByCuadrilla(c.getIdCuadrilla()).ifPresent(rf -> {
                        cr.setReporteFinalizadoId(rf.getIdReporte().getIdReporte());
                        cr.setReporteFinalizadoTitulo(rf.getIdReporte().getTitulo());
                        cr.setReporteFinalizadoComentario(rf.getComentarios());
                        if (rf.getFotoEvidencia() != null) {
                            cr.setReporteFinalizadoFotoBase64(Base64.getEncoder().encodeToString(rf.getFotoEvidencia()));
                        }
                    });
                    return cr;
                }).collect(Collectors.toList());

        return new ApiResponse(true, "Cuadrillas obtenidas correctamente", response);
    }

@Transactional(readOnly = true)
    // Returns list of {idUsuario, nombreUsuario} for voluntarios without active squad
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
    public ApiResponse editarMiembrosCuadrilla(int idCuadrilla, CuadrillaRequest request) {
        Optional<Cuadrilla> cuadrillaOpt = cuadrillaRepository.findById(idCuadrilla);
        if (cuadrillaOpt.isEmpty()) return new ApiResponse(false, "Cuadrilla no encontrada");
        if (request.getIdMiembros().size() != 5)
            return new ApiResponse(false, "Deben ser exactamente 5 integrantes");
        if (!request.getIdMiembros().contains(request.getIdLider()))
            return new ApiResponse(false, "El líder debe ser uno de los 5 integrantes");
        for (Integer idVol : request.getIdMiembros()) {
            if (voluntarioRepository.findById(idVol).isEmpty())
                return new ApiResponse(false, "El voluntario con id " + idVol + " no existe");
        }
        Optional<Voluntario> liderOpt = voluntarioRepository.findById(request.getIdLider());
        if (liderOpt.isEmpty()) return new ApiResponse(false, "El líder seleccionado no es válido");

        Cuadrilla cuadrilla = cuadrillaOpt.get();
        miembrosCuadrillaRepository.deleteAllByCuadrilla(idCuadrilla);
        cuadrilla.setVoluntario(liderOpt.get());
        cuadrillaRepository.save(cuadrilla);
        for (Integer idVol : request.getIdMiembros()) {
            Voluntario voluntario = voluntarioRepository.findById(idVol).get();
            miembrosCuadrilla miembro = new miembrosCuadrilla();
            miembro.setCuadrilla(cuadrilla);
            miembro.setVoluntario(voluntario);
            miembro.setTipo(idVol.equals(request.getIdLider()) ? "lider" : "voluntario");
            miembrosCuadrillaRepository.save(miembro);
        }
        return new ApiResponse(true, "Integrantes de cuadrilla actualizados correctamente");
    }

    @Transactional(readOnly = true)
    public ApiResponse listarVoluntariosParaEdicion(int idMunicipio, int idCuadrilla) {
        List<Object> ids = voluntarioRepository.findAvailableIdsByMunicipioForEdit(idMunicipio, idCuadrilla);
        List<Map<String, Object>> result = ids.stream()
                .map(o -> ((Number) o).intValue())
                .map(id -> voluntarioRepository.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .map(v -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("idUsuario", v.getIdVoluntario());
                    m.put("nombreUsuario", v.getUsuario().getNombreUsuario());
                    m.put("tipo", "voluntario");
                    return m;
                }).collect(Collectors.toList());
        return new ApiResponse(true, "Voluntarios obtenidos correctamente", result);
    }

    @Transactional
    public ApiResponse cambiarEstadoCuadrilla(int idCuadrilla, String estado) {
        if (cuadrillaRepository.findById(idCuadrilla).isEmpty())
            return new ApiResponse(false, "Cuadrilla no encontrada");
        cuadrillaRepository.updateEstado(idCuadrilla, estado);
        return new ApiResponse(true, "Estado de cuadrilla actualizado correctamente");
    }
}
