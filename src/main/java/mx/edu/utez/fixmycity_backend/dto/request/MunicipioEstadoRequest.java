package mx.edu.utez.fixmycity_backend.dto.request;

import jakarta.validation.constraints.*;

public class MunicipioEstadoRequest {

    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "^(Activo|Inactivo)$", message = "El estado debe ser 'Activo' o 'Inactivo'")
    private String estado;

    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
}