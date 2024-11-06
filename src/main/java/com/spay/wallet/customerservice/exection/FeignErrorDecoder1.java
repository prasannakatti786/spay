package com.spay.wallet.customerservice.exection;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.spay.wallet.customerservice.common.exceptions.ApiException;
import com.spay.wallet.customerservice.common.exceptions.ApiExceptionResponse;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
@Slf4j
public class FeignErrorDecoder1 implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        ApiExceptionResponse message ;
        try (InputStream bodyIs = response.body()
                .asInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            message = mapper.readValue(bodyIs, ApiExceptionResponse.class);
        } catch (IOException e) {

            log.error("Error-json-mapper {} {} {}",methodKey,response, e.getMessage());
            return new ApiException("Unexpected error occurred please try again latter", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(response.status() == 500)  return new ApiException("Unexpected error occurred please try again latter", HttpStatus.INTERNAL_SERVER_ERROR);

        return new ApiException(message.message(),message.status());
    }
}
