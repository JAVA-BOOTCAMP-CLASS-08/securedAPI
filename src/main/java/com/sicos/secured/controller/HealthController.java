package com.sicos.secured.controller;

import com.sicos.secured.model.Message;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1", produces = "application/json")
public class HealthController {

    @GetMapping(path = "/health")
    @Operation(description = "Consultar el estado del servicio",
            method = "GET",
            operationId = "Status",
            tags = "Status")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Message> health() {

        return ResponseEntity.ok(new Message("OK"));
    }
}
