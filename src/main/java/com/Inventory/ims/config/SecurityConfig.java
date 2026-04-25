package com.Inventory.ims.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring Security 5 configuration using WebSecurityConfigurerAdapter.
 * Compatible with Spring Boot 2.7.x and Spring Security 5.x.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

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
     * Configure AuthenticationManager to use our custom authentication provider.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }

    /**
     * Configure HTTP security with URL-based access rules, form login, and logout behaviour.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authenticationProvider)
            .authorizeRequests()
                .antMatchers("/", "/login", "/css/**", "/js/**", "/images/**").permitAll()
                .antMatchers("/dashboard").hasAnyRole("SYSADMIN", "MANAGER", "USER")
                .antMatchers("/profile").hasAnyRole("SYSADMIN", "MANAGER", "USER")
                .antMatchers("/equipment/**").hasAnyRole("SYSADMIN", "MANAGER")
                .antMatchers("/users/**").hasRole("SYSADMIN")
                .antMatchers("/assignments/**").hasAnyRole("SYSADMIN", "MANAGER")
                .antMatchers("/maintenance/**").hasAnyRole("SYSADMIN", "MANAGER")
                .antMatchers("/reports/**").hasAnyRole("SYSADMIN", "MANAGER")
                .antMatchers("/admin/**").hasRole("SYSADMIN")
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
                .and()
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
                .and()
            .csrf().disable();
    }
}
