package com.concurrency.org.concurrency.controller;

import com.concurrency.org.concurrency.config.SecurityConfig;
import com.concurrency.org.concurrency.service.ConcurrencyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequiredArgsConstructor
public class EntryPointController {


    private final ConcurrencyService concurrencyService;

    @GetMapping("/user-details")
    public ResponseEntity<String> giveMeUserDetails(@RequestParam String requestId) throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.OK).body(concurrencyService.userDetails(requestId));
    }

}
