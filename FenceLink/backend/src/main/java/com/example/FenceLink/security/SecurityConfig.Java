package com.example.FenceLink.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
     
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(encoder());
 
        return authProvider;
    }

    @Bean
    public SecurityFilterChain SecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((authz)) -> authz

                // Anyone can retrieve players 
                .requestMatchers(HttpMethod.GET, "/api/players/**").hasAnyRole()
                
                // Players can edit their own details (excluding points)
                .requestMatchers(HttpMethod.GET, "/api/players/{id}/edit").hasAnyRole("PLAYER", "ADMIN")

                // Only admin can add & delete players, and update points
                .requestMatchers(HttpMethod.POST, "/api/players").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/players/{id}/updatePoints").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/players/*").hasRole("ADMIN")

                .anyRequest().authenticated()
            .and()
                .csrf().disable();
    }

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
