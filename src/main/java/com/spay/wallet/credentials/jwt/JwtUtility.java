package com.spay.wallet.credentials.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import com.spay.wallet.credentials.CredentialUtil;
import com.spay.wallet.credentials.TokenResponse;
import com.spay.wallet.credentials.UserType;
import com.spay.wallet.exections.ApiException;
import com.spay.wallet.properies.WalletProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class JwtUtility {

    private final WalletProperties properties;

    private String getToken(UserType userType,String id,String sessionId,Long credentialId, int minutes) {
        return JWT.create().withSubject(id).withExpiresAt(expireDate(minutes))
                .withClaim("role", userType.name())
                .withClaim("sessionId",sessionId)
                .withClaim("credentialId",credentialId)
                .sign(getAlgorithm());
    }

    public UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(HttpServletRequest request) throws JWTVerificationException {
        DecodedJWT decodedJWT = verifyTokenInHttpRequest(request);
        String username = getUsername(decodedJWT);
        var role = decodedJWT.getClaim("role").asString();
        var sessionId = decodedJWT.getClaim("sessionId").asString();
        var credentialId = decodedJWT.getClaim("credentialId").asInt();
        request.setAttribute("userId",decodedJWT.getSubject());
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
        return new UsernamePasswordAuthenticationToken(username, new JwtSession(credentialId.longValue(),sessionId), authorities);
    }

    private DecodedJWT verifyTokenInHttpRequest(HttpServletRequest request) {
        var authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null)
            throw new ApiException("Unauthorized",HttpStatus.UNAUTHORIZED);
        var token = authorizationHeader.substring("Bearer ".length());
        return verifyToken(token);
    }

    private DecodedJWT verifyToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(getAlgorithm()).build();
            return verifier.verify(token);
        } catch (RuntimeException e) {
            throw new ApiException(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    public Map<String, String> getAccountDTO(DecodedJWT decodedJWT) {
        var userType =decodedJWT.getClaim("role").as(UserType.class);
        var accountId = decodedJWT.getSubject();
        return Map.of("ROLE",userType.toString(),"Id",accountId);
    }



    public String getUsername(DecodedJWT decodedJWT) {
        return decodedJWT.getSubject();
    }

    public TokenResponse generateJWT(UserType  userType,String sessionId,Long credentialId, String id) {
        var security = properties.getSecurity();
        final var ACCESS_TOKEN = getToken(userType,id,sessionId,credentialId,security.getTokenDuration());
        return new TokenResponse(ACCESS_TOKEN, "bearer", null,null,null);
    }

    private Algorithm getAlgorithm() {
        var security = properties.getSecurity();
        return Algorithm.HMAC256(security.getJwtSecret().getBytes());
    }

    private Date expireDate(int minutes) {
        return new Date(System.currentTimeMillis() + (long) minutes * 60 * 1000);
    }
}