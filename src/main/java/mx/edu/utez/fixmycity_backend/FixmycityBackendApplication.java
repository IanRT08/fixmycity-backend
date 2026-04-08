package mx.edu.utez.fixmycity_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class FixmycityBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FixmycityBackendApplication.class, args);
	}

}
