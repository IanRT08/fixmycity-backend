package mx.edu.utez.fixmycity_backend.controllers;

import mx.edu.utez.fixmycity_backend.dto.request.AsignacionReporteRequest;
import mx.edu.utez.fixmycity_backend.dto.request.CuadrillaRequest;
import mx.edu.utez.fixmycity_backend.dto.request.FinalizarReporteRequest;
import mx.edu.utez.fixmycity_backend.dto.request.VotacionRequest;
import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.modelos.Usuario;
import mx.edu.utez.fixmycity_backend.repositories.UsuarioRepository;
import mx.edu.utez.fixmycity_backend.services.AsignacionService;
import mx.edu.utez.fixmycity_backend.services.CuadrillaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Optional;

@RestController
public class CuadrillaController {

    @Autowired
    private CuadrillaService cuadrillaService;

    @Autowired
    private AsignacionService asignacionService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/api/admin/squads")
    public ResponseEntity<ApiResponse> crearCuadrilla(
            @RequestBody @Valid CuadrillaRequest request) {

        ApiResponse response = cuadrillaService.crearCuadrilla(request);
        HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/api/admin/squads")
    public ResponseEntity<ApiResponse> listarCuadrillas(
            @RequestParam(defaultValue = "activa") String estado) {

        ApiResponse response = cuadrillaService.listarCuadrillas(estado);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/admin/squads/volunteers")
    public ResponseEntity<ApiResponse> voluntariosDisponibles(
            @RequestParam int idMunicipio) {

        ApiResponse response = cuadrillaService.listarVoluntariosDisponibles(idMunicipio);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/api/admin/squads/{id}/estado")
    public ResponseEntity<ApiResponse> cambiarEstado(
            @PathVariable int id,
            @RequestParam String estado) {

        ApiResponse response = cuadrillaService.cambiarEstadoCuadrilla(id, estado);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/api/admin/reports/assign")
    public ResponseEntity<ApiResponse> asignarReporte(
            @RequestBody @Valid AsignacionReporteRequest request) {

        int idAdmin = getIdUsuarioAutenticado();
        if (idAdmin == -1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Usuario no autenticado"));
        }

        ApiResponse response = asignacionService.asignarReporte(request, idAdmin);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/api/squads/vote")
    public ResponseEntity<ApiResponse> votar(
            @RequestBody @Valid VotacionRequest request) {

        int idUsuario = getIdUsuarioAutenticado();
        if (idUsuario == -1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Usuario no autenticado"));
        }

        ApiResponse response = asignacionService.registrarVoto(request, idUsuario);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @PutMapping("/api/squads/reports/{idReporte}/start")
    public ResponseEntity<ApiResponse> iniciarAtencion(@PathVariable int idReporte) {

        int idUsuario = getIdUsuarioAutenticado();
        if (idUsuario == -1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Usuario no autenticado"));
        }

        ApiResponse response = asignacionService.iniciarAtencion(idReporte, idUsuario);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @PutMapping("/api/squads/reports/{idReporte}/finish")
    public ResponseEntity<ApiResponse> finalizarReporte(
            @PathVariable int idReporte,
            @RequestPart("datos") @Valid FinalizarReporteRequest request,
            @RequestPart("evidencia") MultipartFile fotoEvidencia) throws IOException {

        int idUsuario = getIdUsuarioAutenticado();
        if (idUsuario == -1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Usuario no autenticado"));
        }

        ApiResponse response = asignacionService.finalizarReporte(
                idReporte, idUsuario, request, fotoEvidencia);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    private int getIdUsuarioAutenticado() {
        String nombreUsuario = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNombreUsuario(nombreUsuario);
        return usuarioOpt.map(Usuario::getIdUsuario).orElse(-1);
    }
}