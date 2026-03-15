package mx.edu.utez.fixmycity_backend.controllers;

import jakarta.validation.Valid;
import mx.edu.utez.fixmycity_backend.dto.request.ForgotPasswordRequest;
import mx.edu.utez.fixmycity_backend.dto.request.ResetPasswordRequest;
import mx.edu.utez.fixmycity_backend.dto.request.VerificarTokenRequest;
import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.services.RecuperacionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class RecuperacionController {

    private final RecuperacionService recuperacionService;

    public RecuperacionController(RecuperacionService recuperacionService) {
        this.recuperacionService = recuperacionService;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> solicitarRecuperacion(
            @RequestBody @Valid ForgotPasswordRequest request) {

        ApiResponse response = recuperacionService.solicitarRecuperacion(request);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/verify-token")
    public ResponseEntity<ApiResponse> verificarToken(
            @RequestBody @Valid VerificarTokenRequest request) {

        ApiResponse response = recuperacionService.verificarToken(request);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetearContrasenia(
            @RequestBody @Valid ResetPasswordRequest request) {

        ApiResponse response = recuperacionService.resetearContrasenia(request);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
}
