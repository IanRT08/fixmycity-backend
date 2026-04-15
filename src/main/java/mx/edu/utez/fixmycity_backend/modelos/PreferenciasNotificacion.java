package mx.edu.utez.fixmycity_backend.modelos;

import jakarta.persistence.*;

@Entity
@Table(name = "preferencias_notificacion")
public class PreferenciasNotificacion {

    @Id
    @Column(name = "idUsuario", nullable = false)
    private int idUsuario;

    @Column(name = "NOTIF_CAMBIO_ESTADO", nullable = false)
    private int notifCambioEstado = 1;

    @Column(name = "NOTIF_NUEVOS_ZONA", nullable = false)
    private int notifNuevosZona = 1;

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
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
