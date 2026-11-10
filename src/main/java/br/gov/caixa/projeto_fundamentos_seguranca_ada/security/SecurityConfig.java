package br.gov.caixa.projeto_fundamentos_seguranca_ada.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import jakarta.servlet.DispatcherType;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

        /*
         * Configuração de segurança do Spring Security para a aplicação.
         * AuthenticationProvider recebe o username e a senha enviados no login
         * O DaoAuthenticationProvider chama UsuarioDetailsService
         * Ele busca o usuario e entrega o hash salvo por
         * UsuarioUserDetails.getPassword()
         * utiliza o BCryptPasswordEncoder para verificar a senha hash.
         * O fluxo de autenticação segue:
         * login -> AuthenticationProvider -> DaoAuthenticationProvider ->
         * UsuarioDetailsService -> UsuarioUserDetails -> PasswordEncoder.
         */

        @Bean
        public AuthenticationManager authenticationManager(
                        UserDetailsService userDetailsService,
                        PasswordEncoder passwordEncoder) {

                DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
                provider.setPasswordEncoder(passwordEncoder);

                return new ProviderManager(provider);
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http)
                        throws Exception {

                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .headers(headers -> headers
                                                .frameOptions(frame -> frame.sameOrigin()))
                                .formLogin(AbstractHttpConfigurer::disable)
                                .httpBasic(AbstractHttpConfigurer::disable)
                                .requestCache(AbstractHttpConfigurer::disable)
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                                                .requestMatchers("/h2-console", "/h2-console/**").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/user").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/authenticate").permitAll()
                                                .anyRequest().authenticated())
                                .exceptionHandling(exceptions -> exceptions
                                                .authenticationEntryPoint((request, response, exception) -> response
                                                                .setStatus(HttpStatus.UNAUTHORIZED.value())));

                return http.build();
        }
}