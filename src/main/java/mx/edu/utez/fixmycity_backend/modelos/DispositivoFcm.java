package mx.edu.utez.fixmycity_backend.modelos;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "dispositivo_fcm")
public class DispositivoFcm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DISPOSITIVO")
    private Long idDispositivo;

    @Column(name = "idUsuario", nullable = false)
    private int idUsuario;

    @Column(name = "TOKEN_FCM", nullable = false, length = 4000)
    private String tokenFcm;

    @Column(name = "PLATAFORMA", length = 20)
    private String plataforma;

    @Column(name = "CREADO_EN", insertable = false, updatable = false)
    private Timestamp creadoEn;

    public Long getIdDispositivo() {
        return idDispositivo;
    }

    public void setIdDispositivo(Long idDispositivo) {
        this.idDispositivo = idDispositivo;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTokenFcm() {
        return tokenFcm;
    }

    public void setTokenFcm(String tokenFcm) {
        this.tokenFcm = tokenFcm;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;
    }

    public Timestamp getCreadoEn() {
        return creadoEn;
    }
}
