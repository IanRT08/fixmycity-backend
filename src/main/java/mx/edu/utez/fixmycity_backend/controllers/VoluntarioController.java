package mx.edu.utez.fixmycity_backend.controllers;

import mx.edu.utez.fixmycity_backend.dto.request.SolicitudVoluntarioRequest;
import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.security.AuthenticationHelper;
import mx.edu.utez.fixmycity_backend.services.VoluntarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
public class VoluntarioController {

    private final VoluntarioService voluntarioService;
    private final AuthenticationHelper auth;

    public VoluntarioController(VoluntarioService voluntarioService, AuthenticationHelper auth) {
        this.voluntarioService = voluntarioService;
        this.auth = auth;
    }

    @PostMapping("/api/users/volunteer-request")
    public ResponseEntity<ApiResponse> solicitarVoluntario(@RequestBody @Valid SolicitudVoluntarioRequest request) {
        int idUsuario = auth.getAuthenticatedUserId();
        if (idUsuario == -1)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "No autenticado"));
        ApiResponse response = voluntarioService.solicitarVoluntario(idUsuario, request);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST).body(response);
    }

    @GetMapping("/api/admin/users/volunteer-requests")
    public ResponseEntity<ApiResponse> listarSolicitudes() {
        int idAdmin = auth.getAuthenticatedUserId();
        if (idAdmin == -1)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "No autenticado"));
        return ResponseEntity.ok(voluntarioService.listarSolicitudesPendientes(idAdmin));
    }

    @PutMapping("/api/admin/users/volunteer-requests/{id}")
    public ResponseEntity<ApiResponse> responderSolicitud(@PathVariable int id, @RequestParam String decision) {
        ApiResponse response = voluntarioService.responderSolicitud(id, decision);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(response);
    }
}
