package com.jotave.notificationapi.service;

import com.jotave.notificationapi.dto.AlertEventDTO;
import com.jotave.notificationapi.dto.AlertRequestDTO;
import com.jotave.notificationapi.kafka.producer.AlertProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertProducer alertProducer;

    public void processAlert(AlertRequestDTO alert){

        var messageId = UUID.randomUUID().toString();

        AlertEventDTO event = AlertEventDTO.builder()
                .messageId(messageId)
                .clientId(alert.getClientId())
                .alertType(alert.getAlertType())
                .message(alert.getMessage())
                .build();

        alertProducer.send(event);
    }
}
