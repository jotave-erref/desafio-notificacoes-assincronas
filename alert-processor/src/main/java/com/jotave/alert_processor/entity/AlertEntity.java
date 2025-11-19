package com.jotave.alert_processor.entity;

import com.jotave.alert_processor.dto.AlertEventDTO;
import com.jotave.alert_processor.enums.AlertStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "processed_alerts")
@Data
@NoArgsConstructor
public class AlertEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long clientId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime processedAt;

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
    }

    public static AlertEntity fromDTO(AlertEventDTO dto){
        AlertEntity entity = new AlertEntity();
        entity.setClientId(Long.valueOf(dto.getClientId()));
        entity.setMessage(dto.getMessage());
        return entity;
    }
}
