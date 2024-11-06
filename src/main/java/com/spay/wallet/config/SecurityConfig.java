package com.spay.wallet.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.spay.wallet.credentials.jwt.JwtAuthenticationProvider;
import com.spay.wallet.credentials.jwt.JwtAuthorizationFilter;
import com.spay.wallet.exections.ApiException;
import com.spay.wallet.properies.WalletProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.DisableEncodeUrlFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig  {
    private final WalletProperties properties;
    private final JwtAuthorizationFilter jwtTokenFilter;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    // TODO fix access token
    // TODO validate session expire

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(this::corsConfigurationSource);
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(authorize -> {
            authorize.requestMatchers(
                    "/api/v1/customer-registration/**","/api/v1/internal/micro_create-account/**",
                    "/api/v1/customer/micro_registration/**","/api/v1/customer/micro_to_mono_registration/**",
                    "/api/v1/micro_customer/**",
                    "/api/v1/auth/**",
                    "/api/v1/transaction/paypal-payment-success",
                    "/api/v1/transaction/paypal-payment-successs",
                    "/api/v1/transaction/paypal-payment-cancel",
                    "/api/v1/transaction/create-paypal","/api/v1/micro_service_customer_creation/**",
                    "/api/v1/download/profile/**",
                    "/api/v1/download/profile-by-customer/**","/api/v1/micro_to_mono_apis_create_customer/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**").permitAll();
            authorize.anyRequest().authenticated();
        });
//        http.exceptionHandling(httpSecurityExceptionHandlingConfigurer -> {
//            httpSecurityExceptionHandlingConfigurer.accessDeniedHandler(customAccessDeniedHandler());
//            httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(authenticationFailureHandler());
//        });
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authenticationProvider(jwtAuthenticationProvider);
        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

//    @Bean
//   public AccessDeniedHandler customAccessDeniedHandler() {
//        return (request, response, accessDeniedException) -> {
//
//                Authentication auth
//                        = SecurityContextHolder.getContext().getAuthentication();
//            accessDeniedException.printStackTrace();
////                if (auth != null) {
////                    LOG.warn("User: " + auth.getName()
////                            + " attempted to access the protected URL: "
////                            + request.getRequestURI());
////                }
//
//                response.sendRedirect(request.getContextPath() + "/accessDenied");
//        };
//    }

//    @Bean
//    public AccessDeniedHandler accessDeniedHandler() {
//        return (request, response, accessDeniedException) -> ExceptionUtil.exceptionThrow(request, response, "Access Denied", HttpStatus.UNAUTHORIZED);
//    }
//
//    @Bean
//    public AuthenticationEntryPoint authenticationFailureHandler() {
//        return (request, response, e) ->{
//            e.printStackTrace();
//            var responseException = Map.of("message", e.getMessage(), "status",HttpStatus.UNAUTHORIZED,  "timestamp", LocalDateTime.now().toString());
//            ObjectMapper mapper = new ObjectMapper();
//            response.setStatus(HttpStatus.UNAUTHORIZED.value());
//            response.setContentType("application/json");
//            OutputStream out = response.getOutputStream();
//            mapper.writeValue(out, responseException);
//            out.flush();
//        };
//    }



    public void corsConfigurationSource(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
        var configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(properties.getSecurity().getAllowedOrigins());
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS", "DELETE", "PUT"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "content-type", "x-requested-with", "Access-Control-Allow-Origin", "Access-Control-Allow-Headers", "x-auth-token", "x-app-id", "Origin", "Accept", "X-Requested-With", "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        configuration.setAllowCredentials(true);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        httpSecurityCorsConfigurer.configurationSource(source);
    }
}
