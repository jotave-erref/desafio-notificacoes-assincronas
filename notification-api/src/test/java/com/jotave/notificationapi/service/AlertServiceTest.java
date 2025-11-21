package com.jotave.notificationapi.service;

import com.jotave.notificationapi.dto.AlertEventDTO;
import com.jotave.notificationapi.dto.AlertRequestDTO;
import com.jotave.notificationapi.kafka.producer.AlertProducer;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AlertServiceTest {

    @Mock
    private AlertProducer alertProducer;

    @InjectMocks
    private AlertService alertService;

    @Test
    void processAlert_shouldBuildEventAndCallProducer(){
        // Arrange
        AlertRequestDTO request = new AlertRequestDTO();
        request.setClientId(Long.valueOf(1L));
        request.setAlertType("Test_Alert");
        request.setMessage("Test message");

        ArgumentCaptor<AlertEventDTO> eventCaptor = ArgumentCaptor.forClass(AlertEventDTO.class);

        // Act
        alertService.processAlert(request);

        // Assert
        // Verifica se o metodo send() do mock AlertProducer foi chamado exatamente uma vez
        verify(alertProducer).send(eventCaptor.capture());

        AlertEventDTO capturedEvent = eventCaptor.getValue();

        assertNotNull(capturedEvent.getMessageId(), "O messageId não deve ser nulo");
        assertEquals(1L, capturedEvent.getClientId());
        assertEquals("Test_Alert", capturedEvent.getAlertType());
        assertEquals("Test message", capturedEvent.getMessage());
    }
}
