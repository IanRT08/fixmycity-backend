package mx.edu.utez.fixmycity_backend.modelos;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "detallesReporte")
public class detallesReporte {

    @Id
    @OneToOne
    @JoinColumn(name = "idReporte", nullable = false)
    private Reporte idReporte;

    @Column(name = "descripcion", nullable = false, length = 255)
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "idMunicipio", nullable = false)
    private Municipios idMunicipio;

    @Column(name = "estado", nullable = false, length = 15)
    private String estado;

    @Column(name = "fechaRegistro", nullable = false)
    private Date fechaRegistro;

    public Reporte getIdReporte() {
        return idReporte;
    }

    public void setIdReporte(Reporte idReporte) {
        this.idReporte = idReporte;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Municipios getIdMunicipio() {
        return idMunicipio;
    }

    public void setIdMunicipio(Municipios idMunicipio) {
        this.idMunicipio = idMunicipio;
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
