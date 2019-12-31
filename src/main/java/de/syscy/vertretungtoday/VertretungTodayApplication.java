package de.syscy.vertretungtoday;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// Hier ist der Einstiegspunkt für die ganze Server Anwendung!! :)
// Nicht viel zu sehen außer die main Funktion, der Einstiegspunkt, der direkt das alles hier als Spring Applikation startet
// und dann werden hier noch ein paar Konfigurationen mit den @Annotationen bereitgestellt und eine Implementation die zum Passwort
// Verschlüsseln und Überprüfen benutzt werden soll
@EnableScheduling
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class VertretungTodayApplication {
	public static void main(String[] args) {
		SpringApplication.run(VertretungTodayApplication.class, args);
	}

	@Bean
	public PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}