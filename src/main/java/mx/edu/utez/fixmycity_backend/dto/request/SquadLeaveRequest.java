package mx.edu.utez.fixmycity_backend.dto.request;

/**
 * Cuerpo opcional para {@code POST /api/squads/leave}.
 * Si el voluntario pertenece a más de una cuadrilla activa, debe enviarse {@code idCuadrilla}.
 */
public class SquadLeaveRequest {

    private Integer idCuadrilla;

    public Integer getIdCuadrilla() {
        return idCuadrilla;
    }

    public void setIdCuadrilla(Integer idCuadrilla) {
        this.idCuadrilla = idCuadrilla;
    }
}
