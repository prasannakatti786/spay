package com.spay.wallet.customerservice.exection;
import com.spay.wallet.customerservice.common.exceptions.ApiException;
import com.spay.wallet.customerservice.common.exceptions.ApiExceptionResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ApiExceptionHandler1 extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {ApiException.class})
    public ResponseEntity<ApiExceptionResponse> apiExceptionResponse(ApiException e) {
        var apiException = new ApiExceptionResponse(e.getMessage(), LocalDateTime.now(),e.getStatus(),e.getStatusNumericCode());
        return new ResponseEntity<>(apiException, e.getStatus());
    }

//    @ExceptionHandler(value = {Exception.class})
//    public ResponseEntity<ApiExceptionResponse> exception(Exception e) {
//        var apiException = new ApiExceptionResponse(e.getMessage(), LocalDateTime.now(),HttpStatus.NOT_EXTENDED);
//        return new ResponseEntity<>(apiException, HttpStatus.NOT_EXTENDED);
//    }





    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        var exception = ex.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).reduce((s, s2) -> s+"\n"+s2)
                .orElse("Error occurred please check your request payload");
        var apiException = new ApiExceptionResponse(exception, LocalDateTime.now(),HttpStatus.valueOf(status.value()),status.value());
        return new ResponseEntity<>(apiException, status);
    }

}
