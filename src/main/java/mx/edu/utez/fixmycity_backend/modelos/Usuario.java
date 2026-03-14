package mx.edu.utez.fixmycity_backend.modelos;

import jakarta.persistence.*;

import java.util.Date;

@NamedNativeQueries({
    @NamedNativeQuery(
        name = "Usuario.findByNombreUsuarioAndEstado",
        query = "SELECT u.idUsuario, u.nombreUsuario, u.correo, u.contrasenia, " +
                "u.fechaNacimiento, u.idMunicipio, u.tipo, u.estado " +
                "FROM usuario u " +
                "INNER JOIN municipios m ON u.idMunicipio = m.idMunicipio " +
                "WHERE u.nombreUsuario = :nombreUsuario AND u.estado = :estado",
        resultSetMapping = "UsuarioMapping"
    ),
    @NamedNativeQuery(
        name = "Usuario.findByNombreUsuario",
        query = "SELECT u.idUsuario, u.nombreUsuario, u.correo, u.contrasenia, " +
                "u.fechaNacimiento, u.idMunicipio, u.tipo, u.estado " +
                "FROM usuario u " +
                "WHERE u.nombreUsuario = :nombreUsuario",
        resultSetMapping = "UsuarioMapping"
    ),
    @NamedNativeQuery(
        name = "Usuario.findByCorreo",
        query = "SELECT u.idUsuario, u.nombreUsuario, u.correo, u.contrasenia, " +
                "u.fechaNacimiento, u.idMunicipio, u.tipo, u.estado " +
                "FROM usuario u " +
                "WHERE u.correo = :correo",
        resultSetMapping = "UsuarioMapping"
    ),
    @NamedNativeQuery(
        name = "Usuario.findByTipo",
        query = "SELECT u.idUsuario, u.nombreUsuario, u.correo, u.contrasenia, " +
                "u.fechaNacimiento, u.idMunicipio, u.tipo, u.estado " +
                "FROM usuario u " +
                "INNER JOIN municipios m ON u.idMunicipio = m.idMunicipio " +
                "WHERE u.tipo = :tipo",
        resultSetMapping = "UsuarioMapping"
    ),
    @NamedNativeQuery(
        name = "Usuario.findByEstado",
        query = "SELECT u.idUsuario, u.nombreUsuario, u.correo, u.contrasenia, " +
                "u.fechaNacimiento, u.idMunicipio, u.tipo, u.estado " +
                "FROM usuario u " +
                "INNER JOIN municipios m ON u.idMunicipio = m.idMunicipio " +
                "WHERE u.estado = :estado",
        resultSetMapping = "UsuarioMapping"
    )
})
@SqlResultSetMapping(
    name = "UsuarioMapping",
    entities = @EntityResult(
        entityClass = Usuario.class,
        fields = {
            @FieldResult(name = "idUsuario", column = "idUsuario"),
            @FieldResult(name = "nombreUsuario", column = "nombreUsuario"),
            @FieldResult(name = "correo", column = "correo"),
            @FieldResult(name = "contrasenia", column = "contrasenia"),
            @FieldResult(name = "fechaNacimiento", column = "fechaNacimiento"),
            @FieldResult(name = "municipio", column = "idMunicipio"),
            @FieldResult(name = "tipo", column = "tipo"),
            @FieldResult(name = "estado", column = "estado")
        }
    )
)
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idUsuario;

    @Column(name = "nombreUsuario", nullable = false, length = 50)
    private String nombreUsuario;

    @Column(name = "correo", nullable = false, length = 100)
    private String correo;

    @Column(name = "contrasenia", nullable = false, length = 255)
    private String contrasenia;

    @Column(name = "fechaNacimiento", nullable = false)
    private Date fechaNacimiento;

    @ManyToOne
    @JoinColumn(name = "idMunicipio", nullable = false)
    private Municipios municipio;

    @Column(name = "tipo", nullable = false, length = 15)
    private String tipo;

    @Column(name = "estado", nullable = false, length = 10)
    private String estado;

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Municipios getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Municipios municipio) {
        this.municipio = municipio;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
