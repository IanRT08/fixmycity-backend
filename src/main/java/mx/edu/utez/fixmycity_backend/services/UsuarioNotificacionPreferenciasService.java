package mx.edu.utez.fixmycity_backend.services;

import mx.edu.utez.fixmycity_backend.dto.request.FcmTokenRequest;
import mx.edu.utez.fixmycity_backend.dto.request.PreferenciasNotificacionUpdateRequest;
import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.dto.response.PreferenciasNotificacionResponse;
import mx.edu.utez.fixmycity_backend.modelos.DispositivoFcm;
import mx.edu.utez.fixmycity_backend.modelos.PreferenciasNotificacion;
import mx.edu.utez.fixmycity_backend.repositories.DispositivoFcmRepository;
import mx.edu.utez.fixmycity_backend.repositories.PreferenciasNotificacionRepository;
import mx.edu.utez.fixmycity_backend.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioNotificacionPreferenciasService {

    private static final int DEFAULT_ON = 1;

    private final PreferenciasNotificacionRepository preferenciasRepository;
    private final DispositivoFcmRepository dispositivoFcmRepository;
    private final UsuarioRepository usuarioRepository;

    public UsuarioNotificacionPreferenciasService(
            PreferenciasNotificacionRepository preferenciasRepository,
            DispositivoFcmRepository dispositivoFcmRepository,
            UsuarioRepository usuarioRepository) {
        this.preferenciasRepository = preferenciasRepository;
        this.dispositivoFcmRepository = dispositivoFcmRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public ApiResponse obtenerPreferencias(int idUsuario) {
        if (usuarioRepository.findById(idUsuario).isEmpty()) {
            return new ApiResponse(false, "Usuario no encontrado");
        }
        Optional<PreferenciasNotificacion> opt = preferenciasRepository.findByIdUsuario(idUsuario);
        if (opt.isEmpty()) {
            return new ApiResponse(true, "Preferencias (valores por defecto)",
                    new PreferenciasNotificacionResponse(DEFAULT_ON, DEFAULT_ON));
        }
        PreferenciasNotificacion p = opt.get();
        return new ApiResponse(true, "Preferencias obtenidas correctamente",
                new PreferenciasNotificacionResponse(p.getNotifCambioEstado(), p.getNotifNuevosZona()));
    }

    @Transactional
    public ApiResponse actualizarPreferencias(int idUsuario, PreferenciasNotificacionUpdateRequest req) {
        if (usuarioRepository.findById(idUsuario).isEmpty()) {
            return new ApiResponse(false, "Usuario no encontrado");
        }
        if (req.getNotifCambioEstado() == null && req.getNotifNuevosZona() == null) {
            return new ApiResponse(false, "Envía al menos un campo: notifCambioEstado o notifNuevosZona");
        }

        PreferenciasNotificacion ent = preferenciasRepository.findByIdUsuario(idUsuario)
                .orElseGet(() -> {
                    PreferenciasNotificacion n = new PreferenciasNotificacion();
                    n.setIdUsuario(idUsuario);
                    n.setNotifCambioEstado(DEFAULT_ON);
                    n.setNotifNuevosZona(DEFAULT_ON);
                    return n;
                });

        if (req.getNotifCambioEstado() != null) {
            ent.setNotifCambioEstado(req.getNotifCambioEstado());
        }
        if (req.getNotifNuevosZona() != null) {
            ent.setNotifNuevosZona(req.getNotifNuevosZona());
        }
        preferenciasRepository.save(ent);

        return new ApiResponse(true, "Preferencias actualizadas",
                new PreferenciasNotificacionResponse(ent.getNotifCambioEstado(), ent.getNotifNuevosZona()));
    }

    @Transactional
    public ApiResponse registrarTokenFcm(int idUsuario, FcmTokenRequest req) {
        if (usuarioRepository.findById(idUsuario).isEmpty()) {
            return new ApiResponse(false, "Usuario no encontrado");
        }
        String token = req.getToken().trim();
        if (token.isEmpty()) {
            return new ApiResponse(false, "El token FCM es obligatorio");
        }

        dispositivoFcmRepository.deleteByTokenFcm(token);

        DispositivoFcm d = new DispositivoFcm();
        d.setIdUsuario(idUsuario);
        d.setTokenFcm(token);
        d.setPlataforma(req.getPlataforma() != null ? req.getPlataforma().trim() : null);
        dispositivoFcmRepository.save(d);

        return new ApiResponse(true, "Token FCM registrado");
    }

    @Transactional
    public ApiResponse eliminarTokenFcm(int idUsuario, FcmTokenRequest req) {
        String token = req.getToken() != null ? req.getToken().trim() : "";
        if (token.isEmpty()) {
            return new ApiResponse(false, "El token FCM es obligatorio");
        }
        Optional<DispositivoFcm> row = dispositivoFcmRepository.findByTokenFcm(token);
        if (row.isEmpty() || row.get().getIdUsuario() != idUsuario) {
            return new ApiResponse(false, "Token no encontrado para este usuario");
        }
        dispositivoFcmRepository.delete(row.get());
        return new ApiResponse(true, "Token FCM eliminado");
    }
}
