package mx.edu.utez.fixmycity_backend.dto.response;

/** 1 = activo, 0 = inactivo (coincide con NUMBER(1) en BD). */
public class PreferenciasNotificacionResponse {

    private int notifCambioEstado;
    private int notifNuevosZona;

    public PreferenciasNotificacionResponse() {
    }

    public PreferenciasNotificacionResponse(int notifCambioEstado, int notifNuevosZona) {
        this.notifCambioEstado = notifCambioEstado;
        this.notifNuevosZona = notifNuevosZona;
    }

    public int getNotifCambioEstado() {
        return notifCambioEstado;
    }

    public void setNotifCambioEstado(int notifCambioEstado) {
        this.notifCambioEstado = notifCambioEstado;
    }

    public int getNotifNuevosZona() {
        return notifNuevosZona;
    }

    public void setNotifNuevosZona(int notifNuevosZona) {
        this.notifNuevosZona = notifNuevosZona;
    }
}
