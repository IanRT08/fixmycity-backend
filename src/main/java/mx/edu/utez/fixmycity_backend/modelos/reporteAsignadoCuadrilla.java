package mx.edu.utez.fixmycity_backend.modelos;

import jakarta.persistence.*;

import java.util.Date;


@Entity
@Table(name = "reporteAsignadoCuadrilla")
public class reporteAsignadoCuadrilla {

    @Id
    @Column(name = "idReporte")
    private int id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "idReporte", nullable = false)
    private Reporte idReporte;

    @ManyToOne
    @JoinColumn(name = "idCuadrilla", nullable = false)
    private Cuadrilla idCuadrilla;

    @Column(name = "fechaAsignacion", nullable = false)
    private Date fechaAsignacion;

    public Reporte getIdReporte() {
        return idReporte;
    }

    public void setIdReporte(Reporte idReporte) {
        this.idReporte = idReporte;
    }

    public Cuadrilla getIdCuadrilla() {
        return idCuadrilla;
    }

    public void setIdCuadrilla(Cuadrilla idCuadrilla) {
        this.idCuadrilla = idCuadrilla;
    }

    public Date getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(Date fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }
}
