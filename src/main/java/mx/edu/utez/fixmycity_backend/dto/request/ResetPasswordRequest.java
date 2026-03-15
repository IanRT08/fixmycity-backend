package mx.edu.utez.fixmycity_backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetPasswordRequest {

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    private String correo;

    @NotBlank(message = "El token es obligatorio")
    @Size(min = 6, max = 6, message = "El token debe tener 6 dígitos")
    private String token;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener mínimo 8 caracteres")
    private String nuevaContrasenia;

    @NotBlank(message = "La confirmación de contraseña es obligatoria")
    private String confirmarContrasenia;

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getNuevaContrasenia() { return nuevaContrasenia; }
    public void setNuevaContrasenia(String nuevaContrasenia) { this.nuevaContrasenia = nuevaContrasenia; }

    public String getConfirmarContrasenia() { return confirmarContrasenia; }
    public void setConfirmarContrasenia(String confirmarContrasenia) { this.confirmarContrasenia = confirmarContrasenia; }
}
