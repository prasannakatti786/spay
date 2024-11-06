package com.spay.wallet.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spay.wallet.remittance.constants.Constants;
import com.spay.wallet.exections.TransactionException;
import com.spay.wallet.remittance.model.ApiConfig;
import com.spay.wallet.remittance.model.ApiResponse;
import com.spay.wallet.remittance.model.transaction.TokenRequest;
import com.spay.wallet.remittance.model.transaction.TokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

@Component
public class RemittanceUtil {
    private static Logger logger = LoggerFactory.getLogger(RemittanceUtil.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    ApiConfig apiConfig;


    public TokenResponse getBearerToken(String uri, String userName, String password) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.setAccept(List.of(MediaType.valueOf(MediaType.ALL_VALUE)));

            TokenRequest request = new TokenRequest();
            request.setEmail(userName);
            request.setPassword(password);
            ObjectMapper mapper = new ObjectMapper();
            String requestJson = mapper.writeValueAsString(request);
            HttpEntity<String> httpEntity = new HttpEntity<>(requestJson, httpHeaders);
            ResponseEntity<TokenResponse> tokenResponse = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, TokenResponse.class);
            return tokenResponse.getBody();
        } catch (Exception e){
            logger.error("Exception while fetching bearer token", e);
            throw new TransactionException(Constants.Errors.SIGN_IN_FAILED.toString());
        }
    }

    public Object getApiResponse(HttpMethod httpMethod, String apiUrl, String token, Class<?> clazz) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.set("Authorization", "Bearer " + token);
            HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);
            ResponseEntity<?> transactionStatusResponseEntity = restTemplate.exchange(apiUrl, httpMethod, httpEntity, clazz);
            return transactionStatusResponseEntity.getBody();
        } catch (Exception e){
            logger.error("Exception while fetching API Transaction Response", e);
            throw e;
        }
    }

    public Object patchApiResponse(String apiUrl, String token, Class<?> clazz) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            httpHeaders.add(HttpHeaders.ACCEPT, MediaType.ALL_VALUE);
            httpHeaders.add("X-HTTP-Method-Override", "PATCH");
            httpHeaders.add("Authorization", "Bearer " + token);
            HttpEntity<String> httpEntity = new HttpEntity<>(null, httpHeaders);
            ResponseEntity<?> transactionStatusResponseEntity = restTemplate.exchange(apiUrl, HttpMethod.valueOf("POST"), httpEntity, clazz);
            return transactionStatusResponseEntity.getBody();
        } catch (Exception e){
            logger.error("Exception while fetching API Transaction Response", e);
            throw e;
        }
    }

    public Object postApiResponse(Object entity, String apiUrl, String token, Class<?> clazz) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.set("Authorization", "Bearer " + token);
            ObjectMapper mapper = new ObjectMapper();
            String entityValue = mapper.writeValueAsString(entity);
            HttpEntity<String> httpEntity = new HttpEntity<>(entityValue, httpHeaders);
            ResponseEntity<?> transactionStatusResponseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, httpEntity, clazz);
            return transactionStatusResponseEntity.getBody();
        } catch (Exception e){
            logger.error("Exception while fetching API Transaction Response", e);
            throw new TransactionException(Constants.Errors.TRANSACTION_STATUS_BY_PTCN_NO_FAILED.toString());
        }
    }

}
