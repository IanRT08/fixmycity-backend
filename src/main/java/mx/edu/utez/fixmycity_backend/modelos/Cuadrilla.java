package mx.edu.utez.fixmycity_backend.modelos;

import jakarta.persistence.*;

@Entity
@Table(name = "cuadrilla")
public class Cuadrilla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idCuadrilla;

    @Column(name = "nombreCuadrilla", nullable = false, length = 50)
    private String nombreCuadrilla;

    @ManyToOne
    @JoinColumn(name = "idMunicipio", nullable = false)
    private Municipios idMunicipio;

    @OneToOne
    @JoinColumn(name = "idVoluntario", nullable = false)
    private Voluntario idLider;

    @Column(name = "estado", nullable = false, length = 10)
    private String estado;

    public int getIdCuadrilla() {
        return idCuadrilla;
    }

    public void setIdCuadrilla(int idCuadrilla) {
        this.idCuadrilla = idCuadrilla;
    }

    public String getNombreCuadrilla() {
        return nombreCuadrilla;
    }

    public void setNombreCuadrilla(String nombreCuadrilla) {
        this.nombreCuadrilla = nombreCuadrilla;
    }

    public Municipios getIdMunicipio() {
        return idMunicipio;
    }

    public void setIdMunicipio(Municipios idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    public Voluntario getIdLider() {
        return idLider;
    }

    public void setIdLider(Voluntario idLider) {
        this.idLider = idLider;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
