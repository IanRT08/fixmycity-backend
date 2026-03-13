package mx.edu.utez.fixmycity_backend.controllers;

import mx.edu.utez.fixmycity_backend.dto.request.LoginRequest;
import mx.edu.utez.fixmycity_backend.dto.request.RegistroCiudadanoRequest;
import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.dto.response.LoginResponse;
import mx.edu.utez.fixmycity_backend.modelos.Usuario;
import mx.edu.utez.fixmycity_backend.repositories.UsuarioRepository;
import mx.edu.utez.fixmycity_backend.security.JwtUtils;
import mx.edu.utez.fixmycity_backend.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(
            @RequestBody @Valid RegistroCiudadanoRequest request) {

        ApiResponse response = authService.registrarCiudadano(request);
        HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(
            @RequestBody @Valid LoginRequest request) {

        ApiResponse response = authService.login(request);

        if (!response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        LoginResponse loginData = (LoginResponse) response.getData();

        Optional<Usuario> usuarioOpt = usuarioRepository
                .findByNombreUsuario(request.getNombreUsuario());

        if (usuarioOpt.isPresent()) {
            String token = jwtUtils.generateToken(
                    usuarioOpt.get().getNombreUsuario(),
                    usuarioOpt.get().getTipo()
            );
            loginData.setToken(token);
        }

        return ResponseEntity.ok(response);
    }
}