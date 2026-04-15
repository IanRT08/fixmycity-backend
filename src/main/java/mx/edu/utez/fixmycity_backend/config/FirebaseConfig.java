package mx.edu.utez.fixmycity_backend.config;

import com.google.auth.oauth2
        .GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.FileInputStream;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.credentials.path:}")
    private String credentialsPath;

    @PostConstruct
    public void init() {
        if (!StringUtils.hasText(credentialsPath)) {
            log.warn("FIREBASE_CREDENTIALS_PATH / firebase.credentials.path vacío: push FCM deshabilitado");
            return;
        }
        try (InputStream in = new FileInputStream(credentialsPath.trim())) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(in))
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase Admin inicializado");
            }
        } catch (Exception e) {
            log.error("No se pudo inicializar Firebase Admin (push deshabilitado): {}", e.getMessage());
        }
    }
}
