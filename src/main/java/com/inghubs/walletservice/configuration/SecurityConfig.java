package com.inghubs.walletservice.configuration;

import com.inghubs.walletservice.service.impl.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


/**
 * Security configuration class for the application.
 * Configures HTTP security, method security, and password encoding.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Configures the security filter chain for the application.
     *
     * @param http The HttpSecurity object used to configure security settings.
     * @param userDetailsService Custom implementation of UserDetailsService for authentication.
     * @return A configured SecurityFilterChain bean.
     * @throws Exception If an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
        CustomUserDetailsService userDetailsService) throws Exception {
      http
          .csrf(csrf -> csrf.disable())
          .authorizeHttpRequests(auth -> auth
              .requestMatchers("/h2-console/**").permitAll()
              .requestMatchers("/api/wallets/**").authenticated()
              .anyRequest().permitAll()
          )
          .headers(headers -> headers
              .frameOptions(frameOptions -> frameOptions.sameOrigin())
          )
          .userDetailsService(userDetailsService)
          .httpBasic(httpBasic -> {
          });
      return http.build();
    }

    /**
     * Provides a PasswordEncoder bean for encoding passwords.
     *
     * @return A PasswordEncoder instance that uses a delegating password encoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
      return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
