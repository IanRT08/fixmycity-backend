package mx.edu.utez.fixmycity_backend.controllers;

import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.modelos.Usuario;
import mx.edu.utez.fixmycity_backend.repositories.UsuarioRepository;
import mx.edu.utez.fixmycity_backend.services.HistorialService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/admin/reports")
public class HistorialController {

    private final HistorialService historialService;
    private final UsuarioRepository usuarioRepository;

    public HistorialController(HistorialService historialService, UsuarioRepository usuarioRepository) {
        this.historialService = historialService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<ApiResponse> obtenerHistorial(@PathVariable int id) {

        int idAdmin = getIdUsuarioAutenticado();
        if (idAdmin == -1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Usuario no autenticado"));
        }

        ApiResponse response = historialService.obtenerHistorialAdmin(id, idAdmin);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
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
