package mx.edu.utez.fixmycity_backend.modelos;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "tokenRecuperacion")
public class TokenRecuperacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idToken;

    @ManyToOne
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario usuario;

    @Column(name = "token", nullable = false, length = 6)
    private String token;

    @Column(name = "fechaExpiracion", nullable = false)
    private Timestamp fechaExpiracion;

    @Column(name = "usado", nullable = false)
    private boolean usado;

    public int getIdToken() { return idToken; }
    public void setIdToken(int idToken) { this.idToken = idToken; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Timestamp getFechaExpiracion() { return fechaExpiracion; }
    public void setFechaExpiracion(Timestamp fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }

    public boolean isUsado() { return usado; }
    public void setUsado(boolean usado) { this.usado = usado; }
}
