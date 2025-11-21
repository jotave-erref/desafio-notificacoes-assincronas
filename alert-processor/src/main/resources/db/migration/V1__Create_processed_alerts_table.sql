CREATE TABLE processed_alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id BIGINT NOT NULL,
    message TEXT NOT NULL,
    status ENUM('PROCESSADO', 'FALHA') NOT NULL,
    created_at DATETIME NOT NULL,
    processed_at DATETIME
);