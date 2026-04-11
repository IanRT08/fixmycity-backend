package mx.edu.utez.fixmycity_backend.services;

import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.dto.response.PerfilResponse;
import mx.edu.utez.fixmycity_backend.modelos.Usuario;
import mx.edu.utez.fixmycity_backend.repositories.CuadrillaRepository;
import mx.edu.utez.fixmycity_backend.repositories.UsuarioRepository;
import mx.edu.utez.fixmycity_backend.repositories.VoluntarioRepository;
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
import java.util.List;
import java.util.Optional;

@Service
public class PerfilService {

    private static final long MAX_FOTO_BYTES = 5 * 1024 * 1024;

    private final UsuarioRepository usuarioRepository;
    private final VoluntarioRepository voluntarioRepository;
    private final CuadrillaRepository cuadrillaRepository;

    public PerfilService(UsuarioRepository usuarioRepository,
                         VoluntarioRepository voluntarioRepository,
                         CuadrillaRepository cuadrillaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.voluntarioRepository = voluntarioRepository;
        this.cuadrillaRepository = cuadrillaRepository;
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

        boolean esLider = false;
        Integer idCuadrillaLider = null;
        Optional<Object> idVolOpt = voluntarioRepository.findIdByUsuario(u.getIdUsuario());
        if (idVolOpt.isPresent()) {
            int idVoluntario = ((Number) idVolOpt.get()).intValue();
            List<Object> idsCuadrilla = cuadrillaRepository.findIdsByIdLiderVoluntario(idVoluntario);
            if (!idsCuadrilla.isEmpty()) {
                esLider = true;
                idCuadrillaLider = ((Number) idsCuadrilla.get(0)).intValue();
            }
        }

        return new PerfilResponse(
                u.getIdUsuario(),
                u.getNombreUsuario(),
                u.getCorreo(),
                u.getFechaNacimiento(),
                nombreMunicipio,
                idMunicipio,
                u.getTipo(),
                u.getEstado(),
                fotoB64,
                esLider,
                idCuadrillaLider
        );
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
