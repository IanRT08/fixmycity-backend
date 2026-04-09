package mx.edu.utez.fixmycity_backend.services;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import mx.edu.utez.fixmycity_backend.modelos.DispositivoFcm;
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
        if (!firebaseListo()) {
            log.info("FCM omitido (Firebase no inicializado) tipo=GENERAL usuario={}", idUsuario);
            return;
        }
        log.info("FCM preparar envio tipo=GENERAL usuario={}", idUsuario);
        enviarATokens(dispositivoFcmRepository.findAllByIdUsuario(idUsuario), TITLE_APP, cuerpo,
                "GENERAL", null);
    }

    /**
     * Notificación ligada a un reporte (asignación, cambio de estado, etc.).
     * idDueno: idUsuario del dueño del reporte (extraído dentro de la transacción para evitar LazyInitializationException).
     */
    @Async
    public void enviarSobreReporteAsync(int idUsuarioDestino, int idReporte, String cuerpo, int idDueno) {
        if (!firebaseListo()) {
            log.info("FCM omitido (Firebase no inicializado) tipo=REPORTE usuario={} reporte={}",
                    idUsuarioDestino, idReporte);
            return;
        }
        boolean esDueno = idDueno == idUsuarioDestino;
        if (esDueno && !prefCambioEstadoActiva(idUsuarioDestino)) {
            log.info("FCM omitido por preferencia notifCambioEstado=0 usuario={} reporte={}",
                    idUsuarioDestino, idReporte);
            return;
        }
        log.info("FCM preparar envio tipo=REPORTE usuario={} reporte={}", idUsuarioDestino, idReporte);
        enviarATokens(dispositivoFcmRepository.findAllByIdUsuario(idUsuarioDestino), TITLE_APP, cuerpo,
                "REPORTE", idReporte);
    }

    /**
     * Otro usuario publicó un reporte en el mismo municipio (el autor no recibe).
     */
    @Async
    public void enviarNuevoReporteEnMunicipioAsync(int idMunicipio, int idReporte, String tituloReporte,
                                                   int idAutorUsuario) {
        if (!firebaseListo()) {
            log.info("FCM omitido (Firebase no inicializado) tipo=NUEVO_EN_ZONA municipio={} reporte={}",
                    idMunicipio, idReporte);
            return;
        }
        List<Object> ids = usuarioRepository.findIdsByMunicipioActivoExcluyendo(idMunicipio, idAutorUsuario);
        log.info("FCM destinatarios calculados tipo=NUEVO_EN_ZONA municipio={} reporte={} total={}",
                idMunicipio, idReporte, ids.size());
        String cuerpo = "Nuevo reporte en tu zona: " + tituloReporte;
        for (Object o : ids) {
            int uid = ((Number) o).intValue();
            if (!prefNuevosZonaActiva(uid)) {
                log.info("FCM omitido por preferencia notifNuevosZona=0 usuario={} reporte={}", uid, idReporte);
                continue;
            }
            enviarATokens(dispositivoFcmRepository.findAllByIdUsuario(uid), TITLE_APP, cuerpo,
                    "NUEVO_EN_ZONA", idReporte);
        }
    }

    private void enviarATokens(List<DispositivoFcm> dispositivos, String titulo, String cuerpo,
                               String tipoData, Integer idReporte) {
        if (dispositivos == null || dispositivos.isEmpty()) {
            log.info("FCM sin tokens para enviar tipo={} reporte={}", tipoData, idReporte);
            return;
        }
        log.info("FCM enviando tipo={} reporte={} tokens={}", tipoData, idReporte, dispositivos.size());
        FirebaseMessaging fm = FirebaseMessaging.getInstance();
        for (DispositivoFcm d : dispositivos) {
            String token = d.getTokenFcm();
            if (token == null || token.isBlank()) continue;
            try {
                Message.Builder mb = Message.builder()
                        .setToken(token)
                        .setNotification(Notification.builder().setTitle(titulo).setBody(cuerpo).build())
                        .setAndroidConfig(AndroidConfig.builder()
                                .setPriority(AndroidConfig.Priority.HIGH)
                                .build())
                        .putData("type", tipoData);
                if (idReporte != null) {
                    mb.putData("idReporte", String.valueOf(idReporte));
                }
                String messageId = fm.send(mb.build());
                log.info("FCM enviado ok tipo={} reporte={} token={} messageId={}",
                        tipoData, idReporte, maskToken(token), messageId);
            } catch (Exception e) {
                log.warn("FCM falló tipo={} reporte={} token={}: {}",
                        tipoData, idReporte, maskToken(token), e.getMessage());
            }
        }
    }

    private String maskToken(String token) {
        if (token == null || token.isBlank()) return "(vacio)";
        if (token.length() <= 12) return token;
        return token.substring(0, 8) + "..." + token.substring(token.length() - 4);
    }
}
