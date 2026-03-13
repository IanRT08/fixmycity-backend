package mx.edu.utez.fixmycity_backend.services;

import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.dto.response.HistorialReporteResponse;
import mx.edu.utez.fixmycity_backend.modelos.historialReporte;
import mx.edu.utez.fixmycity_backend.repositories.ReporteRepository;
import mx.edu.utez.fixmycity_backend.repositories.historialReporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistorialService {

    @Autowired
    private historialReporteRepository historialReporteRepository;

    @Autowired
    private ReporteRepository reporteRepository;

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