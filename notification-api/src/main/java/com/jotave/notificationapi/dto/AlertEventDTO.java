package com.jotave.notificationapi.dto;

import lombok.*;

@Data
@Builder
public class AlertEventDTO {
    private String messageId;
    private Long clientId;
    private String alertType;
    private String message;
}