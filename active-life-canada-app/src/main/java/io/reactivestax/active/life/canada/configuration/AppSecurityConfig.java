package io.reactivestax.active.life.canada.configuration;

import io.reactivestax.active.life.canada.constant.Endpoints;
import io.reactivestax.active.life.canada.filter.JwtAuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class AppSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable).formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests.requestMatchers(HttpMethod.POST,
                                Endpoints.BASE_ENDPOINT + Endpoints.LOGIN,
                                Endpoints.BASE_ENDPOINT + Endpoints.LOGIN_2FA,
                                Endpoints.BASE_ENDPOINT + Endpoints.SIGNUP,
                                Endpoints.BASE_ENDPOINT + Endpoints.OFFERED_COURSES).permitAll())
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests.requestMatchers(HttpMethod.GET, "/activate/**", "/swagger-ui/**",
                                "/v3/api-docs/**", "/v3/api-docs/swagger-config",
                                Endpoints.BASE_ENDPOINT + Endpoints.ACTIVATION,
                                Endpoints.BASE_ENDPOINT + Endpoints.OFFERED_COURSES,
                                Endpoints.BASE_ENDPOINT + Endpoints.SEARCH_OFFERED_COURSES).permitAll())
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests.requestMatchers(HttpMethod.PUT,
                                Endpoints.BASE_ENDPOINT + Endpoints.OFFERED_COURSES).permitAll())
                .addFilter(new JwtAuthorizationFilter(authenticationManager))
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());

        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
