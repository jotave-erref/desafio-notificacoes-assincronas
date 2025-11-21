package com.jotave.alert_processor.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jotave.alert_processor.dto.AlertEventDTO;
import com.jotave.alert_processor.entity.AlertEntity;
import com.jotave.alert_processor.repository.AlertRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Teste de Integração End-to-End para o Consumidor de Alertas
 *
 * Objetivo: Verificar se o fluxo completo (Kafka -> Consumer -> Banco de Dados)
 * funciona corretamente em um ambiente isolado
 *
 * Tech Stack de Teste:
 *
 *   EmbeddedKafka: Simula o broker Kafka em memória para não depender de containers externos.
 *   H2 Database: Banco em memória configurado para simular a sintaxe do MySQL.
 *   Awaitility: Garante a verificação assíncrona robusta, evitando "flaky tests" com Thread.sleep.
 *
 *
 */

@SpringBootTest // Carrega o contexto completo do Spring
@EmbeddedKafka(partitions = 1, topics = "alerts")
@TestPropertySource(properties = {
        // Configuração dinâmica para conectar ao Kafka em memória (porta aleatória)
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",

        // Segurança: Permite a desserialização de pacotes DTO confiáveis no ambiente de teste
        "spring.kafka.consumer.properties.spring.json.trusted.packages=*",


        // Configuração do Banco H2 em modo de compatibilidade com MySQL 'MODE=MySQL'
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MySQL",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",

        // Dialeto forçado para evitar erros de sintaxe (ex: engine=InnoDB) na criação de tabelas
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",

})
@DirtiesContext // Garante um contexto limpo para cada execução, resetando o Kafka e o DB

public class AlertConsumerIntegrationTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private AlertRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup(){
        repository.deleteAll(); // Garante idempotência entre os testes, limpando o estado do banco
    }

    @Test
    void consumer_shouldReceiveMesageAndPersistInDatabase() throws JsonProcessingException {
        // Arrange
        String messageId = "integration-test-uuid";
        AlertEventDTO event = new AlertEventDTO();
                event.setMessageId(messageId);
                event.setClientId(String.valueOf(112233L));
                event.setAlertType("INTEGRATION_TEST");
                event.setMessage("Kafka rocks");


        String payload = objectMapper.writeValueAsString(event);

        // Act
        // Enviar uma mensagem real (como String Json) par ao tópico do Kafka em memória
        kafkaTemplate.send("alerts", messageId, payload);

        // Assert
        // COmo a mensagem é assincrona, precisa esperar um pouco até que a mensagem seja processada e salva no banco. Uso da biblioteca Awaitility
        // Pattern: Polling Assíncrono.
        // O Awaitility verificará o banco repetidamente (a cada 100ms por padrão) até que a condição seja verdadeira
        // ou o timeout de 10s expire. Isso é muito mais robusto que um Thread.sleep(5000).
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {

            var alerts = repository.findAll();

            // Fail-fast: Verifica o tamanho antes de acessar o índice
            // Isso evita o IndexOutOfBoundsException e dá uma mensagem de erro útil
            assertEquals(1, alerts.size(), "Deveria ter exatamente 1 alerta salvo no banco");


            AlertEntity savedAlert = alerts.get(0);

            // Verificação de Integridade dos dados
            assertNotNull(savedAlert);
            assertEquals(112233L, savedAlert.getClientId());
        });
    }

}
