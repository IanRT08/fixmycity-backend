package mx.edu.utez.fixmycity_backend.controllers;

import mx.edu.utez.fixmycity_backend.dto.request.SolicitudVoluntarioRequest;
import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.modelos.Usuario;
import mx.edu.utez.fixmycity_backend.repositories.UsuarioRepository;
import mx.edu.utez.fixmycity_backend.services.VoluntarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
public class VoluntarioController {

    private final VoluntarioService voluntarioService;
    private final UsuarioRepository usuarioRepository;

    public VoluntarioController(VoluntarioService voluntarioService, UsuarioRepository usuarioRepository) {
        this.voluntarioService = voluntarioService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/api/users/volunteer-request")
    public ResponseEntity<ApiResponse> solicitarVoluntario(
            @RequestBody @Valid SolicitudVoluntarioRequest request) {


        String nombreUsuario = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNombreUsuario(nombreUsuario);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Usuario no autenticado"));
        }

        ApiResponse response = voluntarioService.solicitarVoluntario(
                usuarioOpt.get().getIdUsuario(), request);
        HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/api/admin/users/volunteer-requests")
    public ResponseEntity<ApiResponse> listarSolicitudes() {

        ApiResponse response = voluntarioService.listarSolicitudesPendientes();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/api/admin/users/volunteer-requests/{id}")
    public ResponseEntity<ApiResponse> responderSolicitud(
            @PathVariable int id,
            @RequestParam String decision) {

        ApiResponse response = voluntarioService.responderSolicitud(id, decision);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
}