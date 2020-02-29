package com.ecommerce.ecommApp.commons.exceptions;

import com.ecommerce.ecommApp.recommend.view.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;

@Slf4j
@RestControllerAdvice
public class ExceptionHandleController {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse> handleRunTimeException(HttpServletRequest req, Exception e) {

        log.info("Catch Exceptions ----------\n");
        log.error("Message : " + e.getMessage() + "\nCause : " + e.getCause());

        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                .body(new ApiResponse(HttpStatus.NOT_ACCEPTABLE,e.getMessage(),null));
    }

    @ExceptionHandler(DocumentExceptionHandle.class)
    public ResponseEntity<ApiResponse> handleDocumentException(HttpServletRequest request, Exception e) {

        log.info("Document Exception catch-----------\n");
        log.error("Message : " + e.getMessage() + "\nCause : " + e.getCause());

        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body(new ApiResponse(HttpStatus.NOT_ACCEPTABLE, e.getMessage(),null));
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ApiResponse> handleFileNotFountException(HttpServletRequest request, Exception e) {

        log.info("File not found exception catch---------\n");
        log.error("Message : " + e.getMessage() + "\nCause : " + e.getCause());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(HttpStatus.NOT_FOUND, e.getMessage(),null));
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiResponse> handleIOException(HttpServletRequest request, Exception e) {

        log.error("IOException catch : ------------\n");
        log.error("Message : " + e.getMessage() + "\nCause : " + e.getCause());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(HttpStatus.NOT_FOUND, e.getMessage(),null));
    }

}
