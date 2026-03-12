package mx.edu.utez.fixmycity_backend.dto.request;

import jakarta.validation.constraints.*;
import java.util.List;

public class CuadrillaRequest {

    @NotBlank(message = "El nombre de la cuadrilla es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    private String nombreCuadrilla;

    @NotNull(message = "El municipio es obligatorio")
    private Integer idMunicipio;

    @NotNull(message = "Los miembros son obligatorios")
    @Size(min = 5, max = 5, message = "La cuadrilla debe tener exactamente 5 integrantes")
    private List<Integer> idMiembros;

    @NotNull(message = "Se debe seleccionar un lider de manera obligatoria")
    private Integer idLider;

    public String getNombreCuadrilla() {
        return nombreCuadrilla;
    }
    public void setNombreCuadrilla(String nombreCuadrilla) {
        this.nombreCuadrilla = nombreCuadrilla;
    }

    public Integer getIdMunicipio() {
        return idMunicipio;
    }
    public void setIdMunicipio(Integer idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    public List<Integer> getIdMiembros() {
        return idMiembros;
    }
    public void setIdMiembros(List<Integer> idMiembros) {
        this.idMiembros = idMiembros;
    }

    public Integer getIdLider() {
        return idLider;
    }
    public void setIdLider(Integer idLider) {
        this.idLider = idLider;
    }
}