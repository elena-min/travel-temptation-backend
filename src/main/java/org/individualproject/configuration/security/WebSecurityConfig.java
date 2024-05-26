package org.individualproject.configuration.security;

import org.individualproject.configuration.security.auth.AuthenticationRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
@Configuration
public class WebSecurityConfig {

    @Bean
    //The security filter chain is responsible for processing incoming requests and enforcing security rules
    //'AuthenticationEntryPoint' handles unauthorized requests and redirects users to the login page or returns error messages
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity,
                                           AuthenticationEntryPoint authenticationEntryPoint,
                                           AuthenticationRequestFilter authenticationRequestFilter) throws Exception{
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(registry ->
                        registry
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers(HttpMethod.DELETE, "/bookings/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/excursions/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/excursions").permitAll()
                                .requestMatchers(HttpMethod.DELETE, "/excursions/**").permitAll()
                                .requestMatchers(HttpMethod.PUT, "/excursions/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/register/user").permitAll()
                                .requestMatchers(HttpMethod.POST, "/register/traveling-agency").permitAll()
                                .requestMatchers(HttpMethod.POST, "/login").permitAll()
                                .requestMatchers(HttpMethod.GET, "/users/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/users").permitAll()
                                .requestMatchers(HttpMethod.PUT, "/users/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/bookings").permitAll()
                                .requestMatchers(HttpMethod.GET, "/payment-details/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/payment-details").permitAll()
                                .requestMatchers(HttpMethod.GET, "/reviews/**").permitAll()
                                .anyRequest().authenticated()
                )
                .exceptionHandling(configure -> configure.authenticationEntryPoint(authenticationEntryPoint))
                .addFilterBefore(authenticationRequestFilter, UsernamePasswordAuthenticationFilter.class); //Adds the custom filter before the default one
        return httpSecurity.build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer(){
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("http://localhost:5173")
                        .allowedHeaders("*")
                        .exposedHeaders("Access-Control-Allow-Origin")
                        .allowedMethods("*");
            }
        };
    }

}
