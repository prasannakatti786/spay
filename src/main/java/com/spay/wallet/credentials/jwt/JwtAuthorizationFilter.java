package com.spay.wallet.credentials.jwt;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.spay.wallet.config.WalletConfig;
import com.spay.wallet.credentials.CredentialRepository;
import com.spay.wallet.credentials.TokenResponse;
import com.spay.wallet.exections.ApiException;
import com.spay.wallet.exections.ApiExceptionResponse;
import com.spay.wallet.properies.WalletProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtUtility utility;
    private final CredentialRepository credentialRepository;
    private final WalletProperties properties;
    private static final Logger log = LoggerFactory.getLogger(JwtAuthorizationFilter.class);


    @Override

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        log.info("Request URI: {}", request.getRequestURI());
        if (!isMatchApi(request.getRequestURI())) {
            authorizeJwt(request, response);
        }
        chain.doFilter(request, response);
    }

    private void authorizeJwt(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            UsernamePasswordAuthenticationToken token = utility.getUsernamePasswordAuthenticationToken(request);
            var session = (JwtSession) token.getCredentials();
            if(session == null)
                throw new ApiException("Session is expired",HttpStatus.UNAUTHORIZED);
            var foundSession = credentialRepository.sessionIdWithCredential(session.getCredentialId());
            if(foundSession == null || !foundSession.equals(session.getSessionId()))
                throw new ApiException("Session is expired",HttpStatus.UNAUTHORIZED);
            token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(token);
            request.setAttribute("credentialId",session.getCredentialId());
        } catch (ApiException e) {
            var responseException = Map.of("message", e.getMessage(), "status",HttpStatus.UNAUTHORIZED,  "timestamp", LocalDateTime.now().toString());
            ObjectMapper mapper = new ObjectMapper();
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            OutputStream out = response.getOutputStream();
            mapper.writeValue(out, responseException);
            out.flush();
        }
    }


    private boolean isMatchApi(String url) {
        return properties.getSecurity().getAllowedRoutes().stream().anyMatch(url::startsWith);
    }


}