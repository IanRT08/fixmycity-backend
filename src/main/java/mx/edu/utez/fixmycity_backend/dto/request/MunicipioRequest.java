package mx.edu.utez.fixmycity_backend.dto.request;

import jakarta.validation.constraints.*;

public class MunicipioRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede superar 50 caracteres")
    private String nombre;

    @Pattern(regexp = "^(Activo|Inactivo)$", message = "El estado debe ser 'Activo' o 'Inactivo'")
    private String estado;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
