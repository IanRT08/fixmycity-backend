package mx.edu.utez.fixmycity_backend.dto.response;

import java.util.List;

/**
 * Contexto de la cuadrilla del usuario autenticado (voluntario) para pantalla "Mi equipo".
 */
public class SquadMiCuadrillaResponse {

    private int idCuadrilla;
    private String nombreCuadrilla;
    private String estadoCuadrilla;
    private List<SquadMiembroResponse> miembros;

    public SquadMiCuadrillaResponse() {
    }

    public SquadMiCuadrillaResponse(int idCuadrilla, String nombreCuadrilla, String estadoCuadrilla,
                                    List<SquadMiembroResponse> miembros) {
        this.idCuadrilla = idCuadrilla;
        this.nombreCuadrilla = nombreCuadrilla;
        this.estadoCuadrilla = estadoCuadrilla;
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

    public String getEstadoCuadrilla() {
        return estadoCuadrilla;
    }

    public void setEstadoCuadrilla(String estadoCuadrilla) {
        this.estadoCuadrilla = estadoCuadrilla;
    }

    public List<SquadMiembroResponse> getMiembros() {
        return miembros;
    }

    public void setMiembros(List<SquadMiembroResponse> miembros) {
        this.miembros = miembros;
    }
}
