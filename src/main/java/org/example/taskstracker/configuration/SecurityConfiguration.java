package org.example.taskstracker.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.security.cert.CertPathBuilder;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.security", name = "type",havingValue = "inMemory")
    public SecurityWebFilterChain inMemoryFilterChain(ServerHttpSecurity httpSecurity) {
        return buildDefaultHttpSecurity(httpSecurity).build();
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.security", name = "type", havingValue = "inMemory")
    public ReactiveUserDetailsService inMemoryUserDetailsService(PasswordEncoder passwordEncoder){

        UserDetails user = User.withUsername("user")
                .password(passwordEncoder().encode("12345"))
                .roles("USER")
                .build();

        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder().encode("54321"))
                .roles("ADMIN")
                .build();

        return new MapReactiveUserDetailsService(user, admin);

    }

    private ServerHttpSecurity buildDefaultHttpSecurity(ServerHttpSecurity httpSecurity) {
        return httpSecurity.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange((auth) -> auth
                        .pathMatchers("/api/v1/users/**").permitAll()
                        .pathMatchers("/api/v1/tasks/**").authenticated()
                        .anyExchange().authenticated())
                .httpBasic(Customizer.withDefaults());
    }


}
