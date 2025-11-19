package com.jotave.alert_processor.dto;

import lombok.Data;

@Data
public class AlertEventDTO {

    private String messageId;
    private String clientId;
    private String alertType;
    private String message;
}
