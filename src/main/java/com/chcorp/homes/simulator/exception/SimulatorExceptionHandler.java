package com.chcorp.homes.simulator.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SimulatorExceptionHandler {

    @ExceptionHandler(PlanNotFoundException.class)
    public ResponseEntity<Void> handlePlanNotFound(PlanNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(PlanAccessDeniedException.class)
    public ResponseEntity<Void> handlePlanAccessDenied(PlanAccessDeniedException e) {
        return ResponseEntity.status(403).build();
    }
}