package mx.edu.utez.fixmycity_backend.dto.response;

import java.util.List;

public class CuadrillaResponse {

    private int idCuadrilla;
    private String nombreCuadrilla;
    private String municipio;
    private String lider;
    private String estado;
    private List<String> miembros;

    public CuadrillaResponse(int idCuadrilla, String nombreCuadrilla, String municipio,
                             String lider, String estado, List<String> miembros) {
        this.idCuadrilla = idCuadrilla;
        this.nombreCuadrilla = nombreCuadrilla;
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
}