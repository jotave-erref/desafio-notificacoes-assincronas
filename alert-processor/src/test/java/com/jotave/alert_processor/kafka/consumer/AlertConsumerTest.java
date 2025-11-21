package com.jotave.alert_processor.kafka.consumer;

import com.jotave.alert_processor.dto.AlertEventDTO;
import com.jotave.alert_processor.service.AlertProcessingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AlertConsumerTest {

    @Mock
    private AlertProcessingService alertProcessingService;

    @InjectMocks
    private AlertConsumer alertConsumer;

    @Test
    void consumerAlert_whenAlertFail_shouldCallSaveFailedAlert(){
        // Arrange
        AlertEventDTO event = new AlertEventDTO();
        event.setMessageId("uuid-error");

        // Configura o Mock para lançar um exceção quando alertProcessingServive for chamado
        doThrow(new RuntimeException("Simulated processing error"))
                .when(alertProcessingService).processAndSaveAlert(event);

        // Act
        alertConsumer.consumeAlert(event);

        verify(alertProcessingService).processAndSaveAlert(event);

        verify(alertProcessingService).saveFailedAlert(eq(event), anyString());
    }
}
