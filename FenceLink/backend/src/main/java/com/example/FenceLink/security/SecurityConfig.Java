import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.SecurityWebFilterChain;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.cors().and()
            .csrf.disable();
            // .and()
            // .oauth2ResourceServer(oauth2 -> oauth2
            //     .jwt(jwt -> jwt
            //         .jwtAuthenticationConverter(jwtAuthenticationConverter())
            //     )
            //);
        return http.build();
    }

    // @Bean
    // public JwtAuthenticationConverter jwtAuthenticationConverter() {
    //     JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    //     grantedAuthoritiesConverter.setAuthoritiesClaimName("roles"); // or whatever your roles claim is named in the JWT
        
    //     JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    //     jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        
    //     return jwtAuthenticationConverter;
    // }
    
}
