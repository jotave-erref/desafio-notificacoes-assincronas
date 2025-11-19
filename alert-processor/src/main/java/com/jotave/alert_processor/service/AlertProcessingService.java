package com.jotave.alert_processor.service;

import com.jotave.alert_processor.dto.AlertEventDTO;
import com.jotave.alert_processor.entity.AlertEntity;
import com.jotave.alert_processor.enums.AlertStatus;
import com.jotave.alert_processor.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlertProcessingService {

    private final AlertRepository alertRepository;

    // Método para o "caminho feliz" (processamento com sucesso)
    public void processAndSaveAlert (AlertEventDTO eventDTO){
        log.info("Iniciando processamento para messageId: {}", eventDTO.getMessageId());

        try{
            Thread.sleep(500);
        }catch (InterruptedException e){
            log.warn("Thread de processamento foi interrompida. messageId: {}", eventDTO.getMessageId());
            // Boa prática: ao capturar InterruptedException, restaure o status de interrupção
            Thread.currentThread().interrupt();
        }

        AlertEntity alertEntity = AlertEntity.fromDTO(eventDTO);

        alertEntity.setStatus(AlertStatus.PROCESSADO);
        alertEntity.setProcessedAt(LocalDateTime.now());

        alertRepository.save(alertEntity);

        log.info("Alerta salvo com sucesso! Id: {}, messageId: {}", alertEntity.getId(), eventDTO.getMessageId());
    }

    public void saveFailedAlert(AlertEventDTO eventDTO, String errorMessage){
        log.error("Salvando alerta com status de FALHA para messageId: {}. Motivo: {}", eventDTO.getMessageId(), errorMessage);

        AlertEntity alertEntity = AlertEntity.fromDTO(eventDTO);
        alertEntity.setStatus(AlertStatus.FALHA);

        alertEntity.setProcessedAt(LocalDateTime.now());

        alertRepository.save(alertEntity);
    }
}
