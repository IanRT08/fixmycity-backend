package mx.edu.utez.fixmycity_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class FcmTokenRequest {

    @NotBlank(message = "El token FCM es obligatorio")
    @Size(max = 4000)
    private String token;

    /** Ej.: ANDROID, IOS */
    @Size(max = 20)
    private String plataforma;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;
    }
}
