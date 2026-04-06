package mx.edu.utez.fixmycity_backend.controllers;

import mx.edu.utez.fixmycity_backend.dto.request.CancelacionReporteRequest;
import mx.edu.utez.fixmycity_backend.dto.request.ReporteRequest;
import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.security.AuthenticationHelper;
import mx.edu.utez.fixmycity_backend.services.ReporteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
public class ReporteController {

    private final ReporteService reporteService;
    private final AuthenticationHelper auth;

    public ReporteController(ReporteService reporteService, AuthenticationHelper auth) {
        this.reporteService = reporteService;
        this.auth = auth;
    }

    @PostMapping("/api/reports")
    public ResponseEntity<ApiResponse> crearReporte(
            @RequestPart("reporte") @Valid ReporteRequest request,
            @RequestPart(value = "fotos", required = false) List<MultipartFile> fotos) throws IOException {
        int idUsuario = auth.getAuthenticatedUserId();
        if (idUsuario == -1) return unauthorized();
        ApiResponse response = reporteService.crearReporte(idUsuario, request, fotos);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST).body(response);
    }

    @GetMapping("/api/reports")
    public ResponseEntity<ApiResponse> obtenerMisReportes(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        int idUsuario = auth.getAuthenticatedUserId();
        if (idUsuario == -1) return unauthorized();
        return ResponseEntity.ok(reporteService.obtenerMisReportes(idUsuario, page, size));
    }

    @GetMapping("/api/reports/{id}")
    public ResponseEntity<ApiResponse> obtenerReporte(@PathVariable int id) {
        int idUsuario = auth.getAuthenticatedUserId();
        if (idUsuario == -1) return unauthorized();
        ApiResponse response = reporteService.obtenerReportePorId(id, idUsuario);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(response);
    }

    @PutMapping("/api/reports/{id}")
    public ResponseEntity<ApiResponse> editarReporte(@PathVariable int id,
            @RequestBody @Valid ReporteRequest request) {
        int idUsuario = auth.getAuthenticatedUserId();
        if (idUsuario == -1) return unauthorized();
        ApiResponse response = reporteService.editarReporte(id, idUsuario, request);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(response);
    }

    @PutMapping("/api/reports/{id}/cancel")
    public ResponseEntity<ApiResponse> cancelarReporte(@PathVariable int id,
            @RequestBody @Valid CancelacionReporteRequest request) {
        int idUsuario = auth.getAuthenticatedUserId();
        if (idUsuario == -1) return unauthorized();
        ApiResponse response = reporteService.cancelarReporte(id, idUsuario, request);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(response);
    }

    @GetMapping("/api/admin/reports/{id}")
    public ResponseEntity<ApiResponse> obtenerDetalleAdmin(@PathVariable int id) {
        int idAdmin = auth.getAuthenticatedUserId();
        if (idAdmin == -1) return unauthorized();
        ApiResponse response = reporteService.obtenerDetalleAdmin(id, idAdmin);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(response);
    }

    @GetMapping("/api/admin/reports")
    public ResponseEntity<ApiResponse> listarReportesAdmin(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Integer idMunicipio,
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) String keyword) {
        int idAdmin = auth.getAuthenticatedUserId();
        if (idAdmin == -1) return unauthorized();
        return ResponseEntity.ok(reporteService.listarReportesAdmin(estado, idMunicipio, fechaInicio, fechaFin, keyword, idAdmin));
    }

    @PutMapping("/api/admin/reports/{id}/reject")
    public ResponseEntity<ApiResponse> rechazarReporte(@PathVariable int id,
            @RequestBody @Valid CancelacionReporteRequest request) {
        int idAdmin = auth.getAuthenticatedUserId();
        if (idAdmin == -1) return unauthorized();
        ApiResponse response = reporteService.rechazarReporte(id, idAdmin, request);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(response);
    }

    // NEW: Marcar duplicado — no requiere body/motivo
    @PutMapping("/api/admin/reports/{id}/duplicate")
    public ResponseEntity<ApiResponse> marcarDuplicado(@PathVariable int id) {
        int idAdmin = auth.getAuthenticatedUserId();
        if (idAdmin == -1) return unauthorized();
        ApiResponse response = reporteService.marcarDuplicado(id, idAdmin);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(response);
    }

    @GetMapping("/api/feed")
    public ResponseEntity<ApiResponse> obtenerFeed(
            @RequestParam int idMunicipio,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return ResponseEntity.ok(reporteService.obtenerFeed(idMunicipio, page, size));
    }

    @GetMapping("/api/feed/{id}")
    public ResponseEntity<ApiResponse> obtenerDetallePublico(@PathVariable int id) {
        return ResponseEntity.ok(reporteService.obtenerDetallePublico(id));
    }

    private ResponseEntity<ApiResponse> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "Usuario no autenticado"));
    }
}
