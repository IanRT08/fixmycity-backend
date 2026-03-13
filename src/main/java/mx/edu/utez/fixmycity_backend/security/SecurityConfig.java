package mx.edu.utez.fixmycity_backend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${cors.allowed.origins:http://localhost:3000,http://localhost:4200}")
    private String allowedOrigins;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService,
                         JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // BCrypt es el algoritmo de encriptación para contraseñas
    // Lo usan AuthService y UsuarioService con @Autowired PasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        //Endpoints públicos
                        //Módulo 1.1 - Registro de ciudadano
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                        //Módulo 1.2 - Login
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        //Módulo 13 - Feed público (no requiere login)
                        .requestMatchers(HttpMethod.GET, "/api/feed/**").permitAll()
                        //Módulo 6.1 - Listar municipios activos
                        .requestMatchers(HttpMethod.GET, "/api/zones/active").permitAll()

                        //Solo administrador
                        //Módulo 1.4 - Gestión de usuarios
                        .requestMatchers("/api/admin/users/**").hasRole("ADMIN")
                        //Módulos 4.1, 4.4 - Panel y gestión de reportes
                        .requestMatchers("/api/admin/reports/**").hasRole("ADMIN")
                        //Módulo 3.1, 3.2 - Gestión y asignación de cuadrillas
                        .requestMatchers("/api/admin/squads/**").hasRole("ADMIN")
                        //Módulo 9 - Dashboard
                        .requestMatchers("/api/admin/dashboard/**").hasRole("ADMIN")
                        //Módulo 6.1 - Habilitar/deshabilitar municipios
                        .requestMatchers("/api/zones/**").hasRole("ADMIN")

                        //Ciudadano y voluntario
                        //Módulo 2.1, 2.2, 2.3, 2.4 - Gestión de reportes propios
                        .requestMatchers("/api/reports/**").hasAnyRole("CIUDADANO", "VOLUNTARIO", "ADMIN")
                        // Módulo 1.3 - Solicitud de voluntario
                        .requestMatchers("/api/users/**").hasAnyRole("CIUDADANO", "VOLUNTARIO", "ADMIN")
                        // Módulo 5.1, 5.2 - Notificaciones
                        .requestMatchers("/api/notifications/**").hasAnyRole("CIUDADANO", "VOLUNTARIO", "ADMIN")

                        //Solo voluntario
                        //Módulo 3.3, 3.4, 3.6 - Votación y seguimiento de atención
                        .requestMatchers("/api/squads/**").hasAnyRole("VOLUNTARIO", "ADMIN")
                        //Cualquier otra ruta requiere autenticación
                        .anyRequest().authenticated()
                )

                .authenticationProvider(authenticationProvider())

                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}