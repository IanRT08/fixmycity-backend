package mx.edu.utez.fixmycity_backend.services;

import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.dto.response.NotificacionResponse;
import mx.edu.utez.fixmycity_backend.modelos.Notificaciones;
import mx.edu.utez.fixmycity_backend.modelos.Usuario;
import mx.edu.utez.fixmycity_backend.repositories.NotificacionesRepository;
import mx.edu.utez.fixmycity_backend.repositories.ReporteRepository;
import mx.edu.utez.fixmycity_backend.repositories.UsuarioRepository;
import mx.edu.utez.fixmycity_backend.support.TransactionAfterCommit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mx.edu.utez.fixmycity_backend.modelos.Reporte;


import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificacionesService {

    @Autowired
    private NotificacionesRepository notificacionesRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ReporteRepository reporteRepository;

    @Autowired
    private FcmPushService fcmPushService;

    @Autowired
    private TransactionAfterCommit afterCommit;

    @Transactional
    public void enviarNotificacion(int idUsuario, Integer idReporte, String mensaje) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
        if (usuarioOpt.isEmpty()) return;

        if (idReporte == null) {
            afterCommit.run(() -> fcmPushService.enviarSimpleAsync(idUsuario, mensaje));
            return;
        }

        Optional<Reporte> reporteOpt = reporteRepository.findById(idReporte);
        if (reporteOpt.isEmpty()) return;

        Notificaciones notificacion = new Notificaciones();
        notificacion.setIdUsuario(usuarioOpt.get());
        notificacion.setMensaje(mensaje);
        notificacion.setLeida(false);
        notificacion.setFechaEnvio(Timestamp.from(Instant.now()));
        notificacion.setIdReporte(reporteOpt.get());

        notificacionesRepository.save(notificacion);

        int idDueno = reporteOpt.get().getUsuario().getIdUsuario();
        afterCommit.run(() -> fcmPushService.enviarSobreReporteAsync(idUsuario, idReporte, mensaje, idDueno));
    }

    public ApiResponse obtenerNotificaciones(int idUsuario) {
        List<Notificaciones> notificaciones = notificacionesRepository.findByUsuario(idUsuario);

        List<NotificacionResponse> response = notificaciones.stream()
                .map(n -> new NotificacionResponse(
                        n.getIdNotificacion(),
                        n.getMensaje(),
                        n.isLeida(),
                        n.getFechaEnvio(),
                        n.getIdReporte() != null ? n.getIdReporte().getIdReporte() : 0
                ))
                .collect(Collectors.toList());

        return new ApiResponse(true, "Notificaciones obtenidas correctamente", response);
    }

    public ApiResponse obtenerNoLeidas(int idUsuario) {
        List<Notificaciones> notificaciones = notificacionesRepository.findUnreadByUsuario(idUsuario);

        List<NotificacionResponse> response = notificaciones.stream()
                .map(n -> new NotificacionResponse(
                        n.getIdNotificacion(),
                        n.getMensaje(),
                        n.isLeida(),
                        n.getFechaEnvio(),
                        n.getIdReporte() != null ? n.getIdReporte().getIdReporte() : 0
                ))
                .collect(Collectors.toList());

        return new ApiResponse(true, "Notificaciones no leídas obtenidas correctamente", response);
    }

    @Transactional
    public ApiResponse marcarComoLeida(int idNotificacion, int idUsuario) {

        Optional<Notificaciones> notificacionOpt = notificacionesRepository.findById(idNotificacion);
        if (notificacionOpt.isEmpty()) {
            return new ApiResponse(false, "Notificación no encontrada");
        }
        if (notificacionOpt.get().getIdUsuario().getIdUsuario() != idUsuario) {
            return new ApiResponse(false, "No tienes permiso para modificar esta notificación");
        }

        notificacionesRepository.markAsRead(idNotificacion, idUsuario);
        return new ApiResponse(true, "Notificación marcada como leída");
    }
}