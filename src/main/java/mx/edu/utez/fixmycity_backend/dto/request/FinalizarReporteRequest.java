package mx.edu.utez.fixmycity_backend.dto.request;

import jakarta.validation.constraints.*;

public class FinalizarReporteRequest {

    @NotBlank(message = "Los comentarios finales son obligatorios")
    @Size(max = 255, message = "Los comentarios no pueden exceder 255 caracteres")
    private String comentarios;

    public String getComentarios() {
        return comentarios;
    }
    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }
}