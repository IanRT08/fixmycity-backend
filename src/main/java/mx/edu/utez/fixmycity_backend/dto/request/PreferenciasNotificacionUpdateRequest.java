package mx.edu.utez.fixmycity_backend.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/** Al menos uno de los campos debe enviarse (validado en servicio). */
public class PreferenciasNotificacionUpdateRequest {

    @Min(0)
    @Max(1)
    private Integer notifCambioEstado;

    @Min(0)
    @Max(1)
    private Integer notifNuevosZona;

    public Integer getNotifCambioEstado() {
        return notifCambioEstado;
    }

    public void setNotifCambioEstado(Integer notifCambioEstado) {
        this.notifCambioEstado = notifCambioEstado;
    }

    public Integer getNotifNuevosZona() {
        return notifNuevosZona;
    }

    public void setNotifNuevosZona(Integer notifNuevosZona) {
        this.notifNuevosZona = notifNuevosZona;
    }
}
