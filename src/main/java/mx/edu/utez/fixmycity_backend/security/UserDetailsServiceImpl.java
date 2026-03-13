package mx.edu.utez.fixmycity_backend.security;

import mx.edu.utez.fixmycity_backend.modelos.Usuario;
import mx.edu.utez.fixmycity_backend.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String nombreUsuario)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado: " + nombreUsuario));

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(
                "ROLE_" + usuario.getTipo().toUpperCase()
        );

        return new User(
                usuario.getNombreUsuario(),
                usuario.getContrasenia(),
                Collections.singletonList(authority)
        );
    }
}