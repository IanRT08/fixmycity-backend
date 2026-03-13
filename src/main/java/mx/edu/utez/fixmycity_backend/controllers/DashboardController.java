package mx.edu.utez.fixmycity_backend.controllers;

import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.services.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> obtenerEstadisticas() {

        ApiResponse response = dashboardService.obtenerEstadisticas();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/municipio")
    public ResponseEntity<ApiResponse> estadisticasPorMunicipio(
            @RequestParam int idMunicipio) {

        ApiResponse response = dashboardService.obtenerEstadisticasPorMunicipio(idMunicipio);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/periodo")
    public ResponseEntity<ApiResponse> estadisticasPorFecha(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {

        ApiResponse response = dashboardService.obtenerEstadisticasPorFecha(
                fechaInicio, fechaFin);
        return ResponseEntity.ok(response);
    }
}