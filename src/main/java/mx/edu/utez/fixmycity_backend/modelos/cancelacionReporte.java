package mx.edu.utez.fixmycity_backend.modelos;

import jakarta.persistence.*;

@Entity
@Table(name = "cancelacionReporte")
public class cancelacionReporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idCancelacion;

    @OneToOne
    @JoinColumn(name = "idReporte", nullable = false)
    private Reporte reporte;

    @ManyToOne
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario usuario;

    @Column(name = "motivoCancelacion", nullable = false, length = 255)
    private String motivoCancelacion;

    public int getIdCancelacion() {
        return idCancelacion;
    }

    public void setIdCancelacion(int idCancelacion) {
        this.idCancelacion = idCancelacion;
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

    public String getMotivoCancelacion() {
        return motivoCancelacion;
    }

    public void setMotivoCancelacion(String motivoCancelacion) {
        this.motivoCancelacion = motivoCancelacion;
    }
}
