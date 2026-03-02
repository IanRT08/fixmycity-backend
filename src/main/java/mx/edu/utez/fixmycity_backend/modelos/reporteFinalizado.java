package mx.edu.utez.fixmycity_backend.modelos;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "reporteFinalizado")
public class reporteFinalizado {

    @Id
    @OneToOne
    @JoinColumn(name = "idReporte", nullable = false)
    private Reporte idReporte;

    @Column(name = "fotoEvidencia", nullable = false)
    private String fotoEvidencia;

    @Column(name = "comentarios", nullable = false, length = 255)
    private String comentarios;

    @ManyToOne
    @JoinColumn(name = "idCuadrilla")
    private Cuadrilla idCuadrillaEncargada;

    @Column(name = "fechaFinalizacion", nullable = false)
    private Date fechaFinalizacion;

    public Reporte getIdReporte() {
        return idReporte;
    }

    public void setIdReporte(Reporte idReporte) {
        this.idReporte = idReporte;
    }

    public String getFotoEvidencia() {
        return fotoEvidencia;
    }

    public void setFotoEvidencia(String fotoEvidencia) {
        this.fotoEvidencia = fotoEvidencia;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public Cuadrilla getIdCuadrillaEncargada() {
        return idCuadrillaEncargada;
    }

    public void setIdCuadrillaEncargada(Cuadrilla idCuadrillaEncargada) {
        this.idCuadrillaEncargada = idCuadrillaEncargada;
    }

    public Date getFechaFinalizacion() {
        return fechaFinalizacion;
    }

    public void setFechaFinalizacion(Date fechaFinalizacion) {
        this.fechaFinalizacion = fechaFinalizacion;
    }
}
