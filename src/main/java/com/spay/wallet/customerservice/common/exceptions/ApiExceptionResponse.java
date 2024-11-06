package com.spay.wallet.customerservice.common.exceptions;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ApiExceptionResponse(String message, LocalDateTime timestamp, HttpStatus status, Integer statusCode) {
}
