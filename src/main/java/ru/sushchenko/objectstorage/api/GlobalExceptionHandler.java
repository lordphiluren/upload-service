package ru.sushchenko.objectstorage.api;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.sushchenko.objectstorage.utils.exception.ControllerErrorResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
    // Exception Handling
    @ExceptionHandler(RuntimeException.class)
    private ResponseEntity<ControllerErrorResponse> handleException(RuntimeException e) {
        ControllerErrorResponse errorResponse = new ControllerErrorResponse(e.getMessage(),System.currentTimeMillis());
        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class);
        HttpStatus httpStatus = responseStatus != null ? responseStatus.value() : HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(errorResponse, httpStatus);
    }
}
