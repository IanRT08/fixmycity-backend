package mx.edu.utez.fixmycity_backend.controllers;

import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.security.AuthenticationHelper;
import mx.edu.utez.fixmycity_backend.services.HistorialService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/reports")
public class HistorialController {

    private final HistorialService historialService;
    private final AuthenticationHelper auth;

    public HistorialController(HistorialService historialService, AuthenticationHelper auth) {
        this.historialService = historialService;
        this.auth = auth;
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<ApiResponse> obtenerHistorial(@PathVariable int id) {
        int idAdmin = auth.getAuthenticatedUserId();
        if (idAdmin == -1)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Usuario no autenticado"));
        ApiResponse response = historialService.obtenerHistorialAdmin(id, idAdmin);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(response);
    }
}
