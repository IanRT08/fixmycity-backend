package mx.edu.utez.fixmycity_backend.dto.response;

/**
 * Miembro de cuadrilla para GET /api/squads/me (app voluntario/líder).
 */
public class SquadMiembroResponse {

    private int idUsuario;
    private String nombreUsuario;
    /** Valores típicos: {@code lider}, {@code voluntario} (columna miembrosCuadrilla.tipo). */
    private String rolEnCuadrilla;

    public SquadMiembroResponse() {
    }

    public SquadMiembroResponse(int idUsuario, String nombreUsuario, String rolEnCuadrilla) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.rolEnCuadrilla = rolEnCuadrilla;
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

    public String getRolEnCuadrilla() {
        return rolEnCuadrilla;
    }

    public void setRolEnCuadrilla(String rolEnCuadrilla) {
        this.rolEnCuadrilla = rolEnCuadrilla;
    }
}
