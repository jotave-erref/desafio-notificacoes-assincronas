package com.jotave.notificationapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertRequestDTO {

    @NotNull(message = "O clientId não pode ser nulo.")
    private Long clientId;

    @NotBlank(message = "O alertType não pode estar em branco.")
    private String alertType;

    @NotBlank(message = "A mensagem não pode estar em branco.")
    private String message;
}

