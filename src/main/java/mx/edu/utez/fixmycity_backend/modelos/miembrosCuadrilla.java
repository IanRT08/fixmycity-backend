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
    private Cuadrilla cuadrilla;

    @ManyToOne
    @JoinColumn(name = "idVoluntario", nullable = false)
    private Usuario usuario;

    @Column(name = "tipo", nullable = false, length = 10)
    private String tipo;

    public int getNumeroVoluntario() {
        return numeroVoluntario;
    }

    public void setNumeroVoluntario(int numeroVoluntario) {
        this.numeroVoluntario = numeroVoluntario;
    }

    public Cuadrilla getCuadrilla() {
        return cuadrilla;
    }

    public void setCuadrilla(Cuadrilla cuadrilla) {
        this.cuadrilla = cuadrilla;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
