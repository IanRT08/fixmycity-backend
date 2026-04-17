package mx.edu.utez.fixmycity_backend.services;

import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.dto.response.ReportePageResponse;
import mx.edu.utez.fixmycity_backend.dto.response.ReporteResponse;
import mx.edu.utez.fixmycity_backend.dto.response.SquadMiCuadrillaResponse;
import mx.edu.utez.fixmycity_backend.dto.response.SquadMiembroResponse;
import mx.edu.utez.fixmycity_backend.modelos.Cuadrilla;
import mx.edu.utez.fixmycity_backend.modelos.miembrosCuadrilla;
import mx.edu.utez.fixmycity_backend.repositories.CuadrillaRepository;
import mx.edu.utez.fixmycity_backend.repositories.ReporteRepository;
import mx.edu.utez.fixmycity_backend.repositories.VoluntarioRepository;
import mx.edu.utez.fixmycity_backend.repositories.VotacionRepository;
import mx.edu.utez.fixmycity_backend.repositories.miembrosCuadrillaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Listado de asignaciones a cuadrilla, detalle para miembros y contexto "mi cuadrilla" (app voluntario).
 */
@Service
public class SquadVolunteerService {

    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> ESTADOS_VALIDOS = Set.of(
            "Asignado", "En camino", "En curso", "Finalizado", "Pendiente", "Cancelado", "Duplicado");

    private final VoluntarioRepository voluntarioRepository;
    private final miembrosCuadrillaRepository miembrosCuadrillaRepository;
    private final ReporteRepository reporteRepository;
    private final CuadrillaRepository cuadrillaRepository;
    private final ReporteService reporteService;
    private final VotacionRepository votacionRepository;

    public SquadVolunteerService(VoluntarioRepository voluntarioRepository,
                                 miembrosCuadrillaRepository miembrosCuadrillaRepository,
                                 ReporteRepository reporteRepository,
                                 CuadrillaRepository cuadrillaRepository,
                                 ReporteService reporteService,
                                 VotacionRepository votacionRepository) {
        this.voluntarioRepository = voluntarioRepository;
        this.miembrosCuadrillaRepository = miembrosCuadrillaRepository;
        this.reporteRepository = reporteRepository;
        this.cuadrillaRepository = cuadrillaRepository;
        this.reporteService = reporteService;
        this.votacionRepository = votacionRepository;
    }

    /**
     * Reportes en los que el voluntario participa vía su cuadrilla (asignación activa o filtro por estado).
     * Sin {@code estado}: solo {@code Asignado}, {@code En camino}, {@code En curso}.
     */
    @Transactional(readOnly = true)
    public ApiResponse listarAsignaciones(int idUsuario, Integer page, Integer size,
                                          String estado, Integer idCuadrillaParam) {
        Optional<Integer> idVolOpt = idVoluntarioDeUsuario(idUsuario);
        if (idVolOpt.isEmpty()) {
            return new ApiResponse(false, "No eres voluntario");
        }
        int idVoluntario = idVolOpt.get();

        int idCuadrillaFiltro = idCuadrillaParam != null && idCuadrillaParam > 0 ? idCuadrillaParam : 0;
        if (idCuadrillaFiltro > 0 && !miembroDeCuadrilla(idVoluntario, idCuadrillaFiltro)) {
            return new ApiResponse(false, "No perteneces a esa cuadrilla");
        }

        int filtrarActivo = 1;
        String estadoExacto = "Asignado";
        if (estado != null && !estado.isBlank()) {
            String e = estado.trim();
            if (!ESTADOS_VALIDOS.contains(e)) {
                return new ApiResponse(false, "Estado no válido");
            }
            filtrarActivo = 0;
            estadoExacto = e;
        }

        if (size == null) {
            List<Object> ids = reporteRepository.findSquadAssignmentIdsUnpaged(
                    idVoluntario, idCuadrillaFiltro, filtrarActivo, estadoExacto);
            List<ReporteResponse> content = ids.stream()
                    .map(o -> ((Number) o).intValue())
                    .map(reporteService::construirListadoReporte)
                    .flatMap(Optional::stream)
                    .collect(Collectors.toList());
            return new ApiResponse(true, "Asignaciones obtenidas correctamente", content);
        }

        int p = page != null ? Math.max(0, page) : 0;
        int s = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        long total = reporteRepository.countSquadAssignments(
                idVoluntario, idCuadrillaFiltro, filtrarActivo, estadoExacto);
        List<Object> ids = reporteRepository.findSquadAssignmentIdsPaged(
                idVoluntario, idCuadrillaFiltro, filtrarActivo, estadoExacto, p * s, s);
        List<ReporteResponse> content = ids.stream()
                .map(o -> ((Number) o).intValue())
                .map(reporteService::construirListadoReporte)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
        ReportePageResponse pageResponse = reporteService.toPageResponse(content, total, p, s);
        return new ApiResponse(true, "Asignaciones obtenidas correctamente", pageResponse);
    }

    /** Detalle de reporte para miembro de la cuadrilla asignada (mismo shape que detalle con fotos). */
    @Transactional(readOnly = true)
    public ApiResponse obtenerReporteCuadrilla(int idReporte, int idUsuario) {
        Optional<Integer> idVolOpt = idVoluntarioDeUsuario(idUsuario);
        if (idVolOpt.isEmpty()) {
            return new ApiResponse(false, "No eres voluntario");
        }
        int idVoluntario = idVolOpt.get();
        if (miembrosCuadrillaRepository.findByReporteAndVoluntario(idReporte, idVoluntario).isEmpty()) {
            return new ApiResponse(false, "No tienes permiso para ver este reporte");
        }
        Optional<ReporteResponse> body = reporteService.construirDetalleReporteConFotos(idReporte);
        if (body.isEmpty()) {
            return new ApiResponse(false, "Reporte no encontrado");
        }
        ReporteResponse reporte = body.get();
        boolean yaVoto = votacionRepository.findByReporteAndVoluntario(idReporte, idVoluntario).isPresent();
        reporte.setYaVote(yaVoto);
        return new ApiResponse(true, "Reporte obtenido correctamente", reporte);
    }

    /** Miembros de la cuadrilla asignada al reporte, con su estado de voto ("aceptar") para dicho reporte. */
    @Transactional(readOnly = true)
    public ApiResponse obtenerMiembrosConVotos(int idReporte, int idUsuario) {
        Optional<Integer> idVolOpt = idVoluntarioDeUsuario(idUsuario);
        if (idVolOpt.isEmpty()) {
            return new ApiResponse(false, "No eres voluntario");
        }
        int idVoluntario = idVolOpt.get();
        if (miembrosCuadrillaRepository.findByReporteAndVoluntario(idReporte, idVoluntario).isEmpty()) {
            return new ApiResponse(false, "No tienes permiso para ver este reporte");
        }
        List<miembrosCuadrilla> todos = miembrosCuadrillaRepository.findAllByReporte(idReporte);
        List<SquadMiembroResponse> lista = todos.stream().map(m -> {
            int idVol = m.getVoluntario().getIdVoluntario();
            boolean yaVoto = votacionRepository.findByReporteAndVoluntario(idReporte, idVol).isPresent();
            SquadMiembroResponse r = new SquadMiembroResponse(
                    m.getVoluntario().getUsuario().getIdUsuario(),
                    m.getVoluntario().getUsuario().getNombreUsuario(),
                    m.getTipo());
            r.setYaVoto(yaVoto);
            return r;
        }).collect(Collectors.toList());
        return new ApiResponse(true, "Miembros obtenidos correctamente", lista);
    }

    /** Primera cuadrilla en la que el voluntario figura como miembro + listado de miembros. */
    @Transactional(readOnly = true)
    public ApiResponse obtenerMiCuadrilla(int idUsuario) {
        Optional<Integer> idVolOpt = idVoluntarioDeUsuario(idUsuario);
        if (idVolOpt.isEmpty()) {
            return new ApiResponse(false, "No eres voluntario");
        }
        List<miembrosCuadrilla> filas = miembrosCuadrillaRepository.findByVoluntario_IdVoluntario(idVolOpt.get());
        if (filas.isEmpty()) {
            return new ApiResponse(false, "No perteneces a ninguna cuadrilla");
        }
        int idCuadrilla = filas.stream()
                .mapToInt(m -> m.getCuadrilla().getIdCuadrilla())
                .min()
                .orElseThrow();

        Optional<Cuadrilla> cOpt = cuadrillaRepository.findById(idCuadrilla);
        if (cOpt.isEmpty()) {
            return new ApiResponse(false, "Cuadrilla no encontrada");
        }
        Cuadrilla c = cOpt.get();
        List<miembrosCuadrilla> miembros = miembrosCuadrillaRepository.findByCuadrilla(idCuadrilla);
        List<SquadMiembroResponse> lista = miembros.stream()
                .map(m -> new SquadMiembroResponse(
                        m.getVoluntario().getUsuario().getIdUsuario(),
                        m.getVoluntario().getUsuario().getNombreUsuario(),
                        m.getTipo()))
                .collect(Collectors.toList());

        SquadMiCuadrillaResponse dto = new SquadMiCuadrillaResponse(
                c.getIdCuadrilla(),
                c.getNombreCuadrilla(),
                c.getEstado(),
                lista.isEmpty() ? Collections.emptyList() : lista);
        return new ApiResponse(true, "Cuadrilla obtenida correctamente", dto);
    }

    private Optional<Integer> idVoluntarioDeUsuario(int idUsuario) {
        return voluntarioRepository.findIdByUsuario(idUsuario)
                .map(o -> ((Number) o).intValue());
    }

    private boolean miembroDeCuadrilla(int idVoluntario, int idCuadrilla) {
        return miembrosCuadrillaRepository.findByCuadrilla(idCuadrilla).stream()
                .anyMatch(m -> m.getVoluntario().getIdVoluntario() == idVoluntario);
    }
}
