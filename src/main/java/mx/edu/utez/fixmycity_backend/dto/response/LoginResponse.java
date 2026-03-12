package mx.edu.utez.fixmycity_backend.dto.response;

public class LoginResponse {

    private String token;
    private String nombreUsuario;
    private String tipo;
    private String municipio;

    public LoginResponse(String token, String nombreUsuario, String tipo, String municipio) {
        this.token = token;
        this.nombreUsuario = nombreUsuario;
        this.tipo = tipo;
        this.municipio = municipio;
    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMunicipio() {
        return municipio;
    }
    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }
}