package ar.edu.iua.iw3.gastrack.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import ar.edu.iua.iw3.gastrack.auth.IUserBusiness;
import ar.edu.iua.iw3.gastrack.auth.custom.CustomAuthenticationManager;
import ar.edu.iua.iw3.gastrack.auth.filters.JWTAuthorizationFilter;
import ar.edu.iua.iw3.gastrack.controllers.Constants;
/**
 * Configuración de seguridad para la aplicación GasTrack.
 * Define los beans necesarios para la autenticación y autorización.
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-12-07
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

	/*
	 * @Bean SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	 * // CORS: https://developer.mozilla.org/es/docs/Web/HTTP/CORS // CSRF:0
	 * https://developer.mozilla.org/es/docs/Glossary/CSRF
	 * http.cors(CorsConfigurer::disable);
	 * http.csrf(AbstractHttpConfigurer::disable); http.authorizeHttpRequests(auth
	 * -> auth .requestMatchers("/**").permitAll() .anyRequest().authenticated() );
	 * return http.build(); }
	 */

	@Bean
	PasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
/* 
	@Bean
	WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedMethods("*").allowedHeaders("*").allowedOrigins("*");
			}
		};
	}
*/
	@Autowired
	private IUserBusiness userBusiness;

	@Bean
	AuthenticationManager authenticationManager() {
		return new CustomAuthenticationManager(bCryptPasswordEncoder(), userBusiness);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.cors(); 

		// CORS: https://developer.mozilla.org/es/docs/Web/HTTP/CORS

		// CSRF: https://developer.mozilla.org/es/docs/Glossary/CSRF
		http.csrf(AbstractHttpConfigurer::disable);
		http.authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.POST, Constants.URL_LOGIN).permitAll()
				.requestMatchers("/v3/api-docs/**").permitAll().requestMatchers("/swagger-ui.html").permitAll()
				.requestMatchers("/swagger-ui/**").permitAll().requestMatchers("/ui/**").permitAll()
				.requestMatchers("/demo/**").permitAll().requestMatchers("/websocket/**").permitAll().anyRequest().authenticated());
		//http.httpBasic(Customizer.withDefaults());
		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		http.addFilter(new JWTAuthorizationFilter(authenticationManager()));
		return http.build();

	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of("http://localhost:3000"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("*")); 
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config); 

		return source;
	}


}