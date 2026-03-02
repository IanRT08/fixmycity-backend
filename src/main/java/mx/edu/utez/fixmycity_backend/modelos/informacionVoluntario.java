package mx.edu.utez.fixmycity_backend.modelos;

import jakarta.persistence.*;

@Entity
@Table(name = "informacionVoluntario")
public class informacionVoluntario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idInfo;

    @OneToOne
    @JoinColumn(name = "idSolicitud", nullable = false)
    private solicitudVoluntario idSolicitud;

    @Column(name = "nombre", nullable = false,  length = 100)
    private String nombre;

    @Column(name = "CURP", nullable = false, length = 18)
    private String CURP;

    @Column(name = "telefono", nullable = false, length = 15)
    private String telefono;

    @Column(name = "descripcion", length = 255)
    private String descripcion;


    public int getIdInfo() {
        return idInfo;
    }

    public void setIdInfo(int idInfo) {
        this.idInfo = idInfo;
    }

    public solicitudVoluntario getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(solicitudVoluntario idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCURP() {
        return CURP;
    }

    public void setCURP(String CURP) {
        this.CURP = CURP;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
