package mx.edu.utez.fixmycity_backend.modelos;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "detallesReporte")
public class detallesReporte {

    @Id
    @Column(name = "idReporte")
    private int idReporte;

    @OneToOne
    @MapsId
    @JoinColumn(name = "idReporte", nullable = false)
    private Reporte reporte;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "municipios", nullable = false)
    private Municipios municipios;

    @Column(name = "estado", nullable = false, length = 15)
    private String estado;

    @Column(name = "fechaRegistro", nullable = false)
    private Date fechaRegistro;

    public Reporte getReporte() {
        return reporte;
    }

    public void setReporte(Reporte reporte) {
        this.reporte = reporte;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Municipios getMunicipios() {
        return municipios;
    }

    public void setMunicipios(Municipios municipios) {
        this.municipios = municipios;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
