package mx.edu.utez.fixmycity_backend.services;

import mx.edu.utez.fixmycity_backend.dto.request.MunicipioRequest;
import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.dto.response.MunicipioResponse;
import mx.edu.utez.fixmycity_backend.modelos.Municipios;
import mx.edu.utez.fixmycity_backend.repositories.MunicipioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MunicipioService {

    private final MunicipioRepository municipioRepository;

    public MunicipioService(MunicipioRepository municipioRepository) {
        this.municipioRepository = municipioRepository;
    }
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
    public ApiResponse crearMunicipio(MunicipioRequest request) {
        if (municipioRepository.findByNombre(request.getNombre()).isPresent()) {
            return new ApiResponse(false, "Ya existe un municipio con ese nombre");
        }

        Municipios municipio = new Municipios();
        municipio.setNombre(request.getNombre());
        municipio.setEstado(request.getEstado() != null ? request.getEstado() : "Activo");

        municipioRepository.save(municipio);
        return new ApiResponse(true, "Municipio creado correctamente");
    }

    @Transactional
    public ApiResponse editarMunicipio(int id, MunicipioRequest request) {
        Optional<Municipios> municipioOpt = municipioRepository.findById(id);
        if (municipioOpt.isEmpty()) {
            return new ApiResponse(false, "Municipio no encontrado");
        }

        if (request.getNombre() != null) {
            Optional<Municipios> existente = municipioRepository.findByNombre(request.getNombre());
            if (existente.isPresent() && existente.get().getIdMunicipio() != id) {
                return new ApiResponse(false, "Ya existe un municipio con ese nombre");
            }
            municipioRepository.updateNombre(id, request.getNombre());
        }

        if (request.getEstado() != null) {
            int filasAfectadas = municipioRepository.updateEstado(id, request.getEstado());
            if (filasAfectadas == 0) {
                return new ApiResponse(false, "No se puede deshabilitar el municipio porque tiene reportes en proceso");
            }
        }

        return new ApiResponse(true, "Municipio actualizado correctamente");
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