package mx.edu.utez.fixmycity_backend.services;

import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.dto.response.MunicipioResponse;
import mx.edu.utez.fixmycity_backend.modelos.Municipios;
import mx.edu.utez.fixmycity_backend.repositories.MunicipioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MunicipioService {

    @Autowired
    private MunicipioRepository municipioRepository;
    public ApiResponse listarTodos() {
        List<Municipios> municipios = municipioRepository.findAll();

        List<MunicipioResponse> response = municipios.stream()
                .map(m -> new MunicipioResponse(
                        m.getIdMunicipio(),
                        m.getNombre(),
                        m.getEstado()))
                .collect(Collectors.toList());

        return new ApiResponse(true, "Municipios obtenidos correctamente", response);
    }
    public ApiResponse listarActivos() {
        List<Municipios> municipios = municipioRepository.findByEstado("Activo");

        List<MunicipioResponse> response = municipios.stream()
                .map(m -> new MunicipioResponse(
                        m.getIdMunicipio(),
                        m.getNombre(),
                        m.getEstado()))
                .collect(Collectors.toList());

        return new ApiResponse(true, "Municipios activos obtenidos correctamente", response);
    }

    @Transactional
    public ApiResponse cambiarEstado(int idMunicipio, String estado) {

        Optional<Municipios> municipioOpt = municipioRepository.findById(idMunicipio);
        if (municipioOpt.isEmpty()) {
            return new ApiResponse(false, "Municipio no encontrado");
        }

        int filasAfectadas = municipioRepository.updateEstado(idMunicipio, estado);

        if (filasAfectadas == 0) {
            return new ApiResponse(false,
                    "No se puede deshabilitar el municipio porque tiene reportes en proceso");
        }

        return new ApiResponse(true, "Estado del municipio actualizado correctamente");
    }
}