package mx.edu.utez.fixmycity_backend.dto.response;

import java.util.List;

public class CuadrillaResponse {

    private int idCuadrilla;
    private String nombreCuadrilla;
    private int idMunicipio;
    private String municipio;
    private String lider;
    private String estado;
    private List<String> miembros;
    private Integer reporteActualId;
    private String reporteActualTitulo;
    private String reporteActualEstado;

    public CuadrillaResponse(int idCuadrilla, String nombreCuadrilla, int idMunicipio, String municipio,
                             String lider, String estado, List<String> miembros) {
        this.idCuadrilla = idCuadrilla;
        this.nombreCuadrilla = nombreCuadrilla;
        this.idMunicipio = idMunicipio;
        this.municipio = municipio;
        this.lider = lider;
        this.estado = estado;
        this.miembros = miembros;
    }

    public int getIdCuadrilla() {
        return idCuadrilla;
    }
    public void setIdCuadrilla(int idCuadrilla) {
        this.idCuadrilla = idCuadrilla;
    }

    public String getNombreCuadrilla() {
        return nombreCuadrilla;
    }
    public void setNombreCuadrilla(String nombreCuadrilla) {
        this.nombreCuadrilla = nombreCuadrilla;
    }

    public int getIdMunicipio() {
        return idMunicipio;
    }
    public void setIdMunicipio(int idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    public String getMunicipio() {
        return municipio;
    }
    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getLider() {
        return lider;
    }
    public void setLider(String lider) {
        this.lider = lider;
    }

    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<String> getMiembros() {
        return miembros;
    }
    public void setMiembros(List<String> miembros) {
        this.miembros = miembros;
    }

    public Integer getReporteActualId() {
        return reporteActualId;
    }
    public void setReporteActualId(Integer reporteActualId) {
        this.reporteActualId = reporteActualId;
    }

    public String getReporteActualTitulo() {
        return reporteActualTitulo;
    }
    public void setReporteActualTitulo(String reporteActualTitulo) {
        this.reporteActualTitulo = reporteActualTitulo;
    }

    public String getReporteActualEstado() {
        return reporteActualEstado;
    }
    public void setReporteActualEstado(String reporteActualEstado) {
        this.reporteActualEstado = reporteActualEstado;
    }
}