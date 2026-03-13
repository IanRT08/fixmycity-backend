package mx.edu.utez.fixmycity_backend.controllers;

import mx.edu.utez.fixmycity_backend.dto.request.CancelacionReporteRequest;
import mx.edu.utez.fixmycity_backend.dto.request.ReporteRequest;
import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.modelos.Usuario;
import mx.edu.utez.fixmycity_backend.repositories.UsuarioRepository;
import mx.edu.utez.fixmycity_backend.services.ReporteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
public class ReporteController {

    private final ReporteService reporteService;
    private final UsuarioRepository usuarioRepository;

    public ReporteController(ReporteService reporteService, UsuarioRepository usuarioRepository) {
        this.reporteService = reporteService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/api/reports")
    public ResponseEntity<ApiResponse> crearReporte(
            @RequestPart("reporte") @Valid ReporteRequest request,
            @RequestPart(value = "fotos", required = false) List<MultipartFile> fotos)
            throws IOException {

        int idUsuario = getIdUsuarioAutenticado();
        if (idUsuario == -1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Usuario no autenticado"));
        }

        ApiResponse response = reporteService.crearReporte(idUsuario, request, fotos);
        HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/api/reports")
    public ResponseEntity<ApiResponse> obtenerMisReportes() {

        int idUsuario = getIdUsuarioAutenticado();
        if (idUsuario == -1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Usuario no autenticado"));
        }

        ApiResponse response = reporteService.obtenerMisReportes(idUsuario);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/reports/{id}")
    public ResponseEntity<ApiResponse> obtenerReporte(@PathVariable int id) {

        int idUsuario = getIdUsuarioAutenticado();
        if (idUsuario == -1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Usuario no autenticado"));
        }

        ApiResponse response = reporteService.obtenerReportePorId(id, idUsuario);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    @PutMapping("/api/reports/{id}")
    public ResponseEntity<ApiResponse> editarReporte(
            @PathVariable int id,
            @RequestBody @Valid ReporteRequest request) {

        int idUsuario = getIdUsuarioAutenticado();
        if (idUsuario == -1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Usuario no autenticado"));
        }

        ApiResponse response = reporteService.editarReporte(id, idUsuario, request);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @PutMapping("/api/reports/{id}/cancel")
    public ResponseEntity<ApiResponse> cancelarReporte(
            @PathVariable int id,
            @RequestBody @Valid CancelacionReporteRequest request) {

        int idUsuario = getIdUsuarioAutenticado();
        if (idUsuario == -1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Usuario no autenticado"));
        }

        ApiResponse response = reporteService.cancelarReporte(id, idUsuario, request);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }


    @GetMapping("/api/admin/reports")
    public ResponseEntity<ApiResponse> listarReportesAdmin(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Integer idMunicipio,
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) String keyword) {

        ApiResponse response = reporteService.listarReportesAdmin(
                estado, idMunicipio, fechaInicio, fechaFin, keyword);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/api/admin/reports/{id}/reject")
    public ResponseEntity<ApiResponse> rechazarReporte(
            @PathVariable int id,
            @RequestBody @Valid CancelacionReporteRequest request) {

        int idAdmin = getIdUsuarioAutenticado();
        if (idAdmin == -1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Usuario no autenticado"));
        }

        ApiResponse response = reporteService.rechazarReporte(id, idAdmin, request);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/api/feed")
    public ResponseEntity<ApiResponse> obtenerFeed(
            @RequestParam int idMunicipio) {

        ApiResponse response = reporteService.obtenerFeed(idMunicipio);
        return ResponseEntity.ok(response);
    }

    private int getIdUsuarioAutenticado() {
        String nombreUsuario = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNombreUsuario(nombreUsuario);
        return usuarioOpt.map(Usuario::getIdUsuario).orElse(-1);
    }
}