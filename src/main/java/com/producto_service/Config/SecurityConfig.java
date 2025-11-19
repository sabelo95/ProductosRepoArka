package com.producto_service.Config;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import java.util.Base64;
import java.util.List;

@Configuration
@EnableMethodSecurity   // 👈 habilita @PreAuthorize
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String secret;

    // SecurityFilterChain para rutas públicas (Swagger) - Sin OAuth2
    @Bean
    @Order(1)
    public SecurityFilterChain publicSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/api-docs",
                        "/api-docs/**",
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/actuator/**",
                        "/swagger-ui/index.html"
                )
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
                // No configurar oauth2ResourceServer - esto excluye estas rutas del procesamiento JWT

        return http.build();
    }

    // SecurityFilterChain para rutas protegidas - Con OAuth2 JWT
    @Bean
    @Order(2)
    public SecurityFilterChain protectedSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // ⚠️ IMPORTANTE: Las reglas más específicas PRIMERO

                        // Endpoints públicos específicos (sin JWT)
                        .requestMatchers(
                                "/productos/lista-ids",
                                "/productos/reducir-stock",
                                "/productos/reposicion-stock",
                                "/reporte/inventario-bajo",
                                "/productos/health"
                        ).permitAll()

                        // Todos los GET públicos (sin JWT)
                        .requestMatchers(HttpMethod.GET, "/productos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/categorias/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/marcas/**").permitAll()

                        // Endpoints de MARCAS que requieren rol ADMINISTRADOR
                        .requestMatchers(HttpMethod.POST, "/marcas/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/marcas/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/marcas/**").hasRole("ADMINISTRADOR")

                        // Endpoints de CATEGORÍAS que requieren rol ADMINISTRADOR
                        .requestMatchers(HttpMethod.POST, "/categorias/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/categorias/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/categorias/**").hasRole("ADMINISTRADOR")

                        // Endpoints de PRODUCTOS que requieren rol ADMINISTRADOR
                        .requestMatchers(HttpMethod.POST, "/productos/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/productos/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/productos/**").hasRole("ADMINISTRADOR")

                        // Endpoints reportes que requieren rol ADMINISTRADOR
                        .requestMatchers(HttpMethod.POST, "/reporte/**").hasRole("ADMINISTRADOR")


                        // Cualquier otra petición requiere autenticación
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(
                        jwt -> jwt.decoder(jwtDecoder()).jwtAuthenticationConverter(jwtAuthenticationConverter())
                ));

        return http.build();
    }


    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }



    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String rol = jwt.getClaimAsString("rol");
            if (rol == null) return List.of();
            return List.of(new SimpleGrantedAuthority("ROLE_" + rol.toUpperCase()));
        });
        return converter;
    }

}
