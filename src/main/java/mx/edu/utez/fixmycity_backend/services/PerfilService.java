package mx.edu.utez.fixmycity_backend.services;

import mx.edu.utez.fixmycity_backend.dto.request.PerfilUpdateRequest;
import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.dto.response.PerfilActualizadoResponse;
import mx.edu.utez.fixmycity_backend.dto.response.PerfilResponse;
import mx.edu.utez.fixmycity_backend.modelos.Municipios;
import mx.edu.utez.fixmycity_backend.modelos.Usuario;
import mx.edu.utez.fixmycity_backend.repositories.CuadrillaRepository;
import mx.edu.utez.fixmycity_backend.repositories.MunicipioRepository;
import mx.edu.utez.fixmycity_backend.repositories.UsuarioRepository;
import mx.edu.utez.fixmycity_backend.repositories.VoluntarioRepository;
import mx.edu.utez.fixmycity_backend.security.JwtUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

@Service
public class PerfilService {

    private static final long MAX_FOTO_BYTES = 5 * 1024 * 1024;

    private final UsuarioRepository usuarioRepository;
    private final VoluntarioRepository voluntarioRepository;
    private final CuadrillaRepository cuadrillaRepository;
    private final MunicipioRepository municipioRepository;
    private final JwtUtils jwtUtils;

    public PerfilService(UsuarioRepository usuarioRepository,
                         VoluntarioRepository voluntarioRepository,
                         CuadrillaRepository cuadrillaRepository,
                         MunicipioRepository municipioRepository,
                         JwtUtils jwtUtils) {
        this.usuarioRepository = usuarioRepository;
        this.voluntarioRepository = voluntarioRepository;
        this.cuadrillaRepository = cuadrillaRepository;
        this.municipioRepository = municipioRepository;
        this.jwtUtils = jwtUtils;
    }

    public ApiResponse obtenerPerfil(String nombreUsuario) {
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarIdPorNombre(nombreUsuario)
                .map(o -> ((Number) o).intValue())
                .flatMap(usuarioRepository::findById);
        if (usuarioOpt.isEmpty()) {
            return new ApiResponse(false, "Usuario no encontrado");
        }
        return new ApiResponse(true, "Perfil obtenido correctamente", toPerfilResponse(usuarioOpt.get()));
    }

    @Transactional
    public ApiResponse actualizarPerfil(String nombreUsuarioActual, PerfilUpdateRequest request) {
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarIdPorNombre(nombreUsuarioActual)
                .map(o -> ((Number) o).intValue())
                .flatMap(usuarioRepository::findById);
        if (usuarioOpt.isEmpty()) {
            return new ApiResponse(false, "Usuario no encontrado");
        }
        Usuario usuario = usuarioOpt.get();

        // Validar nombre de usuario único (solo si cambió)
        if (!usuario.getNombreUsuario().equalsIgnoreCase(request.getNombreUsuario())) {
            Optional<Object> existeNombre = usuarioRepository.buscarIdPorNombre(request.getNombreUsuario());
            if (existeNombre.isPresent()) {
                return new ApiResponse(false, "El nombre de usuario ya está en uso");
            }
        }

        // Validar correo único (solo si cambió)
        if (!usuario.getCorreo().equalsIgnoreCase(request.getCorreo())) {
            Optional<Object> existeCorreo = usuarioRepository.buscarIdPorCorreo(request.getCorreo());
            if (existeCorreo.isPresent()) {
                return new ApiResponse(false, "El correo electrónico ya está en uso");
            }
        }

        // Validar que el municipio exista
        Optional<Municipios> municipioOpt = municipioRepository.findById(request.getIdMunicipio());
        if (municipioOpt.isEmpty()) {
            return new ApiResponse(false, "El municipio seleccionado no existe");
        }

        boolean cambioToken = !usuario.getNombreUsuario().equals(request.getNombreUsuario())
                || usuario.getMunicipio().getIdMunicipio() != request.getIdMunicipio();

        usuario.setNombreUsuario(request.getNombreUsuario());
        usuario.setCorreo(request.getCorreo());
        usuario.setFechaNacimiento(request.getFechaNacimiento());
        usuario.setMunicipio(municipioOpt.get());
        usuarioRepository.save(usuario);

        PerfilResponse perfil = toPerfilResponse(usuario);
        String nuevoToken = cambioToken
                ? jwtUtils.generateToken(usuario.getNombreUsuario(), usuario.getTipo(), request.getIdMunicipio())
                : null;

        return new ApiResponse(true, "Perfil actualizado correctamente", new PerfilActualizadoResponse(perfil, nuevoToken));
    }

    @Transactional
    public ApiResponse actualizarFotoPerfil(String nombreUsuario, MultipartFile archivo) throws IOException {
        if (archivo == null || archivo.isEmpty()) {
            return new ApiResponse(false, "Selecciona una imagen");
        }
        if (archivo.getSize() > MAX_FOTO_BYTES) {
            return new ApiResponse(false, "La imagen no puede superar 5 MB");
        }
        String contentType = archivo.getContentType();
        if (contentType == null || (!contentType.startsWith("image/jpeg") && !contentType.startsWith("image/png")
                && !contentType.startsWith("image/jpg"))) {
            return new ApiResponse(false, "Solo se permiten imágenes JPEG o PNG");
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.buscarIdPorNombre(nombreUsuario)
                .map(o -> ((Number) o).intValue())
                .flatMap(usuarioRepository::findById);
        if (usuarioOpt.isEmpty()) {
            return new ApiResponse(false, "Usuario no encontrado");
        }

        byte[] comprimidos = comprimirImagen(archivo.getBytes());
        Usuario usuario = usuarioOpt.get();
        usuario.setFotoPerfil(comprimidos);
        usuarioRepository.save(usuario);

        return new ApiResponse(true, "Foto de perfil actualizada correctamente", toPerfilResponse(usuario));
    }

    @Transactional
    public ApiResponse eliminarFotoPerfil(String nombreUsuario) {
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarIdPorNombre(nombreUsuario)
                .map(o -> ((Number) o).intValue())
                .flatMap(usuarioRepository::findById);
        if (usuarioOpt.isEmpty()) {
            return new ApiResponse(false, "Usuario no encontrado");
        }
        Usuario usuario = usuarioOpt.get();
        usuario.setFotoPerfil(null);
        usuarioRepository.save(usuario);
        return new ApiResponse(true, "Foto de perfil eliminada", toPerfilResponse(usuario));
    }

    private PerfilResponse toPerfilResponse(Usuario u) {
        String fotoB64 = u.getFotoPerfil() != null && u.getFotoPerfil().length > 0
                ? Base64.getEncoder().encodeToString(u.getFotoPerfil())
                : null;
        int idMunicipio = u.getMunicipio() != null ? u.getMunicipio().getIdMunicipio() : -1;
        String nombreMunicipio = u.getMunicipio() != null ? u.getMunicipio().getNombre() : null;
        PerfilResponse response = new PerfilResponse(
                u.getIdUsuario(),
                u.getNombreUsuario(),
                u.getCorreo(),
                u.getFechaNacimiento(),
                nombreMunicipio,
                idMunicipio,
                u.getTipo(),
                u.getEstado(),
                fotoB64
        );
        // Determinar si el usuario es líder de cuadrilla
        voluntarioRepository.findIdByUsuario(u.getIdUsuario()).ifPresent(volIdObj -> {
            int idVoluntario = ((Number) volIdObj).intValue();
            java.util.List<Object> cuadrillaIds = cuadrillaRepository.findIdsByIdLiderVoluntario(idVoluntario);
            if (!cuadrillaIds.isEmpty()) {
                response.setEsLiderDeCuadrilla(true);
                response.setIdCuadrilla(((Number) cuadrillaIds.get(0)).intValue());
            }
        });
        return response;
    }

    private byte[] comprimirImagen(byte[] original) throws IOException {
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(original));
        if (img == null) {
            return original;
        }
        int maxDim = 512;
        int w = img.getWidth();
        int h = img.getHeight();
        if (w > maxDim || h > maxDim) {
            double scale = Math.min((double) maxDim / w, (double) maxDim / h);
            int nw = (int) (w * scale);
            int nh = (int) (h * scale);
            BufferedImage resized = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_RGB);
            resized.createGraphics().drawImage(img.getScaledInstance(nw, nh, Image.SCALE_SMOOTH), 0, 0, null);
            img = resized;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", out);
        byte[] compressed = out.toByteArray();
        return compressed.length < original.length ? compressed : original;
    }
}
