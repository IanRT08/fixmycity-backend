package mx.edu.utez.fixmycity_backend.services;

import mx.edu.utez.fixmycity_backend.dto.request.RegistroAdminRequest;
import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.dto.response.UsuarioResponse;
import mx.edu.utez.fixmycity_backend.modelos.Administradores;
import mx.edu.utez.fixmycity_backend.modelos.Usuario;
import mx.edu.utez.fixmycity_backend.repositories.AdministradoresRepository;
import mx.edu.utez.fixmycity_backend.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AdministradoresRepository administradorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public ApiResponse crearAdministrador(RegistroAdminRequest request) {

        if (!request.getContrasenia().equals(request.getConfirmarContrasenia())) {
            return new ApiResponse(false, "Las contraseñas no coinciden");
        }

        // Validar que el usuario no exista
        if (usuarioRepository.findByNombreUsuario(request.getNombreUsuario()).isPresent()) {
            return new ApiResponse(false, "El nombre de usuario ya está en uso");
        }

        if (usuarioRepository.findByCorreo(request.getCorreo()).isPresent()) {
            return new ApiResponse(false, "El correo ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(request.getNombreUsuario());
        usuario.setCorreo(request.getCorreo());
        usuario.setContrasenia(passwordEncoder.encode(request.getContrasenia()));
        usuario.setTipo("admin");
        usuario.setEstado("activo");
        usuarioRepository.save(usuario);

        Administradores admin = new Administradores();
        admin.setUsuario(usuario);
        administradorRepository.save(admin);

        return new ApiResponse(true, "Administrador creado exitosamente");
    }

    @Transactional
    public ApiResponse cambiarEstadoUsuario(int idUsuario, String estado) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
        if (usuarioOpt.isEmpty()) {
            return new ApiResponse(false, "Usuario no encontrado");
        }

        usuarioRepository.updateEstado(idUsuario, estado);
        return new ApiResponse(true, "Estado del usuario actualizado correctamente");
    }

    public ApiResponse listarPorTipo(String tipo) {
        List<Usuario> usuarios = usuarioRepository.findByTipo(tipo);

        List<UsuarioResponse> response = usuarios.stream()
                .map(u -> new UsuarioResponse(
                        u.getIdUsuario(),
                        u.getNombreUsuario(),
                        u.getCorreo(),
                        u.getTipo(),
                        u.getEstado(),
                        u.getMunicipio() != null ? u.getMunicipio().getNombre() : null
                ))
                .collect(Collectors.toList());

        return new ApiResponse(true, "Usuarios obtenidos correctamente", response);
    }
}