package com.jotave.alert_processor.service;

import com.jotave.alert_processor.dto.AlertEventDTO;
import com.jotave.alert_processor.entity.AlertEntity;
import com.jotave.alert_processor.enums.AlertStatus;
import com.jotave.alert_processor.repository.AlertRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AlertProcessingServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private AlertProcessingService alertProcessingService;

    @Test
    void processingAlert_shouldSaveEntityWithProcessedStatus(){

        // Arrange
        AlertEventDTO event = new AlertEventDTO();
        event.setMessageId("22");
        event.setClientId("22");
        event.setMessage("Test Message");

        ArgumentCaptor<AlertEntity> entityCaptor = ArgumentCaptor.forClass(AlertEntity.class);

        // Act
        alertProcessingService.processAndSaveAlert(event);

        // Assert
        verify(alertRepository).save(entityCaptor.capture());

        AlertEntity savedEntity = entityCaptor.getValue();
        assertEquals(22L, savedEntity.getClientId());
        assertEquals("Test Message", savedEntity.getMessage());
        assertEquals(AlertStatus.PROCESSADO, savedEntity.getStatus());

    }

}
