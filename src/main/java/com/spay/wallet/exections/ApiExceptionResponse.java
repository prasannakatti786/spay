package com.spay.wallet.exections;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;


@RequiredArgsConstructor
@Getter
public class ApiExceptionResponse {
    private final String message;
    private final LocalDateTime timeStamp;
    private final HttpStatus status;
}
