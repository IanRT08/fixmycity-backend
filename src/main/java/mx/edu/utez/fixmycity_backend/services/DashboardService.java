package mx.edu.utez.fixmycity_backend.services;

import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private ReporteRepository reporteRepository;

    @Autowired
    private detallesReporteRepository detallesReporteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CuadrillaRepository cuadrillaRepository;

    @Autowired
    private SolicitudVoluntarioRepository solicitudVoluntarioRepository;

    public ApiResponse obtenerEstadisticas() {

        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("reportesPendientes",
                reporteRepository.findWithFilters("Pendiente", null, null, null, null).size());
        estadisticas.put("reportesAsignados",
                reporteRepository.findWithFilters("Asignado", null, null, null, null).size());
        estadisticas.put("reportesEnCamino",
                reporteRepository.findWithFilters("En camino", null, null, null, null).size());
        estadisticas.put("reportesEnCurso",
                reporteRepository.findWithFilters("En curso", null, null, null, null).size());
        estadisticas.put("reportesFinalizados",
                reporteRepository.findWithFilters("Finalizado", null, null, null, null).size());
        estadisticas.put("reportesCancelados",
                reporteRepository.findWithFilters("Cancelado", null, null, null, null).size());
        estadisticas.put("reportesRechazados",
                reporteRepository.findWithFilters("Rechazado", null, null, null, null).size());
        estadisticas.put("totalCiudadanos",
                usuarioRepository.findByTipo("ciudadano").size());
        estadisticas.put("totalVoluntarios",
                usuarioRepository.findByTipo("voluntario").size());

        estadisticas.put("cuadrillasActivas",
                cuadrillaRepository.findByEstado("activa").size());

        estadisticas.put("solicitudesPendientes",
                solicitudVoluntarioRepository.findByEstado("pendiente").size());

        return new ApiResponse(true, "Estadísticas obtenidas correctamente", estadisticas);
    }

    public ApiResponse obtenerEstadisticasPorMunicipio(int idMunicipio) {

        Map<String, Object> estadisticas = new HashMap<>();

        estadisticas.put("reportesPendientes",
                reporteRepository.findWithFilters("Pendiente", idMunicipio, null, null, null).size());
        estadisticas.put("reportesFinalizados",
                reporteRepository.findWithFilters("Finalizado", idMunicipio, null, null, null).size());
        estadisticas.put("reportesCancelados",
                reporteRepository.findWithFilters("Cancelado", idMunicipio, null, null, null).size());
        estadisticas.put("reportesEnProceso",
                reporteRepository.findWithFilters("En curso", idMunicipio, null, null, null).size()
                        + reporteRepository.findWithFilters("En camino", idMunicipio, null, null, null).size()
                        + reporteRepository.findWithFilters("Asignado", idMunicipio, null, null, null).size());

        return new ApiResponse(true, "Estadísticas del municipio obtenidas correctamente", estadisticas);
    }

    public ApiResponse obtenerEstadisticasPorFecha(String fechaInicio, String fechaFin) {

        Map<String, Object> estadisticas = new HashMap<>();

        estadisticas.put("reportesRegistrados",
                reporteRepository.findWithFilters(null, null, fechaInicio, fechaFin, null).size());
        estadisticas.put("reportesFinalizados",
                reporteRepository.findWithFilters("Finalizado", null, fechaInicio, fechaFin, null).size());
        estadisticas.put("reportesCancelados",
                reporteRepository.findWithFilters("Cancelado", null, fechaInicio, fechaFin, null).size());

        return new ApiResponse(true, "Estadísticas por período obtenidas correctamente", estadisticas);
    }
}