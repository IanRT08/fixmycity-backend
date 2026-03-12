package mx.edu.utez.fixmycity_backend.dto.response;

// Módulos 1.1, 1.4 - Datos del usuario que se devuelven al cliente
// Nunca incluir la contraseña en un response
public class UsuarioResponse {

    private int idUsuario;
    private String nombreUsuario;
    private String correo;
    private String tipo;
    private String estado;
    private String municipio;

    public UsuarioResponse(int idUsuario, String nombreUsuario, String correo,
                           String tipo, String estado, String municipio) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
        this.tipo = tipo;
        this.estado = estado;
        this.municipio = municipio;
    }

    public int getIdUsuario() {
        return idUsuario;
    }
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getCorreo() {
        return correo;
    }
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMunicipio() {
        return municipio;
    }
    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }
}