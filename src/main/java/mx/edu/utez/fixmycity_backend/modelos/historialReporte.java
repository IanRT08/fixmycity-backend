package mx.edu.utez.fixmycity_backend.modelos;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "historialReporte")
public class historialReporte {

    @Id
    @Column(name = "idCambioEstado", nullable = false)
    private int idCambioEstado;

    @ManyToOne
    @JoinColumn(name = "idReporte", nullable = false)
    private Reporte idReporte;

    @ManyToOne
    @JoinColumn(name = "idUsuario")
    private Usuario idResponsable;

    @Column(name = "fechaCambio", nullable = false)
    private Timestamp fechaCambio;

    @Column(name = "estadoAnterior", nullable = false, length = 15)
    private String estadoAnterior;

    @Column(name = "estadoNuevo", nullable = false, length = 15)
    private String estadoNuevo;

    public String getEstadoNuevo() {
        return estadoNuevo;
    }

    public void setEstadoNuevo(String estadoNuevo) {
        this.estadoNuevo = estadoNuevo;
    }

    public String getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(String estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    public Timestamp getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(Timestamp fechaCambio) {
        this.fechaCambio = fechaCambio;
    }

    public Usuario getIdResponsable() {
        return idResponsable;
    }

    public void setIdResponsable(Usuario idResponsable) {
        this.idResponsable = idResponsable;
    }

    public Reporte getIdReporte() {
        return idReporte;
    }

    public void setIdReporte(Reporte idReporte) {
        this.idReporte = idReporte;
    }

    public int getIdCambioEstado() {
        return idCambioEstado;
    }

    public void setIdCambioEstado(int idCambioEstado) {
        this.idCambioEstado = idCambioEstado;
    }
}
