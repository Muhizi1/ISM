package com.Inventory.ims.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 6 configuration using component-based approach.
 * WebSecurityConfigurerAdapter was removed in Spring Boot 3.x / Spring Security 6.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private DaoAuthenticationProvider authenticationProvider;

    /**
     * Password encoder bean using BCrypt hashing algorithm.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the AuthenticationManager bean for use in controllers/services.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Defines the main HTTP security filter chain with URL-based access rules,
     * form login, and logout behaviour.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authenticationProvider)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/dashboard").hasAnyRole("SYSADMIN", "MANAGER", "USER")
                .requestMatchers("/profile").hasAnyRole("SYSADMIN", "MANAGER", "USER")
                .requestMatchers("/equipment/**").hasAnyRole("SYSADMIN", "MANAGER")
                .requestMatchers("/users/**").hasRole("SYSADMIN")
                .requestMatchers("/assignments/**").hasAnyRole("SYSADMIN", "MANAGER")
                .requestMatchers("/maintenance/**").hasAnyRole("SYSADMIN", "MANAGER")
                .requestMatchers("/reports/**").hasAnyRole("SYSADMIN", "MANAGER")
                .requestMatchers("/admin/**").hasRole("SYSADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
