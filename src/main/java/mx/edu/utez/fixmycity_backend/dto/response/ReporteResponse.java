package mx.edu.utez.fixmycity_backend.dto.response;

import java.util.Date;
import java.util.List;

// Módulos 2.1, 2.2, 4.1, 13 - Datos de un reporte para ciudadano y administrador
public class ReporteResponse {

    private int idReporte;
    private String titulo;
    private String descripcion;
    private String estado;
    private String municipio;
    private Date fechaRegistro;
    // El nombreUsuario se muestra solo como alias por privacidad (requerimiento legal DFR)
    private String nombreUsuario;
    private List<String> fotos;
    private Boolean yaVote;

    public ReporteResponse(int idReporte, String titulo, String descripcion,
                           String estado, String municipio, Date fechaRegistro,
                           String nombreUsuario) {
        this.idReporte = idReporte;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.municipio = municipio;
        this.fechaRegistro = fechaRegistro;
        this.nombreUsuario = nombreUsuario;
    }

    public ReporteResponse(int idReporte, String titulo, String descripcion,
                           String estado, String municipio, Date fechaRegistro,
                           String nombreUsuario, List<String> fotos) {
        this(idReporte, titulo, descripcion, estado, municipio, fechaRegistro, nombreUsuario);
        this.fotos = fotos;
    }

    public int getIdReporte() {
        return idReporte;
    }
    public void setIdReporte(int idReporte) {
        this.idReporte = idReporte;
    }

    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
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

    public String getMunicipio() {
        return municipio;
    }
    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }
    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public List<String> getFotos() {
        return fotos;
    }
    public void setFotos(List<String> fotos) {
        this.fotos = fotos;
    }

    public Boolean getYaVote() {
        return yaVote;
    }
    public void setYaVote(Boolean yaVote) {
        this.yaVote = yaVote;
    }
}