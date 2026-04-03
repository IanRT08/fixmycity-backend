package mx.edu.utez.fixmycity_backend.controllers;

import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.services.PerfilService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/users")
public class PerfilController {

    private final PerfilService perfilService;

    public PerfilController(PerfilService perfilService) {
        this.perfilService = perfilService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> obtenerMiPerfil() {
        String nombreUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Usuario no autenticado"));
        }
        ApiResponse response = perfilService.obtenerPerfil(nombreUsuario);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    @PutMapping(value = "/me/photo", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse> actualizarFotoPerfil(
            @RequestPart("foto") MultipartFile foto) throws IOException {

        String nombreUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Usuario no autenticado"));
        }
        ApiResponse response = perfilService.actualizarFotoPerfil(nombreUsuario, foto);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @DeleteMapping("/me/photo")
    public ResponseEntity<ApiResponse> eliminarFotoPerfil() {
        String nombreUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Usuario no autenticado"));
        }
        ApiResponse response = perfilService.eliminarFotoPerfil(nombreUsuario);
        return ResponseEntity.ok(response);
    }
}
