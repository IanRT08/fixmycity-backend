package mx.edu.utez.fixmycity_backend.modelos;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table
public class Notificaciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idNotificacion;

    @ManyToOne
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario idUsuario;

    @ManyToOne
    @JoinColumn(name = "idReporte", nullable = false)
    private Reporte idReporte;

    @Column(name = "mensaje", nullable = false, length = 255)
    private String mensaje;

    @Column(name = "leida", nullable = false, length = 1)
    private int leida;

    @Column(name = "fechaEnvio", nullable = false)
    private Timestamp fechaEnvio;

    public int getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(int idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public Usuario getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Usuario idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Reporte getIdReporte() {
        return idReporte;
    }

    public void setIdReporte(Reporte idReporte) {
        this.idReporte = idReporte;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public int getLeida() {
        return leida;
    }

    public void setLeida(int leida) {
        this.leida = leida;
    }

    public Timestamp getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(Timestamp fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }
}
