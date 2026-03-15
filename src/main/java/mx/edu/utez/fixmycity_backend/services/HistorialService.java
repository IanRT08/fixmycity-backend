package mx.edu.utez.fixmycity_backend.services;

import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.dto.response.HistorialReporteResponse;
import mx.edu.utez.fixmycity_backend.modelos.Usuario;
import mx.edu.utez.fixmycity_backend.modelos.detallesReporte;
import mx.edu.utez.fixmycity_backend.modelos.historialReporte;
import mx.edu.utez.fixmycity_backend.repositories.ReporteRepository;
import mx.edu.utez.fixmycity_backend.repositories.UsuarioRepository;
import mx.edu.utez.fixmycity_backend.repositories.detallesReporteRepository;
import mx.edu.utez.fixmycity_backend.repositories.historialReporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HistorialService {

    @Autowired
    private historialReporteRepository historialReporteRepository;

    @Autowired
    private ReporteRepository reporteRepository;

    @Autowired
    private detallesReporteRepository detallesReporteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public ApiResponse obtenerHistorialAdmin(int idReporte, int idAdmin) {
        Optional<Usuario> adminOpt = usuarioRepository.findById(idAdmin);
        if (adminOpt.isEmpty()) {
            return new ApiResponse(false, "Administrador no encontrado");
        }

        if (reporteRepository.findById(idReporte).isEmpty()) {
            return new ApiResponse(false, "Reporte no encontrado");
        }

        Usuario admin = adminOpt.get();
        if (!admin.getTipo().equals("superadmin")) {
            Optional<detallesReporte> detallesOpt = detallesReporteRepository.findByReporte(idReporte);
            if (detallesOpt.isEmpty() ||
                    detallesOpt.get().getMunicipios().getIdMunicipio() != admin.getMunicipio().getIdMunicipio()) {
                return new ApiResponse(false, "No tienes permiso para ver este reporte");
            }
        }

        List<historialReporte> historial = historialReporteRepository.findByReporte(idReporte);
        List<HistorialReporteResponse> response = historial.stream()
                .map(h -> new HistorialReporteResponse(
                        h.getIdCambioEstado(),
                        h.getEstadoAnterior(),
                        h.getEstadoNuevo(),
                        h.getFechaCambio(),
                        h.getUsuario().getNombreUsuario()
                ))
                .collect(Collectors.toList());

        return new ApiResponse(true, "Historial obtenido correctamente", response);
    }

    public ApiResponse obtenerHistorial(int idReporte) {

        if (reporteRepository.findById(idReporte).isEmpty()) {
            return new ApiResponse(false, "Reporte no encontrado");
        }

        List<historialReporte> historial = historialReporteRepository.findByReporte(idReporte);

        List<HistorialReporteResponse> response = historial.stream()
                .map(h -> new HistorialReporteResponse(
                        h.getIdCambioEstado(),
                        h.getEstadoAnterior(),
                        h.getEstadoNuevo(),
                        h.getFechaCambio(),
                        h.getUsuario().getNombreUsuario()
                ))
                .collect(Collectors.toList());

        return new ApiResponse(true, "Historial obtenido correctamente", response);
    }
}