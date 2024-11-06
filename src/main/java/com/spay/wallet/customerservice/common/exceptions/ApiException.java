package com.spay.wallet.customerservice.common.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final Integer statusNumericCode;
    public ApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.statusNumericCode = status.value();
    }

}
