package mx.edu.utez.fixmycity_backend.controllers;

import mx.edu.utez.fixmycity_backend.dto.request.RegistroAdminRequest;
import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.modelos.Usuario;
import mx.edu.utez.fixmycity_backend.repositories.UsuarioRepository;
import mx.edu.utez.fixmycity_backend.services.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/users")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/admin")
    public ResponseEntity<ApiResponse> crearAdmin(
            @RequestBody @Valid RegistroAdminRequest request) {

        ApiResponse response = usuarioService.crearAdministrador(request);
        HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> listarPorTipo(
            @RequestParam String tipo) {

        int idAdmin = getIdUsuarioAutenticado();
        if (idAdmin == -1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Usuario no autenticado"));
        }

        ApiResponse response = usuarioService.listarPorTipo(tipo, idAdmin);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<ApiResponse> cambiarEstado(
            @PathVariable int id,
            @RequestParam String estado) {

        ApiResponse response = usuarioService.cambiarEstadoUsuario(id, estado);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    private int getIdUsuarioAutenticado() {
        String nombreUsuario = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarIdPorNombre(nombreUsuario)
                .map(o -> ((Number) o).intValue())
                .flatMap(id -> usuarioRepository.findById(id));
        return usuarioOpt.map(Usuario::getIdUsuario).orElse(-1);
    }
}