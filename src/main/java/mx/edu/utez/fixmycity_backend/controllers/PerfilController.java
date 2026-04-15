package mx.edu.utez.fixmycity_backend.controllers;

import jakarta.validation.Valid;
import mx.edu.utez.fixmycity_backend.dto.request.PerfilUpdateRequest;
import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.security.AuthenticationHelper;
import mx.edu.utez.fixmycity_backend.services.PerfilService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/users")
public class PerfilController {

    private final PerfilService perfilService;
    private final AuthenticationHelper auth;

    public PerfilController(PerfilService perfilService, AuthenticationHelper auth) {
        this.perfilService = perfilService;
        this.auth = auth;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> obtenerMiPerfil() {
        String nombre = auth.getAuthenticatedUsername();
        if (nombre == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "No autenticado"));
        ApiResponse r = perfilService.obtenerPerfil(nombre);
        return ResponseEntity.status(r.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(r);
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse> actualizarPerfil(@RequestBody @Valid PerfilUpdateRequest request) {
        String nombre = auth.getAuthenticatedUsername();
        if (nombre == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "No autenticado"));
        ApiResponse r = perfilService.actualizarPerfil(nombre, request);
        return ResponseEntity.status(r.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(r);
    }

    @PutMapping(value = "/me/photo", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse> actualizarFoto(@RequestPart("foto") MultipartFile foto) throws IOException {
        String nombre = auth.getAuthenticatedUsername();
        if (nombre == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "No autenticado"));
        ApiResponse r = perfilService.actualizarFotoPerfil(nombre, foto);
        return ResponseEntity.status(r.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(r);
    }

    @DeleteMapping("/me/photo")
    public ResponseEntity<ApiResponse> eliminarFoto() {
        String nombre = auth.getAuthenticatedUsername();
        if (nombre == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "No autenticado"));
        return ResponseEntity.ok(perfilService.eliminarFotoPerfil(nombre));
    }
}
