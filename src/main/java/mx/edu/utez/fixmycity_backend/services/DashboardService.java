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
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CuadrillaRepository cuadrillaRepository;

    @Autowired
    private SolicitudVoluntarioRepository solicitudVoluntarioRepository;

    public ApiResponse obtenerEstadisticas() {

        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("reportesPendientes",
                reporteRepository.findIdsWithFilters("Pendiente", null, null, null, null).size());
        estadisticas.put("reportesAsignados",
                reporteRepository.findIdsWithFilters("Asignado", null, null, null, null).size());
        estadisticas.put("reportesEnCamino",
                reporteRepository.findIdsWithFilters("En camino", null, null, null, null).size());
        estadisticas.put("reportesEnCurso",
                reporteRepository.findIdsWithFilters("En curso", null, null, null, null).size());
        estadisticas.put("reportesFinalizados",
                reporteRepository.findIdsWithFilters("Finalizado", null, null, null, null).size());
        estadisticas.put("reportesCancelados",
                reporteRepository.findIdsWithFilters("Cancelado", null, null, null, null).size());
        estadisticas.put("reportesRechazados",
                reporteRepository.findIdsWithFilters("Rechazado", null, null, null, null).size());
        estadisticas.put("totalCiudadanos",
                usuarioRepository.buscarIdsPorTipo("ciudadano").size());
        estadisticas.put("totalVoluntarios",
                usuarioRepository.buscarIdsPorTipo("voluntario").size());

        estadisticas.put("cuadrillasActivas",
                cuadrillaRepository.findIdsByEstado("activa").size());

        estadisticas.put("solicitudesPendientes",
                solicitudVoluntarioRepository.findIdsByEstado("pendiente").size());

        return new ApiResponse(true, "Estadísticas obtenidas correctamente", estadisticas);
    }

    public ApiResponse obtenerEstadisticasPorMunicipio(int idMunicipio) {

        Map<String, Object> estadisticas = new HashMap<>();

        estadisticas.put("reportesPendientes",
                reporteRepository.findIdsWithFilters("Pendiente", idMunicipio, null, null, null).size());
        estadisticas.put("reportesFinalizados",
                reporteRepository.findIdsWithFilters("Finalizado", idMunicipio, null, null, null).size());
        estadisticas.put("reportesCancelados",
                reporteRepository.findIdsWithFilters("Cancelado", idMunicipio, null, null, null).size());
        estadisticas.put("reportesEnProceso",
                reporteRepository.findIdsWithFilters("En curso", idMunicipio, null, null, null).size()
                        + reporteRepository.findIdsWithFilters("En camino", idMunicipio, null, null, null).size()
                        + reporteRepository.findIdsWithFilters("Asignado", idMunicipio, null, null, null).size());

        return new ApiResponse(true, "Estadísticas del municipio obtenidas correctamente", estadisticas);
    }

    public ApiResponse obtenerEstadisticasPorFecha(String fechaInicio, String fechaFin) {

        Map<String, Object> estadisticas = new HashMap<>();

        estadisticas.put("reportesRegistrados",
                reporteRepository.findIdsWithFilters(null, null, fechaInicio, fechaFin, null).size());
        estadisticas.put("reportesFinalizados",
                reporteRepository.findIdsWithFilters("Finalizado", null, fechaInicio, fechaFin, null).size());
        estadisticas.put("reportesCancelados",
                reporteRepository.findIdsWithFilters("Cancelado", null, fechaInicio, fechaFin, null).size());

        return new ApiResponse(true, "Estadísticas por período obtenidas correctamente", estadisticas);
    }
}