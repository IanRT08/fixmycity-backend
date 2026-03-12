package mx.edu.utez.fixmycity_backend.dto.request;

import jakarta.validation.constraints.*;

public class CancelacionReporteRequest {

    @NotBlank(message = "El motivo de cancelación es obligatorio")
    @Size(max = 255, message = "El motivo no puede exceder 255 caracteres")
    private String motivoCancelacion;

    public String getMotivoCancelacion() {
        return motivoCancelacion;
    }
    public void setMotivoCancelacion(String motivoCancelacion) {
        this.motivoCancelacion = motivoCancelacion;
    }
}