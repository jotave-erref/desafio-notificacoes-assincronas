package com.jotave.notificationapi.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertRequestDTO {
    private Long clientId;
    private String alertType;
    private String message;
}

