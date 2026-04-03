package mx.edu.utez.fixmycity_backend.controllers;

import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.security.JwtUtils;
import mx.edu.utez.fixmycity_backend.services.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final JwtUtils jwtUtils;

    public DashboardController(DashboardService dashboardService, JwtUtils jwtUtils) {
        this.dashboardService = dashboardService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> obtenerEstadisticas(
            @RequestParam(required = false) Integer idMunicipio) {
        ApiResponse response = idMunicipio != null
                ? dashboardService.obtenerEstadisticasPorMunicipio(idMunicipio)
                : dashboardService.obtenerEstadisticas();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cuadrillas")
    public ResponseEntity<ApiResponse> obtenerCuadrillas(
            @RequestParam(required = false) Integer idMunicipio) {
        return ResponseEntity.ok(dashboardService.obtenerCuadrillas(idMunicipio));
    }

    @GetMapping("/municipio")
    public ResponseEntity<ApiResponse> obtenerMunicipioAdmin(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        int idMunicipio = jwtUtils.getIdMunicipioFromToken(token);
        return ResponseEntity.ok(new ApiResponse(true, "Municipio obtenido", Map.of("idMunicipio", idMunicipio)));
    }

    @GetMapping("/today")
    public ResponseEntity<ApiResponse> obtenerReportesHoy(
            @RequestParam(required = false) Integer idMunicipio) {
        return ResponseEntity.ok(dashboardService.obtenerReportesHoy(idMunicipio));
    }

    @GetMapping("/municipios")
    public ResponseEntity<ApiResponse> obtenerReportesActivosPorMunicipio() {
        return ResponseEntity.ok(dashboardService.obtenerReportesActivosPorMunicipio());
    }

    @GetMapping("/periodo")
    public ResponseEntity<ApiResponse> estadisticasPorFecha(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {
        return ResponseEntity.ok(dashboardService.obtenerEstadisticasPorFecha(fechaInicio, fechaFin));
    }
}