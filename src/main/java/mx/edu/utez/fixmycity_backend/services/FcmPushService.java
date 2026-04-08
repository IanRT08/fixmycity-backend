package mx.edu.utez.fixmycity_backend.services;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import mx.edu.utez.fixmycity_backend.modelos.DispositivoFcm;
import mx.edu.utez.fixmycity_backend.modelos.Reporte;
import mx.edu.utez.fixmycity_backend.repositories.DispositivoFcmRepository;
import mx.edu.utez.fixmycity_backend.repositories.PreferenciasNotificacionRepository;
import mx.edu.utez.fixmycity_backend.repositories.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Envío FCM. Si Firebase no está inicializado, los métodos no hacen nada.
 * Preferencias: dueño del reporte respeta NOTIF_CAMBIO_ESTADO; otros destinatarios (p. ej. voluntarios) reciben push si hay token.
 * Nuevos reportes en municipio: NOTIF_NUEVOS_ZONA; no se notifica al autor.
 */
@Service
public class FcmPushService {

    private static final Logger log = LoggerFactory.getLogger(FcmPushService.class);
    private static final int ON = 1;
    private static final String TITLE_APP = "FixMyCity";

    private final PreferenciasNotificacionRepository preferenciasRepository;
    private final DispositivoFcmRepository dispositivoFcmRepository;
    private final UsuarioRepository usuarioRepository;

    public FcmPushService(PreferenciasNotificacionRepository preferenciasRepository,
                          DispositivoFcmRepository dispositivoFcmRepository,
                          UsuarioRepository usuarioRepository) {
        this.preferenciasRepository = preferenciasRepository;
        this.dispositivoFcmRepository = dispositivoFcmRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private boolean firebaseListo() {
        return !FirebaseApp.getApps().isEmpty();
    }

    private boolean prefCambioEstadoActiva(int idUsuario) {
        return preferenciasRepository.findByIdUsuario(idUsuario)
                .map(p -> p.getNotifCambioEstado() == ON)
                .orElse(true);
    }

    private boolean prefNuevosZonaActiva(int idUsuario) {
        return preferenciasRepository.findByIdUsuario(idUsuario)
                .map(p -> p.getNotifNuevosZona() == ON)
                .orElse(true);
    }

    @Async
    public void enviarSimpleAsync(int idUsuario, String cuerpo) {
        if (!firebaseListo()) return;
        enviarATokens(dispositivoFcmRepository.findAllByIdUsuario(idUsuario), TITLE_APP, cuerpo,
                "GENERAL", null);
    }

    /**
     * Notificación ligada a un reporte (asignación, cambio de estado, etc.).
     */
    @Async
    public void enviarSobreReporteAsync(int idUsuarioDestino, int idReporte, String cuerpo, Reporte reporte) {
        if (!firebaseListo()) return;
        boolean esDueno = reporte.getUsuario().getIdUsuario() == idUsuarioDestino;
        if (esDueno && !prefCambioEstadoActiva(idUsuarioDestino)) {
            return;
        }
        enviarATokens(dispositivoFcmRepository.findAllByIdUsuario(idUsuarioDestino), TITLE_APP, cuerpo,
                "REPORTE", idReporte);
    }

    /**
     * Otro usuario publicó un reporte en el mismo municipio (el autor no recibe).
     */
    @Async
    public void enviarNuevoReporteEnMunicipioAsync(int idMunicipio, int idReporte, String tituloReporte,
                                                   int idAutorUsuario) {
        if (!firebaseListo()) return;
        List<Object> ids = usuarioRepository.findIdsByMunicipioActivoExcluyendo(idMunicipio, idAutorUsuario);
        String cuerpo = "Nuevo reporte en tu zona: " + tituloReporte;
        for (Object o : ids) {
            int uid = ((Number) o).intValue();
            if (!prefNuevosZonaActiva(uid)) continue;
            enviarATokens(dispositivoFcmRepository.findAllByIdUsuario(uid), TITLE_APP, cuerpo,
                    "NUEVO_EN_ZONA", idReporte);
        }
    }

    private void enviarATokens(List<DispositivoFcm> dispositivos, String titulo, String cuerpo,
                               String tipoData, Integer idReporte) {
        if (dispositivos == null || dispositivos.isEmpty()) return;
        FirebaseMessaging fm = FirebaseMessaging.getInstance();
        for (DispositivoFcm d : dispositivos) {
            String token = d.getTokenFcm();
            if (token == null || token.isBlank()) continue;
            try {
                Message.Builder mb = Message.builder()
                        .setToken(token)
                        .setNotification(Notification.builder().setTitle(titulo).setBody(cuerpo).build())
                        .putData("type", tipoData);
                if (idReporte != null) {
                    mb.putData("idReporte", String.valueOf(idReporte));
                }
                fm.send(mb.build());
            } catch (Exception e) {
                log.warn("FCM falló para token …{}: {}", token.length() > 12 ? token.substring(0, 8) : token, e.getMessage());
            }
        }
    }
}
