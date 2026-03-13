package mx.edu.utez.fixmycity_backend.controllers;

import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.modelos.Usuario;
import mx.edu.utez.fixmycity_backend.repositories.UsuarioRepository;
import mx.edu.utez.fixmycity_backend.services.NotificacionesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
public class NotificacionController {

    private final NotificacionesService notificacionService;
    private final UsuarioRepository usuarioRepository;

    public NotificacionController(NotificacionesService notificacionService, UsuarioRepository usuarioRepository) {
        this.notificacionService = notificacionService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> obtenerNotificaciones() {

        int idUsuario = getIdUsuarioAutenticado();
        if (idUsuario == -1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Usuario no autenticado"));
        }

        ApiResponse response = notificacionService.obtenerNotificaciones(idUsuario);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread")
    public ResponseEntity<ApiResponse> obtenerNoLeidas() {

        int idUsuario = getIdUsuarioAutenticado();
        if (idUsuario == -1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Usuario no autenticado"));
        }

        ApiResponse response = notificacionService.obtenerNoLeidas(idUsuario);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse> marcarComoLeida(@PathVariable int id) {

        int idUsuario = getIdUsuarioAutenticado();
        if (idUsuario == -1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Usuario no autenticado"));
        }

        ApiResponse response = notificacionService.marcarComoLeida(id, idUsuario);
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