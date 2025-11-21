package com.jotave.alert_processor.kafka.consumer;

import com.jotave.alert_processor.dto.AlertEventDTO;
import com.jotave.alert_processor.service.AlertProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AlertConsumer {

    private final AlertProcessingService alertProcessingService;


    /**
     *  @KafkaListener
     *   - Conecta-se ao Kafka.
     *   - Inscreve-se no tópico definido em 'topics'.
     *   - Fica escutando por novas mensagens.
     *   - Quando uma mensagem chega, o Spring a desserializa do JSON para o objeto AlertEventDTO
     *     e entrega como um parâmetro para este método
     */
    @KafkaListener(topics = "${app.kafka.topic}", groupId = "${{spring.kafka.consumer.group-id}")
    public void consumeAlert(AlertEventDTO alertEventDTO){
        log.info("<<< Alerta recebido do Kafka! messageId: {}", alertEventDTO.getMessageId());

        try{
            alertProcessingService.processAndSaveAlert(alertEventDTO);
        }catch (Exception e){
            /*
                - Se algum error insesperado acontecer durante o processamento
                - ele é capturado e salvado com o status de falha
                - nenhuma mensagem é perdida mesmo em caso de error
             */
            alertProcessingService.saveFailedAlert(alertEventDTO, e.getMessage());
        }
    }
}
