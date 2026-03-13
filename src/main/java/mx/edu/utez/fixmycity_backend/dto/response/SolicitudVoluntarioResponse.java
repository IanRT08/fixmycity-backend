package mx.edu.utez.fixmycity_backend.dto.response;

public class SolicitudVoluntarioResponse {

    private int idSolicitud;
    private String nombreUsuario;
    private String nombre;
    private String curp;
    private String telefono;
    private String descripcion;
    private String estado;

    public SolicitudVoluntarioResponse(int idSolicitud, String nombreUsuario, String nombre,
                                       String curp, String telefono,
                                       String descripcion, String estado) {
        this.idSolicitud = idSolicitud;
        this.nombreUsuario = nombreUsuario;
        this.nombre = nombre;
        this.curp = curp;
        this.telefono = telefono;
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