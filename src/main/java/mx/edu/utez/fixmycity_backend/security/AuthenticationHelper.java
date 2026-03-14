package mx.edu.utez.fixmycity_backend.security;

import mx.edu.utez.fixmycity_backend.modelos.Usuario;
import mx.edu.utez.fixmycity_backend.repositories.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthenticationHelper {

    private final UsuarioRepository usuarioRepository;

    public AuthenticationHelper(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Obtiene el ID del usuario autenticado actualmente
     * @return ID del usuario o -1 si no está autenticado
     */
    public int getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return -1;
        }

        String nombreUsuario = authentication.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarIdPorNombre(nombreUsuario)
                .map(o -> ((Number) o).intValue())
                .flatMap(id -> usuarioRepository.findById(id));
        return usuarioOpt.map(Usuario::getIdUsuario).orElse(-1);
    }

    /**
     * Obtiene el usuario autenticado actualmente
     * @return Optional con el usuario o vacío si no está autenticado
     */
    public Optional<Usuario> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        String nombreUsuario = authentication.getName();
        return usuarioRepository.buscarIdPorNombre(nombreUsuario)
                .map(o -> ((Number) o).intValue())
                .flatMap(id -> usuarioRepository.findById(id));
    }

    /**
     * Obtiene el nombre de usuario autenticado
     * @return Nombre de usuario o null si no está autenticado
     */
    public String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return authentication.getName();
    }
}
