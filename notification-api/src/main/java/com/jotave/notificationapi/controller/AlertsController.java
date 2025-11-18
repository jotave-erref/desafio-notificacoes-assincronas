package com.jotave.notificationapi.controller;

import com.jotave.notificationapi.dto.AlertRequestDTO;
import com.jotave.notificationapi.service.AlertService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/alerts")
@Slf4j
public class AlertsController {

    private final AlertService service;

    public AlertsController(AlertService service){
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> sendAlert(@RequestBody AlertRequestDTO alertRequest){

        service.processAlert(alertRequest);

        return ResponseEntity.accepted().build();
    }
}
