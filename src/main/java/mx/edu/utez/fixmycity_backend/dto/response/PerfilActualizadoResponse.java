package mx.edu.utez.fixmycity_backend.dto.response;

/**
 * Respuesta de PUT /api/users/me. Si cambió nombre de usuario o municipio, viene un nuevo JWT.
 */
public class PerfilActualizadoResponse {

    private PerfilResponse perfil;
    /** Nuevo token si cambió nombreUsuario o municipio; si es null, el token anterior sigue válido. */
    private String token;

    public PerfilActualizadoResponse() {
    }

    public PerfilActualizadoResponse(PerfilResponse perfil, String token) {
        this.perfil = perfil;
        this.token = token;
    }

    public PerfilResponse getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilResponse perfil) {
        this.perfil = perfil;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
