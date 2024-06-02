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
                               // .requestMatchers(HttpMethod.DELETE, "/bookings/**").permitAll()
                               // .requestMatchers(HttpMethod.GET, "/excursions/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/excursions").hasAnyRole("TRAVELAGENCY", "ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/excursions/**").hasAnyRole("TRAVELAGENCY", "ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/excursions/**").hasAnyRole("TRAVELAGENCY", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/register/user", "/register/traveling-agency").permitAll()
                               // .requestMatchers(HttpMethod.POST, "/login").permitAll()
                                //.requestMatchers(HttpMethod.GET, "/users/**").permitAll()
                                //.requestMatchers(HttpMethod.POST, "/users").permitAll()
                                .requestMatchers(HttpMethod.PUT, "/users/**").authenticated()
                                //.requestMatchers(HttpMethod.POST, "/bookings").hasRole("USER")
                               // .requestMatchers(HttpMethod.GET, "/bookings/**").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/bookings/**").hasAnyRole("USER", "TRAVELAGENCY")
                                .requestMatchers(HttpMethod.GET, "/payment-details/**", "/bookings/**").authenticated()
                                .requestMatchers(HttpMethod.POST, "/payment-details", "/reviews", "/bookings").hasRole("USER")
                                .requestMatchers(HttpMethod.DELETE, "/payment-details/**").hasRole("USER")
                                .requestMatchers(HttpMethod.PUT, "/payment-details/**").hasRole("USER")
                               // .requestMatchers(HttpMethod.GET, "/reviews/**").permitAll()
                               // .requestMatchers(HttpMethod.POST, "/reviews").hasRole("USER")
                                .requestMatchers(HttpMethod.DELETE, "/reviews/**").hasAnyRole("USER", "ADMIN")
                               //.requestMatchers(HttpMethod.GET, "/trending-excursions").permitAll()
                                .anyRequest().permitAll()
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
