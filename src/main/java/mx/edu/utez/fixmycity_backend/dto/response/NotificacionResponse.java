package mx.edu.utez.fixmycity_backend.dto.response;

import java.sql.Timestamp;

public class NotificacionResponse {

    private int idNotificacion;
    private String mensaje;
    private boolean leida;
    private Timestamp fechaEnvio;
    private int idReporte;

    public NotificacionResponse(int idNotificacion, String mensaje,
                                boolean leida, Timestamp fechaEnvio, int idReporte) {
        this.idNotificacion = idNotificacion;
        this.mensaje = mensaje;
        this.leida = leida;
        this.fechaEnvio = fechaEnvio;
        this.idReporte = idReporte;
    }

    public int getIdNotificacion() {
        return idNotificacion;
    }
    public void setIdNotificacion(int idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public String getMensaje() {
        return mensaje;
    }
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isLeida() {
        return leida;
    }
    public void setLeida(boolean leida) {
        this.leida = leida;
    }

    public Timestamp getFechaEnvio() {
        return fechaEnvio;
    }
    public void setFechaEnvio(Timestamp fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public int getIdReporte() {
        return idReporte;
    }
    public void setIdReporte(int idReporte) {
        this.idReporte = idReporte;
    }
}