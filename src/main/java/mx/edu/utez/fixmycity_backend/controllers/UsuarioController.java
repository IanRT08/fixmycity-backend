package mx.edu.utez.fixmycity_backend.controllers;

import mx.edu.utez.fixmycity_backend.dto.request.RegistroAdminRequest;
import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.security.AuthenticationHelper;
import mx.edu.utez.fixmycity_backend.services.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/users")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final AuthenticationHelper auth;

    public UsuarioController(UsuarioService usuarioService, AuthenticationHelper auth) {
        this.usuarioService = usuarioService;
        this.auth = auth;
    }

    @PostMapping("/admin")
    public ResponseEntity<ApiResponse> crearAdmin(@RequestBody @Valid RegistroAdminRequest request) {
        ApiResponse response = usuarioService.crearAdministrador(request);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> listarPorTipo(@RequestParam String tipo) {
        int idAdmin = auth.getAuthenticatedUserId();
        if (idAdmin == -1)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "No autenticado"));
        return ResponseEntity.ok(usuarioService.listarPorTipo(tipo, idAdmin));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<ApiResponse> cambiarEstado(@PathVariable int id, @RequestParam String estado) {
        ApiResponse response = usuarioService.cambiarEstadoUsuario(id, estado);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(response);
    }
}
