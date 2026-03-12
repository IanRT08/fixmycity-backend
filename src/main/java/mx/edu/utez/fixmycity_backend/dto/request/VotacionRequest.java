package mx.edu.utez.fixmycity_backend.dto.request;

import jakarta.validation.constraints.*;

public class VotacionRequest {

    @NotNull(message = "El reporte es obligatorio")
    private Integer idReporte;

    @NotBlank(message = "La respuesta es obligatoria")
    @Pattern(regexp = "^(aceptar|rechazar)$", message = "La respuesta debe ser 'aceptar' o 'rechazar'")
    private String respuesta;

    public Integer getIdReporte() { return idReporte; }
    public void setIdReporte(Integer idReporte) {
        this.idReporte = idReporte;
    }

    public String getRespuesta() {
        return respuesta;
    }
    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }
}