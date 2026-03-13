package mx.edu.utez.fixmycity_backend.services;

import mx.edu.utez.fixmycity_backend.dto.request.CancelacionReporteRequest;
import mx.edu.utez.fixmycity_backend.dto.request.ReporteRequest;
import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.dto.response.ReporteResponse;
import mx.edu.utez.fixmycity_backend.modelos.*;
import mx.edu.utez.fixmycity_backend.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    @Autowired
    private ReporteRepository reporteRepository;

    @Autowired
    private detallesReporteRepository detallesReporteRepository;

    @Autowired
    private fotosReporteRepository fotosReporteRepository;

    @Autowired
    private cancelacionReporteRepository cancelacionReporteRepository;

    @Autowired
    private historialReporteRepository historialReporteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MunicipioRepository municipioRepository;

    @Autowired
    private NotificacionesService notificacionService;

    @Transactional
    public ApiResponse crearReporte(int idUsuario, ReporteRequest request,
                                    List<MultipartFile> fotos) throws IOException {

        Optional<Municipios> municipioOpt = municipioRepository.findById(request.getIdMunicipio());
        if (municipioOpt.isEmpty() || !municipioOpt.get().getEstado().equals("Activo")) {
            return new ApiResponse(false, "El municipio seleccionado no está disponible");
        }

        if (fotos != null && fotos.size() > 3) {
            return new ApiResponse(false, "Solo se permiten máximo 3 fotos por reporte");
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
        if (usuarioOpt.isEmpty()) {
            return new ApiResponse(false, "Usuario no encontrado");
        }

        Reporte reporte = new Reporte();
        reporte.setUsuario(usuarioOpt.get()); // setIdUsuario() porque el atributo es idUsuario
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
                fotoReporte.setFoto(Base64.getEncoder().encodeToString(foto.getBytes()).getBytes());
                fotosReporteRepository.save(fotoReporte);
            }
        }

        registrarHistorial(reporte, idUsuario, null, "Pendiente");
        return new ApiResponse(true, "Reporte creado correctamente");
    }

    public ApiResponse obtenerMisReportes(int idUsuario) {
        List<Reporte> reportes = reporteRepository.findByUsuario(idUsuario);
        return new ApiResponse(true, "Reportes obtenidos correctamente", mapearReportes(reportes));
    }

    public ApiResponse obtenerReportePorId(int idReporte, int idUsuario) {
        Optional<Reporte> reporteOpt = reporteRepository.findById(idReporte);
        if (reporteOpt.isEmpty()) {
            return new ApiResponse(false, "Reporte no encontrado");
        }
        if (reporteOpt.get().getUsuario().getIdUsuario() != idUsuario) {
            return new ApiResponse(false, "No tienes permiso para ver este reporte");
        }
        return new ApiResponse(true, "Reporte obtenido correctamente", mapearReporte(reporteOpt.get()));
    }

    @Transactional
    public ApiResponse editarReporte(int idReporte, int idUsuario, ReporteRequest request) {

        Optional<Reporte> reporteOpt = reporteRepository.findById(idReporte);
        if (reporteOpt.isEmpty()) {
            return new ApiResponse(false, "Reporte no encontrado");
        }

        Reporte reporte = reporteOpt.get();
        if (reporte.getUsuario().getIdUsuario() != idUsuario) {
            return new ApiResponse(false, "No tienes permiso para editar este reporte");
        }

        Optional<detallesReporte> detallesOpt = detallesReporteRepository.findByReporte(idReporte);
        if (detallesOpt.isEmpty()) {
            return new ApiResponse(false, "No se encontraron los detalles del reporte");
        }
        if (!detallesOpt.get().getEstado().equals("Pendiente")) {
            return new ApiResponse(false, "Solo puedes editar reportes en estado Pendiente");
        }

        reporte.setTitulo(request.getTitulo());
        reporteRepository.save(reporte);

        detallesReporte detalles = detallesOpt.get();
        detalles.setDescripcion(request.getDescripcion());
        detallesReporteRepository.save(detalles);

        return new ApiResponse(true, "Reporte actualizado correctamente");
    }

    @Transactional
    public ApiResponse cancelarReporte(int idReporte, int idUsuario,
                                       CancelacionReporteRequest request) {

        Optional<Reporte> reporteOpt = reporteRepository.findById(idReporte);
        if (reporteOpt.isEmpty()) {
            return new ApiResponse(false, "Reporte no encontrado");
        }

        Reporte reporte = reporteOpt.get();
        if (reporte.getUsuario().getIdUsuario() != idUsuario) {
            return new ApiResponse(false, "No tienes permiso para cancelar este reporte");
        }

        Optional<detallesReporte> detallesOpt = detallesReporteRepository.findByReporte(idReporte);
        if (detallesOpt.isEmpty()) {
            return new ApiResponse(false, "No se encontraron los detalles del reporte");
        }

        String estadoActual = detallesOpt.get().getEstado();
        if (!estadoActual.equals("Pendiente") && !estadoActual.equals("Asignado")) {
            return new ApiResponse(false, "Solo puedes cancelar reportes en estado Pendiente o Asignado");
        }

        cancelacionReporte cancelacion = new cancelacionReporte();
        cancelacion.setReporte(reporte);
        cancelacion.setUsuario(usuarioRepository.findById(idUsuario).get());
        cancelacion.setMotivoCancelacion(request.getMotivoCancelacion());
        cancelacionReporteRepository.save(cancelacion);

        registrarHistorial(reporte, idUsuario, estadoActual, "Cancelado");
        detallesReporteRepository.updateEstado(idReporte, "Cancelado");

        return new ApiResponse(true, "Reporte cancelado correctamente");
    }

    public ApiResponse listarReportesAdmin(String estado, Integer idMunicipio,
                                           String fechaInicio, String fechaFin, String keyword) {
        List<Reporte> reportes = reporteRepository.findWithFilters(
                estado, idMunicipio, fechaInicio, fechaFin, keyword);
        return new ApiResponse(true, "Reportes obtenidos correctamente", mapearReportes(reportes));
    }

    @Transactional
    public ApiResponse rechazarReporte(int idReporte, int idAdmin,
                                       CancelacionReporteRequest request) {

        Optional<Reporte> reporteOpt = reporteRepository.findById(idReporte);
        if (reporteOpt.isEmpty()) {
            return new ApiResponse(false, "Reporte no encontrado");
        }

        Optional<detallesReporte> detallesOpt = detallesReporteRepository.findByReporte(idReporte);
        if (detallesOpt.isEmpty()) {
            return new ApiResponse(false, "No se encontraron los detalles del reporte");
        }

        String estadoActual = detallesOpt.get().getEstado();
        if (!estadoActual.equals("Pendiente")) {
            return new ApiResponse(false, "Solo se pueden rechazar reportes en estado Pendiente");
        }

        Reporte reporte = reporteOpt.get();
        cancelacionReporte cancelacion = new cancelacionReporte();
        cancelacion.setReporte(reporte);
        cancelacion.setUsuario(usuarioRepository.findById(idAdmin).get());
        cancelacion.setMotivoCancelacion(request.getMotivoCancelacion());
        cancelacionReporteRepository.save(cancelacion);

        registrarHistorial(reporte, idAdmin, estadoActual, "Rechazado");
        detallesReporteRepository.updateEstado(idReporte, "Rechazado");

        notificacionService.enviarNotificacion(
                reporte.getUsuario().getIdUsuario(),
                idReporte,
                "Tu reporte '" + reporte.getTitulo() + "' fue rechazado"
        );

        return new ApiResponse(true, "Reporte rechazado correctamente");
    }

    public ApiResponse obtenerFeed(int idMunicipio) {
        List<Reporte> reportes = reporteRepository.findFeedByMunicipio(idMunicipio);
        return new ApiResponse(true, "Feed obtenido correctamente", mapearReportes(reportes));
    }


    private void registrarHistorial(Reporte reporte, int idResponsable,
                                    String estadoAnterior, String estadoNuevo) {
        historialReporte historial = new historialReporte();
        historial.setReporte(reporte);
        historial.setUsuario(usuarioRepository.findById(idResponsable).get());
        historial.setEstadoAnterior(estadoAnterior);
        historial.setEstadoNuevo(estadoNuevo);
        historial.setFechaCambio(new java.sql.Timestamp(System.currentTimeMillis()));
        historialReporteRepository.save(historial);
    }

    private ReporteResponse mapearReporte(Reporte r) {
        detallesReporte detalles = detallesReporteRepository
                .findByReporte(r.getIdReporte()).orElse(null);
        return new ReporteResponse(
                r.getIdReporte(),
                r.getTitulo(),
                detalles != null ? detalles.getDescripcion() : null,
                detalles != null ? detalles.getEstado() : null,
                detalles != null ? detalles.getMunicipios().getNombre() : null,
                detalles != null ? detalles.getFechaRegistro() : null,
                r.getUsuario().getNombreUsuario()
        );
    }

    private List<ReporteResponse> mapearReportes(List<Reporte> reportes) {
        return reportes.stream().map(this::mapearReporte).collect(Collectors.toList());
    }
}