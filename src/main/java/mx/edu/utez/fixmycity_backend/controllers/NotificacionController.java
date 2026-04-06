package mx.edu.utez.fixmycity_backend.controllers;

import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.security.AuthenticationHelper;
import mx.edu.utez.fixmycity_backend.services.NotificacionesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificacionController {

    private final NotificacionesService notificacionService;
    private final AuthenticationHelper auth;

    public NotificacionController(NotificacionesService notificacionService, AuthenticationHelper auth) {
        this.notificacionService = notificacionService;
        this.auth = auth;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> obtenerNotificaciones() {
        int id = auth.getAuthenticatedUserId();
        if (id == -1) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "No autenticado"));
        return ResponseEntity.ok(notificacionService.obtenerNotificaciones(id));
    }

    @GetMapping("/unread")
    public ResponseEntity<ApiResponse> obtenerNoLeidas() {
        int id = auth.getAuthenticatedUserId();
        if (id == -1) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "No autenticado"));
        return ResponseEntity.ok(notificacionService.obtenerNoLeidas(id));
    }

    @PutMapping("/{idNot}/read")
    public ResponseEntity<ApiResponse> marcarComoLeida(@PathVariable int idNot) {
        int id = auth.getAuthenticatedUserId();
        if (id == -1) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "No autenticado"));
        ApiResponse r = notificacionService.marcarComoLeida(idNot, id);
        return ResponseEntity.status(r.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(r);
    }
}
