package com.fullstack.gstbillingwithinvoicemngmt.config;

import com.fullstack.gstbillingwithinvoicemngmt.service.UserInfoService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

    @Configuration
    @EnableWebSecurity
    public class SecurityConfig {

        private final JWTFilter jwtFilter;
        private final UserInfoService userInfoService;

        public SecurityConfig(@Lazy JWTFilter jwtFilter, @Lazy UserInfoService userInfoService) {
            this.jwtFilter = jwtFilter;
            this.userInfoService = userInfoService;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public DaoAuthenticationProvider daoAuthenticationProvider() {
            DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userInfoService);
            //daoAuthenticationProvider.setUserDetailsService(userInfoService);
            daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
            return daoAuthenticationProvider;
        }

        @Bean
        @SneakyThrows
        public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
            return configuration.getAuthenticationManager();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
            httpSecurity
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(authReq ->
                            authReq.requestMatchers("/v3/api-docs", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                                    .permitAll()
                                    .requestMatchers("/v1/auth/**")
                                    .permitAll()
                                    .anyRequest()
                                    .authenticated())
                    .exceptionHandling(exception ->
                            exception.authenticationEntryPoint((req, res, ex) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED)))
                    .sessionManagement(session ->
                            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

            httpSecurity.authenticationProvider(daoAuthenticationProvider());
            httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
            return httpSecurity.build();
        }

        @Bean
        public FilterRegistrationBean<CorsFilter> corsConfigurationSource() {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.addAllowedOrigin("http://localhost:9002");
            configuration.addAllowedMethod("POST");
            configuration.addAllowedMethod("GET");
            configuration.addAllowedMethod("DELETE");
            configuration.addAllowedMethod("PUT");
            configuration.addAllowedMethod("OPTIONS");
            configuration.addAllowedMethod("PATCH");
            configuration.addAllowedHeader("Authorization");
            configuration.addAllowedHeader("Accept");
            configuration.addAllowedHeader("Content-Type");
            configuration.setAllowCredentials(true);
            configuration.setMaxAge(3600L);

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);

            FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
            bean.setOrder(-999);
            return bean;
        }
    }

