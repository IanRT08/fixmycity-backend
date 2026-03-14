package mx.edu.utez.fixmycity_backend.services;

import mx.edu.utez.fixmycity_backend.dto.request.LoginRequest;
import mx.edu.utez.fixmycity_backend.dto.request.RegistroCiudadanoRequest;
import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.dto.response.LoginResponse;
import mx.edu.utez.fixmycity_backend.modelos.Municipios;
import mx.edu.utez.fixmycity_backend.modelos.Usuario;
import mx.edu.utez.fixmycity_backend.repositories.MunicipioRepository;
import mx.edu.utez.fixmycity_backend.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Optional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final MunicipioRepository municipioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository, 
                      MunicipioRepository municipioRepository,
                      PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.municipioRepository = municipioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public ApiResponse registrarCiudadano(RegistroCiudadanoRequest request) {

        if (!request.getContrasenia().equals(request.getConfirmarContrasenia())) {
            return new ApiResponse(false, "Las contraseñas no coinciden");
        }
        if (usuarioRepository.buscarIdPorNombre(request.getNombreUsuario()).isPresent()) {
            return new ApiResponse(false, "El nombre de usuario ya está en uso");
        }
        if (usuarioRepository.buscarIdPorCorreo(request.getCorreo()).isPresent()) {
            return new ApiResponse(false, "El correo ya está registrado");
        }
        if (!esMayorDeEdad(request.getFechaNacimiento())) {
            return new ApiResponse(false, "Debes ser mayor de edad para registrarte");
        }

        Optional<Municipios> municipio = municipioRepository.findById(request.getIdMunicipio());
        if (municipio.isEmpty()) {
            return new ApiResponse(false, "El municipio seleccionado no existe");
        }
        if (!municipio.get().getEstado().equals("Activo")) {
            return new ApiResponse(false, "El municipio seleccionado no está disponible");
        }

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(request.getNombreUsuario());
        usuario.setCorreo(request.getCorreo());
        usuario.setContrasenia(passwordEncoder.encode(request.getContrasenia()));
        usuario.setFechaNacimiento(request.getFechaNacimiento());
        usuario.setMunicipio(municipio.get()); // getIdMunicipio() en el modelo
        usuario.setTipo("ciudadano");
        usuario.setEstado("activo");

        usuarioRepository.save(usuario);
        return new ApiResponse(true, "Registro exitoso");
    }

    // Módulo 1.2 - Inicio de sesión (ciudadano, voluntario, administrador)
    public ApiResponse login(LoginRequest request) {

        Optional<Object> idOpt = usuarioRepository.buscarIdPorNombreYEstado(request.getNombreUsuario(), "activo");
        if (idOpt.isEmpty()) {
            return new ApiResponse(false, "Usuario no encontrado");
        }
        int id = ((Number) idOpt.get()).intValue();
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) {
            return new ApiResponse(false, "Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        if (!passwordEncoder.matches(request.getContrasenia(), usuario.getContrasenia())) {
            return new ApiResponse(false, "Contraseña incorrecta");
        }

        LoginResponse response = new LoginResponse(
                null,
                usuario.getNombreUsuario(),
                usuario.getTipo(),
                usuario.getMunicipio() != null ? usuario.getMunicipio().getNombre() : null
        );

        return new ApiResponse(true, "Inicio de sesión exitoso", response);
    }

    private boolean esMayorDeEdad(java.util.Date fechaNacimiento) {
        LocalDate nacimiento = fechaNacimiento.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return Period.between(nacimiento, LocalDate.now()).getYears() >= 18;
    }
}