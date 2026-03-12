package mx.edu.utez.fixmycity_backend.dto.response;

// Módulo 1.3 - Datos de una solicitud de voluntario que el admin revisa
public class SolicitudVoluntarioResponse {

    private int idSolicitud;
    private String nombreUsuario;
    private String nombre;
    private String curp;
    private String telefono;
    private String municipio;
    private String descripcion;
    private String estado;

    public SolicitudVoluntarioResponse(int idSolicitud, String nombreUsuario, String nombre,
                                       String curp, String telefono, String municipio,
                                       String descripcion, String estado) {
        this.idSolicitud = idSolicitud;
        this.nombreUsuario = nombreUsuario;
        this.nombre = nombre;
        this.curp = curp;
        this.telefono = telefono;
        this.municipio = municipio;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public int getIdSolicitud() {
        return idSolicitud;
    }
    public void setIdSolicitud(int idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCurp() {
        return curp;
    }
    public void setCurp(String curp) {
        this.curp = curp;
    }

    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getMunicipio() {
        return municipio;
    }
    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
}