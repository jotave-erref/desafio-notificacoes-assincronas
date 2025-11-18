package com.jotave.notificationapi.kafka.producer;

import com.jotave.notificationapi.dto.AlertEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlertProducer {

    private final KafkaTemplate<String, AlertEventDTO> kafkaTemplate;

    @Value("${app.kafka.topic}")
    private String topic;

    public void send(AlertEventDTO event){
        log.info("Enviando evento para o Kafka. messageId: " + event.getMessageId());

        kafkaTemplate.send(topic, event.getMessageId(), event);
    }
}
