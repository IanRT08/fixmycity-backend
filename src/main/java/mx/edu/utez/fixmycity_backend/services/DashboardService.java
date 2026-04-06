package mx.edu.utez.fixmycity_backend.services;

import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.repositories.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final ReporteRepository reporteRepository;
    private final UsuarioRepository usuarioRepository;
    private final CuadrillaRepository cuadrillaRepository;
    private final SolicitudVoluntarioRepository solicitudVoluntarioRepository;

    public DashboardService(ReporteRepository reporteRepository,
                            UsuarioRepository usuarioRepository,
                            CuadrillaRepository cuadrillaRepository,
                            SolicitudVoluntarioRepository solicitudVoluntarioRepository) {
        this.reporteRepository = reporteRepository;
        this.usuarioRepository = usuarioRepository;
        this.cuadrillaRepository = cuadrillaRepository;
        this.solicitudVoluntarioRepository = solicitudVoluntarioRepository;
    }

    // SuperAdmin: all stats without municipality filter
    public ApiResponse obtenerEstadisticas() {
        return obtenerEstadisticasInternas(null);
    }

    // Admin: stats filtered by municipality
    public ApiResponse obtenerEstadisticasPorMunicipio(int idMunicipio) {
        return obtenerEstadisticasInternas(idMunicipio);
    }

    private ApiResponse obtenerEstadisticasInternas(Integer idMunicipio) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("reportesPendientes", reporteRepository.countByEstadoAndMunicipio("Pendiente", idMunicipio));
        stats.put("reportesAsignados", reporteRepository.countByEstadoAndMunicipio("Asignado", idMunicipio));
        stats.put("reportesEnCamino", reporteRepository.countByEstadoAndMunicipio("En camino", idMunicipio));
        stats.put("reportesEnCurso", reporteRepository.countByEstadoAndMunicipio("En curso", idMunicipio));
        stats.put("reportesFinalizados", reporteRepository.countByEstadoAndMunicipio("Finalizado", idMunicipio));
        stats.put("reportesCancelados", reporteRepository.countByEstadoAndMunicipio("Cancelado", idMunicipio));
        stats.put("reportesRechazados", reporteRepository.countByEstadoAndMunicipio("Rechazado", idMunicipio));
        stats.put("reportesDuplicados", reporteRepository.countByEstadoAndMunicipio("Duplicado", idMunicipio));
        stats.put("reportesEnProceso", reporteRepository.countReportesEnProceso(idMunicipio));

        stats.put("totalCiudadanos", usuarioRepository.buscarIdsPorTipo("ciudadano").size());
        stats.put("totalVoluntarios", usuarioRepository.buscarIdsPorTipo("voluntario").size());
        stats.put("cuadrillasActivas", cuadrillaRepository.countCuadrillasActivas(idMunicipio));
        stats.put("solicitudesPendientes", idMunicipio != null
                ? solicitudVoluntarioRepository.findIdsByEstadoAndMunicipio("pendiente", idMunicipio).size()
                : solicitudVoluntarioRepository.findIdsByEstado("pendiente").size());

        return new ApiResponse(true, "Estadísticas obtenidas correctamente", stats);
    }

    public ApiResponse obtenerCuadrillas(Integer idMunicipio) {
        Map<String, Object> data = new HashMap<>();
        data.put("cuadrillasLibres", cuadrillaRepository.countCuadrillasLibres(idMunicipio));
        data.put("cuadrillasAsignadas", cuadrillaRepository.countCuadrillasAsignadas(idMunicipio));
        data.put("cuadrillasActivas", cuadrillaRepository.countCuadrillasActivas(idMunicipio));
        return new ApiResponse(true, "Conteo de cuadrillas obtenido correctamente", data);
    }

    public ApiResponse obtenerReportesHoy(Integer idMunicipio) {
        int count = reporteRepository.countReportesHoy(idMunicipio);
        return new ApiResponse(true, "Reportes de hoy obtenidos", Map.of("reportesHoy", count));
    }

    public ApiResponse obtenerReportesActivosPorMunicipio() {
        List<Object[]> rows = reporteRepository.countReportesActivosPorMunicipio();
        List<Map<String, Object>> data = rows.stream().map(row -> {
            Map<String, Object> item = new HashMap<>();
            item.put("municipio", row[0]);
            item.put("count", ((Number) row[1]).intValue());
            return item;
        }).toList();
        return new ApiResponse(true, "Reportes activos por municipio", data);
    }

    public ApiResponse obtenerEstadisticasPorFecha(String fechaInicio, String fechaFin) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("reportesRegistrados", reporteRepository.countByEstadoAndFecha(null, fechaInicio, fechaFin));
        stats.put("reportesFinalizados", reporteRepository.countByEstadoAndFecha("Finalizado", fechaInicio, fechaFin));
        stats.put("reportesCancelados", reporteRepository.countByEstadoAndFecha("Cancelado", fechaInicio, fechaFin));
        return new ApiResponse(true, "Estadísticas por período", stats);
    }
}
