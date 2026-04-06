package mx.edu.utez.fixmycity_backend.services;

import mx.edu.utez.fixmycity_backend.dto.request.RegistroAdminRequest;
import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.dto.response.UsuarioResponse;
import mx.edu.utez.fixmycity_backend.modelos.Administradores;
import mx.edu.utez.fixmycity_backend.modelos.Municipios;
import mx.edu.utez.fixmycity_backend.modelos.Usuario;
import mx.edu.utez.fixmycity_backend.repositories.AdministradoresRepository;
import mx.edu.utez.fixmycity_backend.repositories.MunicipioRepository;
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
    private MunicipioRepository municipioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public ApiResponse crearAdministrador(RegistroAdminRequest request) {

        if (!request.getContrasenia().equals(request.getConfirmarContrasenia())) {
            return new ApiResponse(false, "Las contraseñas no coinciden");
        }

        // Validar que el usuario no exista
        if (usuarioRepository.buscarIdPorNombre(request.getNombreUsuario()).isPresent()) {
            return new ApiResponse(false, "El nombre de usuario ya está en uso");
        }

        if (usuarioRepository.buscarIdPorCorreo(request.getCorreo()).isPresent()) {
            return new ApiResponse(false, "El correo ya está registrado");
        }

        Optional<Municipios> municipioOpt = municipioRepository.findById(request.getIdMunicipio());
        if (municipioOpt.isEmpty()) {
            return new ApiResponse(false, "El municipio seleccionado no existe");
        }

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(request.getNombreUsuario());
        usuario.setCorreo(request.getCorreo());
        usuario.setContrasenia(passwordEncoder.encode(request.getContrasenia()));
        usuario.setFechaNacimiento(request.getFechaNacimiento());
        usuario.setMunicipio(municipioOpt.get());
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

    public ApiResponse listarPorTipo(String tipo, int idAdmin) {
        Optional<Usuario> adminOpt = usuarioRepository.findById(idAdmin);
        if (adminOpt.isEmpty()) {
            return new ApiResponse(false, "Administrador no encontrado");
        }

        String tipoAdmin = adminOpt.get().getTipo();
        List<Object[]> rows;

        if (tipoAdmin.equals("superadmin") || adminOpt.get().getMunicipio() == null) {
            rows = usuarioRepository.findByTipo(tipo);
        } else {
            int idMunicipio = adminOpt.get().getMunicipio().getIdMunicipio();
            rows = usuarioRepository.findByTipoAndMunicipio(tipo, idMunicipio);
        }

        // row: [0]=idUsuario, [1]=nombreUsuario, [2]=correo, [3]=tipo, [4]=estado, [5]=municipio
        List<UsuarioResponse> response = rows.stream()
                .map(row -> new UsuarioResponse(
                        ((Number) row[0]).intValue(),
                        (String) row[1],
                        (String) row[2],
                        (String) row[3],
                        (String) row[4],
                        (String) row[5]
                ))
                .collect(Collectors.toList());

        return new ApiResponse(true, "Usuarios obtenidos correctamente", response);
    }
}