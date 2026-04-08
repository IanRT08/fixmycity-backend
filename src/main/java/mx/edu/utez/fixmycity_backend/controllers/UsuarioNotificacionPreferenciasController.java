package mx.edu.utez.fixmycity_backend.controllers;

import jakarta.validation.Valid;
import mx.edu.utez.fixmycity_backend.dto.request.FcmTokenRequest;
import mx.edu.utez.fixmycity_backend.dto.request.PreferenciasNotificacionUpdateRequest;
import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.security.AuthenticationHelper;
import mx.edu.utez.fixmycity_backend.services.UsuarioNotificacionPreferenciasService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/me")
public class UsuarioNotificacionPreferenciasController {

    private final UsuarioNotificacionPreferenciasService service;
    private final AuthenticationHelper auth;

    public UsuarioNotificacionPreferenciasController(
            UsuarioNotificacionPreferenciasService service,
            AuthenticationHelper auth) {
        this.service = service;
        this.auth = auth;
    }

    @GetMapping("/notification-preferences")
    public ResponseEntity<ApiResponse> obtenerPreferencias() {
        int id = auth.getAuthenticatedUserId();
        if (id == -1) return unauthorized();
        return ResponseEntity.ok(service.obtenerPreferencias(id));
    }

    @PatchMapping(value = "/notification-preferences", consumes = "application/json")
    public ResponseEntity<ApiResponse> actualizarPreferencias(
            @Valid @RequestBody PreferenciasNotificacionUpdateRequest body) {
        int id = auth.getAuthenticatedUserId();
        if (id == -1) return unauthorized();
        ApiResponse r = service.actualizarPreferencias(id, body);
        return ResponseEntity.status(r.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(r);
    }

    @PostMapping(value = "/fcm-token", consumes = "application/json")
    public ResponseEntity<ApiResponse> registrarFcm(@Valid @RequestBody FcmTokenRequest body) {
        int id = auth.getAuthenticatedUserId();
        if (id == -1) return unauthorized();
        ApiResponse r = service.registrarTokenFcm(id, body);
        return ResponseEntity.status(r.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(r);
    }

    @DeleteMapping(value = "/fcm-token", consumes = "application/json")
    public ResponseEntity<ApiResponse> eliminarFcm(@Valid @RequestBody FcmTokenRequest body) {
        int id = auth.getAuthenticatedUserId();
        if (id == -1) return unauthorized();
        ApiResponse r = service.eliminarTokenFcm(id, body);
        return ResponseEntity.status(r.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(r);
    }

    /** Misma semántica que DELETE /fcm-token; útil si el cliente HTTP no envía body en DELETE. */
    @PostMapping(value = "/fcm-token/revoke", consumes = "application/json")
    public ResponseEntity<ApiResponse> revocarFcm(@Valid @RequestBody FcmTokenRequest body) {
        int id = auth.getAuthenticatedUserId();
        if (id == -1) return unauthorized();
        ApiResponse r = service.eliminarTokenFcm(id, body);
        return ResponseEntity.status(r.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(r);
    }

    private ResponseEntity<ApiResponse> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "No autenticado"));
    }
}
