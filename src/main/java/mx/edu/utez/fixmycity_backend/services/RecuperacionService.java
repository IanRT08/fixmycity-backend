package mx.edu.utez.fixmycity_backend.services;

import mx.edu.utez.fixmycity_backend.dto.request.ForgotPasswordRequest;
import mx.edu.utez.fixmycity_backend.dto.request.ResetPasswordRequest;
import mx.edu.utez.fixmycity_backend.dto.request.VerificarTokenRequest;
import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.modelos.TokenRecuperacion;
import mx.edu.utez.fixmycity_backend.modelos.Usuario;
import mx.edu.utez.fixmycity_backend.repositories.TokenRecuperacionRepository;
import mx.edu.utez.fixmycity_backend.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;

@Service
public class RecuperacionService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenRecuperacionRepository tokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${spring.mail.username}")
    private String mailFrom;

    @Transactional
    public ApiResponse solicitarRecuperacion(ForgotPasswordRequest request) throws Exception {
        Optional<Object> idOpt = usuarioRepository.buscarIdPorCorreo(request.getCorreo());
        if (idOpt.isEmpty()) {
            return new ApiResponse(false, "No existe una cuenta con ese correo");
        }

        int idUsuario = ((Number) idOpt.get()).intValue();
        Usuario usuario = usuarioRepository.findById(idUsuario).get();

        tokenRepository.deleteByUsuario(idUsuario);

        String token = String.format("%06d", new Random().nextInt(999999));
        Timestamp expiracion = Timestamp.from(Instant.now().plus(6 * 60 + 15, ChronoUnit.MINUTES));

        TokenRecuperacion tokenRecuperacion = new TokenRecuperacion();
        tokenRecuperacion.setUsuario(usuario);
        tokenRecuperacion.setToken(token);
        tokenRecuperacion.setFechaExpiracion(expiracion);
        tokenRecuperacion.setUsado(false);
        tokenRepository.save(tokenRecuperacion);

        ClassPathResource templateResource = new ClassPathResource("recovery-email-template.html");
        String html = StreamUtils.copyToString(templateResource.getInputStream(), StandardCharsets.UTF_8);
        html = html.replace("{{nombreUsuario}}", usuario.getNombreUsuario());
        html = html.replace("{{token}}", token);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setFrom(mailFrom);
        helper.setTo(request.getCorreo());
        helper.setSubject("FixMyCity - Recuperación de contraseña");
        helper.setText(html, true);
        helper.addInline("logoFMC", new ClassPathResource("LogoFMC.png"));
        helper.addInline("mailCat", new ClassPathResource("MailCat.png"));
        mailSender.send(mimeMessage);

        return new ApiResponse(true, "Se envió un código de recuperación a tu correo");
    }

    public ApiResponse verificarToken(VerificarTokenRequest request) {
        Optional<Object> tokenOpt = tokenRepository.findValidToken(
                request.getCorreo(), request.getToken());

        if (tokenOpt.isEmpty()) {
            return new ApiResponse(false, "El código es inválido o ha expirado");
        }

        return new ApiResponse(true, "Código válido");
    }

    @Transactional
    public ApiResponse resetearContrasenia(ResetPasswordRequest request) {
        if (!request.getNuevaContrasenia().equals(request.getConfirmarContrasenia())) {
            return new ApiResponse(false, "Las contraseñas no coinciden");
        }

        Optional<Object> tokenOpt = tokenRepository.findValidToken(
                request.getCorreo(), request.getToken());

        if (tokenOpt.isEmpty()) {
            return new ApiResponse(false, "El código es inválido o ha expirado");
        }

        int idToken = ((Number) tokenOpt.get()).intValue();
        TokenRecuperacion tokenRecuperacion = tokenRepository.findById(idToken).get();
        Usuario usuario = tokenRecuperacion.getUsuario();
        usuario.setContrasenia(passwordEncoder.encode(request.getNuevaContrasenia()));
        usuarioRepository.save(usuario);

        tokenRepository.marcarComoUsado(idToken);

        return new ApiResponse(true, "Contraseña actualizada correctamente");
    }
}
