package mx.edu.utez.fixmycity_backend.dto.request;

import jakarta.validation.constraints.*;

public class ReporteRequest {

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 60, message = "El título no puede exceder 60 caracteres")
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    private String descripcion;

    @NotNull(message = "El municipio es obligatorio")
    private Integer idMunicipio;

    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getIdMunicipio() {
        return idMunicipio;
    }
    public void setIdMunicipio(Integer idMunicipio) {
        this.idMunicipio = idMunicipio;
    }
}