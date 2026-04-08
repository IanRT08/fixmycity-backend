package mx.edu.utez.fixmycity_backend.services;

import mx.edu.utez.fixmycity_backend.dto.request.CancelacionReporteRequest;
import mx.edu.utez.fixmycity_backend.dto.request.ReporteRequest;
import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.dto.response.ReportePageResponse;
import mx.edu.utez.fixmycity_backend.dto.response.ReporteResponse;
import mx.edu.utez.fixmycity_backend.modelos.*;
import mx.edu.utez.fixmycity_backend.repositories.*;
import mx.edu.utez.fixmycity_backend.support.TransactionAfterCommit;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    private static final int MAX_PAGE_SIZE = 100;

    @Autowired private ReporteRepository reporteRepository;
    @Autowired private detallesReporteRepository detallesReporteRepository;
    @Autowired private fotosReporteRepository fotosReporteRepository;
    @Autowired private cancelacionReporteRepository cancelacionReporteRepository;
    @Autowired private historialReporteRepository historialReporteRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private MunicipioRepository municipioRepository;
    @Autowired private NotificacionesService notificacionService;
    @Autowired private FcmPushService fcmPushService;
    @Autowired private TransactionAfterCommit afterCommit;

    @Transactional
    public ApiResponse crearReporte(int idUsuario, ReporteRequest request,
                                    List<MultipartFile> fotos) throws IOException {
        Optional<Municipios> municipioOpt = municipioRepository.findById(request.getIdMunicipio());
        if (municipioOpt.isEmpty() || !municipioOpt.get().getEstado().equals("Activo"))
            return new ApiResponse(false, "El municipio seleccionado no está disponible");
        if (fotos != null && fotos.size() > 3)
            return new ApiResponse(false, "Máximo 3 fotos por reporte");
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
        if (usuarioOpt.isEmpty()) return new ApiResponse(false, "Usuario no encontrado");

        Reporte reporte = new Reporte();
        reporte.setUsuario(usuarioOpt.get());
        reporte.setTitulo(request.getTitulo());
        reporteRepository.save(reporte);

        detallesReporte detalles = new detallesReporte();
        detalles.setReporte(reporte);
        detalles.setDescripcion(request.getDescripcion());
        detalles.setMunicipios(municipioOpt.get());
        detalles.setEstado("Pendiente");
        detalles.setFechaRegistro(new Date());
        detallesReporteRepository.save(detalles);

        if (fotos != null && !fotos.isEmpty()) {
            for (MultipartFile foto : fotos) {
                fotosReporte fotoReporte = new fotosReporte();
                fotoReporte.setReporte(reporte);
                fotoReporte.setFoto(foto.getBytes());
                fotosReporteRepository.save(fotoReporte);
            }
        }
        registrarHistorial(reporte, idUsuario, "Pendiente", "Pendiente");
        int idMun = municipioOpt.get().getIdMunicipio();
        int idRep = reporte.getIdReporte();
        String titulo = reporte.getTitulo();
        afterCommit.run(() -> fcmPushService.enviarNuevoReporteEnMunicipioAsync(idMun, idRep, titulo, idUsuario));
        return new ApiResponse(true, "Reporte creado correctamente");
    }

    public ApiResponse obtenerMisReportes(int idUsuario, Integer page, Integer size) {
        if (size == null) {
            List<Reporte> reportes = reporteRepository.findIdsByUsuario(idUsuario).stream()
                    .map(o -> ((Number) o).intValue())
                    .map(id -> reporteRepository.findById(id).orElse(null))
                    .filter(r -> r != null).collect(Collectors.toList());
            return new ApiResponse(true, "Reportes obtenidos correctamente", mapearReportes(reportes));
        }
        int p = page != null ? Math.max(0, page) : 0;
        int s = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        long total = reporteRepository.countReportesByUsuario(idUsuario);
        List<Reporte> reportes = reporteRepository.findIdsByUsuarioPaged(idUsuario, p * s, s).stream()
                .map(o -> ((Number) o).intValue())
                .map(id -> reporteRepository.findById(id).orElse(null))
                .filter(r -> r != null).collect(Collectors.toList());
        return new ApiResponse(true, "Reportes obtenidos correctamente",
                toPageResponse(mapearReportes(reportes), total, p, s));
    }

    public ApiResponse obtenerReportePorId(int idReporte, int idUsuario) {
        Optional<Reporte> reporteOpt = reporteRepository.findById(idReporte);
        if (reporteOpt.isEmpty()) return new ApiResponse(false, "Reporte no encontrado");
        Reporte reporte = reporteOpt.get();
        if (reporte.getUsuario().getIdUsuario() != idUsuario)
            return new ApiResponse(false, "No tienes permiso para ver este reporte");
        detallesReporte detalles = detallesReporteRepository.findByReporte(idReporte).orElse(null);
        List<String> fotos = fotosReporteRepository.findByReporte(idReporte).stream()
                .map(f -> Base64.getEncoder().encodeToString(f.getFoto())).collect(Collectors.toList());
        return new ApiResponse(true, "Reporte obtenido correctamente", new ReporteResponse(
                reporte.getIdReporte(), reporte.getTitulo(),
                detalles != null ? detalles.getDescripcion() : null,
                detalles != null ? detalles.getEstado() : null,
                detalles != null ? detalles.getMunicipios().getNombre() : null,
                detalles != null ? detalles.getFechaRegistro() : null,
                reporte.getUsuario().getNombreUsuario(), fotos));
    }

    @Transactional
    public ApiResponse editarReporte(int idReporte, int idUsuario, ReporteRequest request) {
        Optional<Reporte> reporteOpt = reporteRepository.findById(idReporte);
        if (reporteOpt.isEmpty()) return new ApiResponse(false, "Reporte no encontrado");
        Reporte reporte = reporteOpt.get();
        if (reporte.getUsuario().getIdUsuario() != idUsuario)
            return new ApiResponse(false, "No tienes permiso para editar este reporte");
        Optional<detallesReporte> detallesOpt = detallesReporteRepository.findByReporte(idReporte);
        if (detallesOpt.isEmpty()) return new ApiResponse(false, "Detalles del reporte no encontrados");
        if (!detallesOpt.get().getEstado().equals("Pendiente"))
            return new ApiResponse(false, "Solo puedes editar reportes en estado Pendiente");
        Optional<Municipios> municipioOpt = municipioRepository.findById(request.getIdMunicipio());
        if (municipioOpt.isEmpty() || !municipioOpt.get().getEstado().equals("Activo"))
            return new ApiResponse(false, "El municipio seleccionado no está disponible");
        reporte.setTitulo(request.getTitulo());
        reporteRepository.save(reporte);
        detallesReporte detalles = detallesOpt.get();
        detalles.setDescripcion(request.getDescripcion());
        detalles.setMunicipios(municipioOpt.get());
        detallesReporteRepository.save(detalles);
        List<String> fotos = fotosReporteRepository.findByReporte(idReporte).stream()
                .map(f -> Base64.getEncoder().encodeToString(f.getFoto())).collect(Collectors.toList());
        return new ApiResponse(true, "Reporte actualizado correctamente", new ReporteResponse(
                reporte.getIdReporte(), reporte.getTitulo(), detalles.getDescripcion(), detalles.getEstado(),
                detalles.getMunicipios().getNombre(), detalles.getFechaRegistro(),
                reporte.getUsuario().getNombreUsuario(), fotos));
    }

    @Transactional
    public ApiResponse cancelarReporte(int idReporte, int idUsuario, CancelacionReporteRequest request) {
        Optional<Reporte> reporteOpt = reporteRepository.findById(idReporte);
        if (reporteOpt.isEmpty()) return new ApiResponse(false, "Reporte no encontrado");
        Reporte reporte = reporteOpt.get();
        if (reporte.getUsuario().getIdUsuario() != idUsuario)
            return new ApiResponse(false, "No tienes permiso para cancelar este reporte");
        Optional<detallesReporte> detallesOpt = detallesReporteRepository.findByReporte(idReporte);
        if (detallesOpt.isEmpty()) return new ApiResponse(false, "Detalles no encontrados");
        String estadoActual = detallesOpt.get().getEstado();
        if (!estadoActual.equals("Pendiente") && !estadoActual.equals("Asignado"))
            return new ApiResponse(false, "Solo puedes cancelar reportes en estado Pendiente o Asignado");
        cancelacionReporte cancelacion = new cancelacionReporte();
        cancelacion.setReporte(reporte);
        cancelacion.setUsuario(usuarioRepository.findById(idUsuario).get());
        cancelacion.setMotivoCancelacion(request.getMotivoCancelacion());
        cancelacionReporteRepository.save(cancelacion);
        registrarHistorial(reporte, idUsuario, estadoActual, "Cancelado");
        detallesReporteRepository.updateEstado(idReporte, "Cancelado");
        return new ApiResponse(true, "Reporte cancelado correctamente");
    }

    public ApiResponse obtenerDetalleAdmin(int idReporte, int idAdmin) {
        Optional<Usuario> adminOpt = usuarioRepository.findById(idAdmin);
        if (adminOpt.isEmpty()) return new ApiResponse(false, "Administrador no encontrado");
        Optional<Reporte> reporteOpt = reporteRepository.findById(idReporte);
        if (reporteOpt.isEmpty()) return new ApiResponse(false, "Reporte no encontrado");
        Usuario admin = adminOpt.get();
        if (!admin.getTipo().equals("superadmin")) {
            Optional<detallesReporte> detallesOpt = detallesReporteRepository.findByReporte(idReporte);
            if (detallesOpt.isEmpty() ||
                    detallesOpt.get().getMunicipios().getIdMunicipio() != admin.getMunicipio().getIdMunicipio())
                return new ApiResponse(false, "No tienes permiso para ver este reporte");
        }
        Reporte reporte = reporteOpt.get();
        detallesReporte detalles = detallesReporteRepository.findByReporte(idReporte).orElse(null);
        List<String> fotos = fotosReporteRepository.findByReporte(idReporte).stream()
                .map(f -> Base64.getEncoder().encodeToString(f.getFoto())).collect(Collectors.toList());
        return new ApiResponse(true, "Reporte obtenido correctamente", new ReporteResponse(
                reporte.getIdReporte(), reporte.getTitulo(),
                detalles != null ? detalles.getDescripcion() : null,
                detalles != null ? detalles.getEstado() : null,
                detalles != null ? detalles.getMunicipios().getNombre() : null,
                detalles != null ? detalles.getFechaRegistro() : null,
                reporte.getUsuario().getNombreUsuario(), fotos));
    }

    public ApiResponse listarReportesAdmin(String estado, Integer idMunicipio,
                                           String fechaInicio, String fechaFin,
                                           String keyword, int idAdmin) {
        Optional<Usuario> adminOpt = usuarioRepository.findById(idAdmin);
        if (adminOpt.isEmpty()) return new ApiResponse(false, "Administrador no encontrado");
        Usuario admin = adminOpt.get();
        Integer idMunicipioFiltro = admin.getTipo().equals("superadmin")
                ? idMunicipio
                : (admin.getMunicipio() != null ? admin.getMunicipio().getIdMunicipio() : null);

        List<Object[]> rows = reporteRepository.findReportesAdminDirect(
                estado, idMunicipioFiltro, fechaInicio, fechaFin, keyword);
        List<ReporteResponse> reportes = rows.stream().map(row -> {
            java.util.Date fecha = null;
            if (row[5] instanceof java.sql.Timestamp) fecha = new java.util.Date(((java.sql.Timestamp) row[5]).getTime());
            else if (row[5] instanceof java.sql.Date) fecha = new java.util.Date(((java.sql.Date) row[5]).getTime());
            else if (row[5] instanceof java.time.LocalDateTime) fecha = java.util.Date.from(((java.time.LocalDateTime) row[5]).atZone(java.time.ZoneId.systemDefault()).toInstant());
            else if (row[5] instanceof java.time.LocalDate) fecha = java.util.Date.from(((java.time.LocalDate) row[5]).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
            else if (row[5] instanceof java.util.Date) fecha = (java.util.Date) row[5];
            return new ReporteResponse(
                    ((Number) row[0]).intValue(), (String) row[1], (String) row[3],
                    (String) row[4], (String) row[6], fecha, (String) row[2], List.of());
        }).collect(Collectors.toList());
        return new ApiResponse(true, "Reportes obtenidos correctamente", reportes);
    }

    @Transactional
    public ApiResponse rechazarReporte(int idReporte, int idAdmin, CancelacionReporteRequest request) {
        Optional<Reporte> reporteOpt = reporteRepository.findById(idReporte);
        if (reporteOpt.isEmpty()) return new ApiResponse(false, "Reporte no encontrado");
        Optional<detallesReporte> detallesOpt = detallesReporteRepository.findByReporte(idReporte);
        if (detallesOpt.isEmpty()) return new ApiResponse(false, "Detalles no encontrados");
        String estadoActual = detallesOpt.get().getEstado();
        if (!estadoActual.equals("Pendiente"))
            return new ApiResponse(false, "Solo se pueden rechazar reportes en estado Pendiente");
        Reporte reporte = reporteOpt.get();
        cancelacionReporte cancelacion = new cancelacionReporte();
        cancelacion.setReporte(reporte);
        cancelacion.setUsuario(usuarioRepository.findById(idAdmin).get());
        cancelacion.setMotivoCancelacion(request.getMotivoCancelacion());
        cancelacionReporteRepository.save(cancelacion);
        registrarHistorial(reporte, idAdmin, estadoActual, "Rechazado");
        detallesReporteRepository.updateEstado(idReporte, "Rechazado");
        notificacionService.enviarNotificacion(reporte.getUsuario().getIdUsuario(), idReporte,
                "Tu reporte '" + reporte.getTitulo() + "' fue rechazado");
        return new ApiResponse(true, "Reporte rechazado correctamente");
    }

    // NEW: Marcar reporte como duplicado — solo cambia estado, sin motivo
    @Transactional
    public ApiResponse marcarDuplicado(int idReporte, int idAdmin) {
        Optional<Reporte> reporteOpt = reporteRepository.findById(idReporte);
        if (reporteOpt.isEmpty()) return new ApiResponse(false, "Reporte no encontrado");
        Optional<detallesReporte> detallesOpt = detallesReporteRepository.findByReporte(idReporte);
        if (detallesOpt.isEmpty()) return new ApiResponse(false, "Detalles no encontrados");
        String estadoActual = detallesOpt.get().getEstado();
        if (!estadoActual.equals("Pendiente"))
            return new ApiResponse(false, "Solo se pueden marcar como duplicado reportes en estado Pendiente");
        Reporte reporte = reporteOpt.get();
        registrarHistorial(reporte, idAdmin, estadoActual, "Duplicado");
        detallesReporteRepository.updateEstado(idReporte, "Duplicado");
        notificacionService.enviarNotificacion(reporte.getUsuario().getIdUsuario(), idReporte,
                "Tu reporte '" + reporte.getTitulo() + "' fue marcado como duplicado");
        return new ApiResponse(true, "Reporte marcado como duplicado");
    }

    public ApiResponse obtenerDetallePublico(int idReporte) {
        Optional<Reporte> reporteOpt = reporteRepository.findById(idReporte);
        if (reporteOpt.isEmpty()) return new ApiResponse(false, "Reporte no encontrado");
        Reporte reporte = reporteOpt.get();
        detallesReporte detalles = detallesReporteRepository.findByReporte(idReporte).orElse(null);
        List<String> fotos = fotosReporteRepository.findByReporte(idReporte).stream()
                .map(f -> Base64.getEncoder().encodeToString(f.getFoto())).collect(Collectors.toList());
        return new ApiResponse(true, "Reporte obtenido correctamente", new ReporteResponse(
                reporte.getIdReporte(), reporte.getTitulo(),
                detalles != null ? detalles.getDescripcion() : null,
                detalles != null ? detalles.getEstado() : null,
                detalles != null ? detalles.getMunicipios().getNombre() : null,
                detalles != null ? detalles.getFechaRegistro() : null,
                reporte.getUsuario().getNombreUsuario(), fotos));
    }

    public ApiResponse obtenerFeed(int idMunicipio, Integer page, Integer size) {
        if (size == null) {
            List<Reporte> reportes = reporteRepository.findFeedIdsByMunicipio(idMunicipio).stream()
                    .map(o -> ((Number) o).intValue())
                    .map(id -> reporteRepository.findById(id).orElse(null))
                    .filter(r -> r != null).collect(Collectors.toList());
            return new ApiResponse(true, "Feed obtenido correctamente", mapearReportes(reportes));
        }
        int p = page != null ? Math.max(0, page) : 0;
        int s = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        long total = reporteRepository.countFeedByMunicipio(idMunicipio);
        List<Reporte> reportes = reporteRepository.findFeedIdsByMunicipioPaged(idMunicipio, p * s, s).stream()
                .map(o -> ((Number) o).intValue())
                .map(id -> reporteRepository.findById(id).orElse(null))
                .filter(r -> r != null).collect(Collectors.toList());
        return new ApiResponse(true, "Feed obtenido correctamente",
                toPageResponse(mapearReportes(reportes), total, p, s));
    }

    private ReportePageResponse toPageResponse(List<ReporteResponse> content, long totalElements, int page, int size) {
        int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
        boolean last = totalPages == 0 || page >= totalPages - 1;
        return new ReportePageResponse(content, totalElements, totalPages, page, size, last);
    }

    private void registrarHistorial(Reporte reporte, int idResponsable, String estadoAnterior, String estadoNuevo) {
        historialReporte historial = new historialReporte();
        historial.setReporte(reporte);
        historial.setUsuario(usuarioRepository.findById(idResponsable).get());
        historial.setEstadoAnterior(estadoAnterior);
        historial.setEstadoNuevo(estadoNuevo);
        historial.setFechaCambio(new java.sql.Timestamp(System.currentTimeMillis()));
        historialReporteRepository.save(historial);
    }

    private ReporteResponse mapearReporte(Reporte r) {
        detallesReporte detalles = detallesReporteRepository.findByReporte(r.getIdReporte()).orElse(null);
        return new ReporteResponse(r.getIdReporte(), r.getTitulo(),
                detalles != null ? detalles.getDescripcion() : null,
                detalles != null ? detalles.getEstado() : null,
                detalles != null ? detalles.getMunicipios().getNombre() : null,
                detalles != null ? detalles.getFechaRegistro() : null,
                r.getUsuario().getNombreUsuario(), List.of());
    }

    private List<ReporteResponse> mapearReportes(List<Reporte> reportes) {
        return reportes.stream().map(this::mapearReporte).collect(Collectors.toList());
    }

    private byte[] comprimirImagen(byte[] original) throws IOException {
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(original));
        if (img == null) return original;
        int maxDim = 1024;
        int w = img.getWidth(), h = img.getHeight();
        if (w > maxDim || h > maxDim) {
            double scale = Math.min((double) maxDim / w, (double) maxDim / h);
            int nw = (int) (w * scale), nh = (int) (h * scale);
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
