package mx.edu.utez.fixmycity_backend.services;

import mx.edu.utez.fixmycity_backend.dto.request.AsignacionReporteRequest;
import mx.edu.utez.fixmycity_backend.dto.request.FinalizarReporteRequest;
import mx.edu.utez.fixmycity_backend.dto.request.VotacionRequest;
import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
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

@Service
public class AsignacionService {

    private final ReporteRepository reporteRepository;
    private final detallesReporteRepository detallesReporteRepository;
    private final reporteAsignadoCuadrillaRepository asignacionRepository;
    private final reporteFinalizadoRepository reporteFinalizadoRepository;
    private final historialReporteRepository historialReporteRepository;
    private final CuadrillaRepository cuadrillaRepository;
    private final miembrosCuadrillaRepository miembrosCuadrillaRepository;
    private final VotacionRepository votacionRepository;
    private final VoluntarioRepository voluntarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionesService notificacionService;

    public AsignacionService(ReporteRepository reporteRepository,
                             detallesReporteRepository detallesReporteRepository,
                             reporteAsignadoCuadrillaRepository asignacionRepository,
                             reporteFinalizadoRepository reporteFinalizadoRepository,
                             historialReporteRepository historialReporteRepository,
                             CuadrillaRepository cuadrillaRepository,
                             miembrosCuadrillaRepository miembrosCuadrillaRepository,
                             VotacionRepository votacionRepository,
                             VoluntarioRepository voluntarioRepository,
                             UsuarioRepository usuarioRepository,
                             NotificacionesService notificacionService) {
        this.reporteRepository = reporteRepository;
        this.detallesReporteRepository = detallesReporteRepository;
        this.asignacionRepository = asignacionRepository;
        this.reporteFinalizadoRepository = reporteFinalizadoRepository;
        this.historialReporteRepository = historialReporteRepository;
        this.cuadrillaRepository = cuadrillaRepository;
        this.miembrosCuadrillaRepository = miembrosCuadrillaRepository;
        this.votacionRepository = votacionRepository;
        this.voluntarioRepository = voluntarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacionService = notificacionService;
    }

    @Transactional
    public ApiResponse asignarReporte(AsignacionReporteRequest request, int idAdmin) {

        Optional<Reporte> reporteOpt = reporteRepository.findById(request.getIdReporte());
        if (reporteOpt.isEmpty()) {
            return new ApiResponse(false, "Reporte no encontrado");
        }

        Optional<detallesReporte> detallesOpt = detallesReporteRepository
                .findByReporte(request.getIdReporte());
        if (detallesOpt.isEmpty() || !detallesOpt.get().getEstado().equals("Pendiente")) {
            return new ApiResponse(false, "Solo se pueden asignar reportes en estado Pendiente");
        }

        Optional<Cuadrilla> cuadrillaOpt = cuadrillaRepository.findById(request.getIdCuadrilla());
        if (cuadrillaOpt.isEmpty() || !cuadrillaOpt.get().getEstado().equals("activa")) {
            return new ApiResponse(false, "La cuadrilla seleccionada no está disponible");
        }

        reporteAsignadoCuadrilla asignacion = new reporteAsignadoCuadrilla();
        asignacion.setIdReporte(reporteOpt.get());
        asignacion.setIdCuadrilla(cuadrillaOpt.get());
        asignacion.setFechaAsignacion(new java.sql.Timestamp(System.currentTimeMillis()));
        asignacionRepository.save(asignacion);

        registrarHistorial(reporteOpt.get(), idAdmin, detallesOpt.get().getEstado(), "Asignado");
        detallesReporteRepository.updateEstado(request.getIdReporte(), "Asignado");

        List<miembrosCuadrilla> miembros = miembrosCuadrillaRepository // clase con m minúscula
                .findByCuadrilla(request.getIdCuadrilla());
        for (miembrosCuadrilla miembro : miembros) {
            notificacionService.enviarNotificacion(
                    miembro.getVoluntario().getUsuario().getIdUsuario(),
                    request.getIdReporte(),
                    "Se te ha asignado un nuevo reporte: " + reporteOpt.get().getTitulo()
            );
        }

        notificacionService.enviarNotificacion(
                reporteOpt.get().getUsuario().getIdUsuario(),
                request.getIdReporte(),
                "Tu reporte '" + reporteOpt.get().getTitulo() + "' fue asignado a una cuadrilla"
        );

        return new ApiResponse(true, "Reporte asignado correctamente");
    }

    @Transactional
    public ApiResponse registrarVoto(VotacionRequest request, int idUsuario) {

        Optional<Voluntario> voluntarioOpt = voluntarioRepository.findIdByUsuario(idUsuario)
                .map(o -> ((Number) o).intValue())
                .flatMap(id -> voluntarioRepository.findById(id));
        if (voluntarioOpt.isEmpty()) {
            return new ApiResponse(false, "No eres voluntario");
        }

        int idVoluntario = voluntarioOpt.get().getIdVoluntario();

        Optional<Reporte> reporteOpt = reporteRepository.findById(request.getIdReporte());
        if (reporteOpt.isEmpty()) {
            return new ApiResponse(false, "Reporte no encontrado");
        }

        Optional<detallesReporte> detallesOpt = detallesReporteRepository
                .findByReporte(request.getIdReporte());
        if (detallesOpt.isEmpty() || !detallesOpt.get().getEstado().equals("Asignado")) {
            return new ApiResponse(false, "El reporte no está en estado de votación");
        }

        List<miembrosCuadrilla> pertenece = miembrosCuadrillaRepository
                .findByReporteAndVoluntario(request.getIdReporte(), idVoluntario);
        if (pertenece.isEmpty()) {
            return new ApiResponse(false, "No perteneces a la cuadrilla asignada a este reporte");
        }

        if (votacionRepository.findByReporteAndVoluntario(
                request.getIdReporte(), idVoluntario).isPresent()) {
            return new ApiResponse(false, "Ya registraste tu voto para este reporte");
        }

        Optional<reporteAsignadoCuadrilla> asignacionOpt = asignacionRepository
                .findByReporte(request.getIdReporte());
        if (asignacionOpt.isEmpty()) {
            return new ApiResponse(false, "No se encontró la asignación del reporte");
        }

        int idCuadrilla = asignacionOpt.get().getIdCuadrilla().getIdCuadrilla();

        Votacion voto = new Votacion();
        voto.setIdReporte(reporteOpt.get());
        voto.setIdCuadrilla(asignacionOpt.get().getIdCuadrilla());
        voto.setIdVoluntario(voluntarioOpt.get());
        voto.setRespuesta(request.getRespuesta());
        voto.setFechaVoto(new java.sql.Timestamp(System.currentTimeMillis()));
        votacionRepository.save(voto);

        Optional<Votacion> votoLider = votacionRepository
                .findLiderVoteByReporte(request.getIdReporte());

        if (votoLider.isPresent() && votoLider.get().getRespuesta().equals("rechazar")) {
            detallesReporteRepository.updateEstado(request.getIdReporte(), "Pendiente");
            registrarHistorial(reporteOpt.get(), idUsuario, "Asignado", "Pendiente");

            notificacionService.enviarNotificacion(
                    reporteOpt.get().getUsuario().getIdUsuario(),
                    request.getIdReporte(),
                    "La cuadrilla no pudo atender tu reporte, será reasignado"
            );
            return new ApiResponse(true, "Voto registrado. La cuadrilla rechazó el reporte");
        }

        // Verificar mayoría (3 de 5)
        int votosAceptados = votacionRepository.countAcceptedVotes(
                request.getIdReporte(), idCuadrilla);

        if (votosAceptados >= 3) {
            detallesReporteRepository.updateEstado(request.getIdReporte(), "En camino");
            registrarHistorial(reporteOpt.get(), idUsuario, "Asignado", "En camino");

            notificacionService.enviarNotificacion(
                    reporteOpt.get().getUsuario().getIdUsuario(),
                    request.getIdReporte(),
                    "La cuadrilla aceptó tu reporte y va en camino"
            );
            return new ApiResponse(true, "Mayoría alcanzada. El reporte pasa a En camino");
        }

        return new ApiResponse(true, "Voto registrado correctamente");
    }

    // Módulo 3.4 - Líder marca que la cuadrilla llegó al lugar (En camino → En curso)
    @Transactional
    public ApiResponse iniciarAtencion(int idReporte, int idUsuario) {

        if (!esLiderDelReporte(idReporte, idUsuario)) {
            return new ApiResponse(false, "Solo el líder de la cuadrilla puede cambiar este estado");
        }

        Optional<detallesReporte> detallesOpt = detallesReporteRepository.findByReporte(idReporte);
        if (detallesOpt.isEmpty() || !detallesOpt.get().getEstado().equals("En camino")) {
            return new ApiResponse(false, "El reporte debe estar en estado En camino");
        }

        Reporte reporte = reporteRepository.findById(idReporte).get();
        detallesReporteRepository.updateEstado(idReporte, "En curso");
        registrarHistorial(reporte, idUsuario, "En camino", "En curso");

        notificacionService.enviarNotificacion(
                reporte.getUsuario().getIdUsuario(),
                idReporte,
                "La cuadrilla ya está atendiendo tu reporte"
        );

        return new ApiResponse(true, "Estado actualizado a En curso");
    }

    // Módulo 3.6 - Líder finaliza el reporte con evidencia fotográfica
    @Transactional
    public ApiResponse finalizarReporte(int idReporte, int idUsuario,
                                        FinalizarReporteRequest request,
                                        MultipartFile fotoEvidencia) throws IOException {

        if (!esLiderDelReporte(idReporte, idUsuario)) {
            return new ApiResponse(false, "Solo el líder de la cuadrilla puede finalizar el reporte");
        }

        Optional<detallesReporte> detallesOpt = detallesReporteRepository.findByReporte(idReporte);
        if (detallesOpt.isEmpty() || !detallesOpt.get().getEstado().equals("En curso")) {
            return new ApiResponse(false, "El reporte debe estar en estado En curso");
        }

        if (fotoEvidencia == null || fotoEvidencia.isEmpty()) {
            return new ApiResponse(false, "La foto de evidencia es obligatoria");
        }

        Optional<reporteAsignadoCuadrilla> asignacionOpt = asignacionRepository
                .findByReporte(idReporte);
        if (asignacionOpt.isEmpty()) {
            return new ApiResponse(false, "No se encontró la asignación del reporte");
        }

        reporteFinalizado finalizado = new reporteFinalizado();
        finalizado.setIdReporte(reporteRepository.findById(idReporte).get());
        finalizado.setFotoEvidencia(fotoEvidencia.getBytes());
        finalizado.setComentarios(request.getComentarios());
        finalizado.setIdCuadrillaEncargada(asignacionOpt.get().getIdCuadrilla());
        finalizado.setFechaFinalizacion(new Date());
        reporteFinalizadoRepository.save(finalizado);

        Reporte reporte = reporteRepository.findById(idReporte).get();
        detallesReporteRepository.updateEstado(idReporte, "Finalizado");
        registrarHistorial(reporte, idUsuario, "En curso", "Finalizado");

        notificacionService.enviarNotificacion(
                reporte.getUsuario().getIdUsuario(),
                idReporte,
                "Tu reporte '" + reporte.getTitulo() + "' fue atendido exitosamente"
        );

        return new ApiResponse(true, "Reporte finalizado correctamente");
    }


    private boolean esLiderDelReporte(int idReporte, int idUsuario) {
        Optional<reporteAsignadoCuadrilla> asignacionOpt = asignacionRepository
                .findByReporte(idReporte);
        if (asignacionOpt.isEmpty()) return false;

        Voluntario lider = asignacionOpt.get().getIdCuadrilla().getVoluntario();
        return lider.getUsuario().getIdUsuario() == idUsuario;
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
}