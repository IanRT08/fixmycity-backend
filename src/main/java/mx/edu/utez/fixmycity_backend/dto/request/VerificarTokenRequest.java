package mx.edu.utez.fixmycity_backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class VerificarTokenRequest {

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    private String correo;

    @NotBlank(message = "El token es obligatorio")
    @Size(min = 6, max = 6, message = "El token debe tener 6 dígitos")
    private String token;

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
