package mx.edu.utez.fixmycity_backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RestController
@RequestMapping("/test")
public class TestConnectionController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/db")
    public String testConnection() {
        try (Connection conn = dataSource.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                return "✅ Conexión exitosa a Oracle Cloud";
            }
        } catch (SQLException e) {
            return "❌ Error: " + e.getMessage();
        }
        return "❌ Conexión nula o cerrada";
    }

}
