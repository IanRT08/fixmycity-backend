package mx.edu.utez.fixmycity_backend.dto.response;

import java.util.Date;

public class PerfilResponse {

    private int idUsuario;
    private String nombreUsuario;
    private String correo;
    private Date fechaNacimiento;
    private String municipio;
    private int idMunicipio;
    private String tipo;
    private String estado;
    /** Base64 de la imagen, o null si no hay foto (admins u opcional). */
    private String fotoPerfilBase64;

    /** True si el usuario es el {@code Voluntario} líder de alguna cuadrilla (tabla cuadrilla.idLider). */
    private boolean esLiderDeCuadrilla;

    /** Identificador de la cuadrilla donde es líder; null si no aplica o no se encontró fila. */
    private Integer idCuadrilla;

    public PerfilResponse() {
    }

    public PerfilResponse(int idUsuario, String nombreUsuario, String correo, Date fechaNacimiento,
                          String municipio, int idMunicipio, String tipo, String estado,
                          String fotoPerfilBase64) {
        this(idUsuario, nombreUsuario, correo, fechaNacimiento, municipio, idMunicipio, tipo, estado,
                fotoPerfilBase64, false, null);
    }

    public PerfilResponse(int idUsuario, String nombreUsuario, String correo, Date fechaNacimiento,
                          String municipio, int idMunicipio, String tipo, String estado,
                          String fotoPerfilBase64, boolean esLiderDeCuadrilla, Integer idCuadrilla) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
        this.fechaNacimiento = fechaNacimiento;
        this.municipio = municipio;
        this.idMunicipio = idMunicipio;
        this.tipo = tipo;
        this.estado = estado;
        this.fotoPerfilBase64 = fotoPerfilBase64;
        this.esLiderDeCuadrilla = esLiderDeCuadrilla;
        this.idCuadrilla = idCuadrilla;
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

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public int getIdMunicipio() {
        return idMunicipio;
    }

    public void setIdMunicipio(int idMunicipio) {
        this.idMunicipio = idMunicipio;
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

    public String getFotoPerfilBase64() {
        return fotoPerfilBase64;
    }

    public void setFotoPerfilBase64(String fotoPerfilBase64) {
        this.fotoPerfilBase64 = fotoPerfilBase64;
    }

    public boolean getEsLiderDeCuadrilla() {
        return esLiderDeCuadrilla;
    }

    public void setEsLiderDeCuadrilla(boolean esLiderDeCuadrilla) {
        this.esLiderDeCuadrilla = esLiderDeCuadrilla;
    }

    public Integer getIdCuadrilla() {
        return idCuadrilla;
    }

    public void setIdCuadrilla(Integer idCuadrilla) {
        this.idCuadrilla = idCuadrilla;
    }
}
