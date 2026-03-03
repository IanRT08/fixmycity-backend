package mx.edu.utez.fixmycity_backend.modelos;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "historialReporte")
public class historialReporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idCambioEstado;

    @ManyToOne
    @JoinColumn(name = "idReporte", nullable = false)
    private Reporte reporte;

    @ManyToOne
    @JoinColumn(name = "idResponsable")
    private Usuario usuario;

    @Column(name = "fechaCambio", nullable = false)
    private Timestamp fechaCambio;

    @Column(name = "estadoAnterior", nullable = false, length = 15)
    private String estadoAnterior;

    @Column(name = "estadoNuevo", nullable = false, length = 15)
    private String estadoNuevo;

    public int getIdCambioEstado() {
        return idCambioEstado;
    }

    public void setIdCambioEstado(int idCambioEstado) {
        this.idCambioEstado = idCambioEstado;
    }

    public Reporte getReporte() {
        return reporte;
    }

    public void setReporte(Reporte reporte) {
        this.reporte = reporte;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Timestamp getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(Timestamp fechaCambio) {
        this.fechaCambio = fechaCambio;
    }

    public String getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(String estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    public String getEstadoNuevo() {
        return estadoNuevo;
    }

    public void setEstadoNuevo(String estadoNuevo) {
        this.estadoNuevo = estadoNuevo;
    }
}
