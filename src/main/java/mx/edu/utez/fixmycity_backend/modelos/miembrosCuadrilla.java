package mx.edu.utez.fixmycity_backend.modelos;

import jakarta.persistence.*;

@Entity
@Table(name = "miembrosCuadrilla")
public class miembrosCuadrilla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int numeroVoluntario;

    @ManyToOne
    @JoinColumn(name = "idCuadrilla", nullable = false)
    private Cuadrilla idCuadrilla;

    @ManyToOne
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario idUsuario;

    @Column(name = "tipo", nullable = false, length = 10)
    private String tipo;

    public int getNumeroVoluntario() {
        return numeroVoluntario;
    }

    public void setNumeroVoluntario(int numeroVoluntario) {
        this.numeroVoluntario = numeroVoluntario;
    }

    public Cuadrilla getIdCuadrilla() {
        return idCuadrilla;
    }

    public void setIdCuadrilla(Cuadrilla idCuadrilla) {
        this.idCuadrilla = idCuadrilla;
    }

    public Usuario getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Usuario idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
