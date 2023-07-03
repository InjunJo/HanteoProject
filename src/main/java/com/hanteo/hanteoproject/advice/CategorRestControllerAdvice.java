package com.hanteo.hanteoproject.advice;

import com.hanteo.hanteoproject.exception.DuplicateException;
import com.hanteo.hanteoproject.exception.NotFoundException;
import com.hanteo.hanteoproject.exception.NotFoundParentException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class CategorRestControllerAdvice {

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<Object> handleDuplicateException(Exception e){

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler({NotFoundParentException.class, NotFoundException.class})
    public ResponseEntity<Object> handleNotFoundException(Exception e){

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }




}
