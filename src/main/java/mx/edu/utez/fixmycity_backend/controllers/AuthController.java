package mx.edu.utez.fixmycity_backend.controllers;

import mx.edu.utez.fixmycity_backend.dto.request.LoginRequest;
import mx.edu.utez.fixmycity_backend.dto.request.RegistroCiudadanoRequest;
import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.dto.response.LoginResponse;
import mx.edu.utez.fixmycity_backend.modelos.Usuario;
import mx.edu.utez.fixmycity_backend.repositories.UsuarioRepository;
import mx.edu.utez.fixmycity_backend.security.AuthenticationHelper;
import mx.edu.utez.fixmycity_backend.security.JwtUtils;
import mx.edu.utez.fixmycity_backend.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;
    private final UsuarioRepository usuarioRepository;
    private final AuthenticationHelper authenticationHelper;

    public AuthController(AuthService authService, JwtUtils jwtUtils, UsuarioRepository usuarioRepository,
                            AuthenticationHelper authenticationHelper) {
        this.authService = authService;
        this.jwtUtils = jwtUtils;
        this.usuarioRepository = usuarioRepository;
        this.authenticationHelper = authenticationHelper;
    }

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
                .buscarIdPorNombre(request.getNombreUsuario())
                .map(o -> ((Number) o).intValue())
                .flatMap(id -> usuarioRepository.findById(id));

        if (usuarioOpt.isPresent()) {
            String token = jwtUtils.generateToken(
                    usuarioOpt.get().getNombreUsuario(),
                    usuarioOpt.get().getTipo(),
                    usuarioOpt.get().getMunicipio() != null ? usuarioOpt.get().getMunicipio().getIdMunicipio() : -1
            );
            loginData.setToken(token);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Emite un JWT nuevo leyendo {@code tipo} e {@code idMunicipio} desde BD (mismo criterio que login: usuario activo).
     * Útil cuando el rol cambió en servidor (p. ej. aprobación como voluntario) y el token antiguo aún lleva {@code ciudadano}.
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse> refreshToken() {
        String nombre = authenticationHelper.getAuthenticatedUsername();
        if (nombre == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "No autenticado"));
        }
        Optional<Object> idOpt = usuarioRepository.buscarIdPorNombreYEstado(nombre, "activo");
        if (idOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Sesión no válida o cuenta inactiva"));
        }
        int id = ((Number) idOpt.get()).intValue();
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Usuario no encontrado"));
        }
        Usuario u = usuarioOpt.get();
        int idMunicipio = u.getMunicipio() != null ? u.getMunicipio().getIdMunicipio() : -1;
        String token = jwtUtils.generateToken(u.getNombreUsuario(), u.getTipo(), idMunicipio);
        LoginResponse data = new LoginResponse(
                token,
                u.getNombreUsuario(),
                u.getTipo(),
                u.getMunicipio() != null ? u.getMunicipio().getNombre() : null,
                idMunicipio
        );
        return ResponseEntity.ok(new ApiResponse(true, "Token actualizado", data));
    }
}