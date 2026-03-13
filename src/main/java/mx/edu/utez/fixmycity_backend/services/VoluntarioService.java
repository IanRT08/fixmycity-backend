package mx.edu.utez.fixmycity_backend.services;

import mx.edu.utez.fixmycity_backend.dto.request.SolicitudVoluntarioRequest;
import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.dto.response.SolicitudVoluntarioResponse;
import mx.edu.utez.fixmycity_backend.modelos.*;
import mx.edu.utez.fixmycity_backend.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VoluntarioService {

    @Autowired
    private SolicitudVoluntarioRepository solicitudRepository;

    @Autowired
    private informacionVoluntarioRepository informacionRepository;

    @Autowired
    private VoluntarioRepository voluntarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MunicipioRepository municipioRepository;

    @Autowired
    private NotificacionesService notificacionService;

    @Transactional
    public ApiResponse solicitarVoluntario(int idUsuario, SolicitudVoluntarioRequest request) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
        if (usuarioOpt.isEmpty()) {
            return new ApiResponse(false, "Usuario no encontrado");
        }

        if (solicitudRepository.findPendingByUsuario(idUsuario).isPresent()) {
            return new ApiResponse(false, "Ya existe una solicitud en proceso");
        }

        if (informacionRepository.findByCURP(request.getCurp()).isPresent()) {
            return new ApiResponse(false, "El CURP ya está registrado en el sistema");
        }

        solicitudVoluntario solicitud = new solicitudVoluntario();
        solicitud.setUsuario(usuarioOpt.get()); // solicitudVoluntario usa setUsuario()
        solicitud.setEstado("pendiente");
        solicitudRepository.save(solicitud);

        informacionVoluntario info = new informacionVoluntario();
        info.setSolicitudVoluntario(solicitud);
        info.setNombre(request.getNombre());
        info.setCURP(request.getCurp());
        info.setTelefono(request.getTelefono());
        info.setDescripcion(request.getDescripcion());
        informacionRepository.save(info);

        return new ApiResponse(true, "Solicitud enviada correctamente");
    }

    public ApiResponse listarSolicitudesPendientes() {
        List<solicitudVoluntario> solicitudes = solicitudRepository.findByEstado("pendiente");

        List<SolicitudVoluntarioResponse> response = solicitudes.stream().map(s -> {
            informacionVoluntario info = informacionRepository
                    .findBySolicitud(s.getIdSolicitud()).orElse(null);
            return new SolicitudVoluntarioResponse(
                    s.getIdSolicitud(),
                    s.getUsuario().getNombreUsuario(),
                    info != null ? info.getNombre() : "",
                    info != null ? info.getCURP() : "",
                    info != null ? info.getTelefono() : "",
                    info != null ? info.getDescripcion() : "",
                    s.getEstado()
            );
        }).collect(Collectors.toList());

        return new ApiResponse(true, "Solicitudes obtenidas correctamente", response);
    }

    @Transactional
    public ApiResponse responderSolicitud(int idSolicitud, String decision) {

        Optional<solicitudVoluntario> solicitudOpt = solicitudRepository.findById(idSolicitud);
        if (solicitudOpt.isEmpty()) {
            return new ApiResponse(false, "Solicitud no encontrada");
        }

        solicitudVoluntario solicitud = solicitudOpt.get();

        if (!solicitud.getEstado().equals("pendiente")) {
            return new ApiResponse(false, "La solicitud ya fue procesada");
        }

        solicitudRepository.updateEstado(idSolicitud, decision);

        if (decision.equals("aprobado")) {
            Voluntario voluntario = new Voluntario();
            voluntario.setUsuario(solicitud.getUsuario());
            voluntarioRepository.save(voluntario);

            usuarioRepository.updateTipo(solicitud.getUsuario().getIdUsuario(), "voluntario");

            notificacionService.enviarNotificacion(
                    solicitud.getUsuario().getIdUsuario(),
                    null,
                    "Tu solicitud para ser voluntario fue aprobada"
            );
            return new ApiResponse(true, "Solicitud aprobada correctamente");
        }

        notificacionService.enviarNotificacion(
                solicitud.getUsuario().getIdUsuario(),
                null,
                "Tu solicitud para ser voluntario fue rechazada"
        );
        return new ApiResponse(true, "Solicitud rechazada correctamente");
    }
}