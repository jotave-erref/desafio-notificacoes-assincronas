# 🎯 Sistema de Notificações Assíncronas (PoC)

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.0-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Kafka](https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apache-kafka&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-00000F?style=for-the-badge&logo=mysql&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-CC0200?style=for-the-badge&logo=flyway&logoColor=white)

> **Prova de Conceito (PoC)** desenvolvida como parte de um desafio técnico para a posição de Desenvolvedor Java.

---

## 📄 Sobre o Projeto

Este projeto é uma Prova de Conceito (PoC) de um sistema de notificações assíncronas. O objetivo é demonstrar habilidades em arquitetura de microsserviços, comunicação via mensageria, persistência de dados e qualidade de código através de testes automatizados.

---

## 🏛️ Arquitetura

A solução é composta por dois microsserviços desacoplados que se comunicam através de um broker **Apache Kafka**, seguindo um fluxo de trabalho assíncrono:

```mermaid
graph LR
    A[Cliente] -->|1. POST /alerts| B(notification-api)
    B -->|2. Publica Evento| C{Apache Kafka}
    C -->|3. Consome Evento| D(alert-processor)
    D -->|4. Persiste Alerta| E[(MySQL DB)]

   %% Cliente = Vermelho
    style A fill:#ff4d4d,stroke:#333,stroke-width:2px,color:#fff
    
    %% notification-api = Amarelo
    style B fill:#f1c40f,stroke:#333,stroke-width:2px,color:#000
    
    %% Apache Kafka = Verde
    style C fill:#2ecc71,stroke:#333,stroke-width:2px,color:#fff
    
    %% alert-processor = Azul
    style D fill:#3498db,stroke:#333,stroke-width:2px,color:#fff
    
    %% MySQL DB = Roxo
    style E fill:#9b59b6,stroke:#333,stroke-width:2px,color:#fff
````
1. ***notification-api***: Recebe uma requisição HTTP, a enriquece com um messageId e a publica imediatamente no Kafka, retornando 202 Accepted.
2. ***alert-processor***: Consome a mensagem do tópico, simula um processamento (delay) e persiste o resultado no banco de dados MySQL com o status final.

---

## 🛠️ Tecnologias Utilizadas

Abaixo estão as principais tecnologias e bibliotecas empregadas no projeto:

| Categoria | Tecnologia |
| :--- | :--- |
| **Linguagem** | Java 17 |
| **Framework** | Spring Boot 3.2.0 |
| **Mensageria** | Apache Kafka |
| **Banco de Dados** | MySQL 8.0 |
| **Migrations** | Flyway |
| **Validação** | Spring Boot Validation (Jakarta Bean Validation) |
| **Build** | Maven |
| **Infraestrutura** | Docker & Docker Compose |
| **Testes** | JUnit 5, Mockito, Spring Kafka Test, H2 Database |

---

## 📁 Estrutura do Projeto

O repositório está organizado como um **monorepo**. Abaixo a visão geral dos diretórios:

```text
.
├── alert-processor/      # Microsserviço consumidor (Kafka Listener -> MySQL)
├── notification-api/     # Microsserviço produtor (API REST -> Kafka)
├── docker-compose.yml    # Arquivo de infraestrutura (Kafka, Zookeeper, MySQL)
└── README.md             # Documentação do projeto

```
---

```markdown
## 🚀 Como Executar o Projeto

Siga os passos abaixo para executar a aplicação completa em seu ambiente local.

### Pré-requisitos
*   Java 17 ou superior
*   Apache Maven 3.8+
*   Docker e Docker Compose

### Passo a Passo
````
**1. Clone o Repositório**


```bash
git clone https://github.com/seu-usuario/desafio-notificacoes-assincronas.git
cd desafio-notificacoes-assincronas
```
----

**2. Suba a Infraestrutura com Docker**

Este comando iniciará os containers do Kafka, Zookeeper e MySQL em background.

````bash
docker-compose up -d
````
*⏳ Atenção: Aguarde cerca de 1 minuto para que todos os serviços (especialmente o Kafka e o MySQL) estejam completamente operacionais antes de prosseguir.*


**3. Execute o Consumidor (alert-processor)**

Em um novo terminal, navegue até a pasta do consumidor:


```bash
cd alert-processor
mvn spring-boot:run
````


**4. Execute o Produtor (notification-api)**

Em um terceiro terminal, navegue até a pasta do produtor:

````bash
cd notification-api
mvn spring-boot:run
````

---


## ✅ Como Testar a Aplicação
**1. Enviar Requisição (Produtor)**
Use o curl ou uma ferramenta como Postman/Insomnia para enviar uma notificação:


````bash
curl -X POST http://localhost:8080/alerts \
-H "Content-Type: application/json" \
-d '{
      "clientId": 12345,
      "alertType": "FATURA_DISPONIVEL",
      "message": "Sua fatura do cartão de crédito está disponível para visualização."
    }'
````
Resultado Esperado: **Status 202 Accepted** (A solicitação foi aceita para processamento assíncrono).


**2. Verificar Processamento (Consumidor)**
Nos Logs: Verifique se o notification-api confirmou o envio e se o alert-processor confirmou o recebimento e persistência.

No Banco de Dados:

````bash
# Conecta ao cliente MySQL dentro do container
docker exec -it mysql mysql -u root -p
# Digite a senha: rootpassword

# Executa a query

USE alerts_db;
SELECT * FROM processed_alerts;
````
Você verá o registro do alerta com o status PROCESSADO.

---


## 🧪 Executando os Testes Automatizados
O projeto possui cobertura de testes de unidade e integração. Para executá-los:

````bash
# Na raiz de cada microsserviço (notification-api/ ou alert-processor/)
mvn clean install
````
Isso irá rodar os testes unitários (Mockito) e os testes de integração (que sobem um Kafka embutido e um banco H2 em memória).

---


## ✨ Decisões de Design e Boas Práticas
**Este projeto foi construído com foco em práticas modernas de desenvolvimento de software:**

**Assincronismo e Desacoplamento:** O uso do Kafka permite que o notification-api responda instantaneamente, melhorando a experiência do usuário e a resiliência do sistema.

**Confiabilidade (Reliability):** O notification-api é configurado com acks=all e enable.idempotence=true, garantindo a entrega de mensagens "exactly-once" do lado do produtor.

**Tratamento de Erros Robusto:** O alert-processor utiliza blocos try-catch estratégicos para garantir que falhas sejam persistidas com o status FALHA, evitando perda de dados.

**Validação de Entrada:** O notification-api utiliza Jakarta Bean Validation (@Valid) para garantir a integridade dos dados na entrada. Um @RestControllerAdvice captura erros e retorna respostas 400 Bad Request claras.

**Versionamento de Banco de Dados:** Uso do Flyway para gerenciar as migrações (migrations) do banco de dados, garantindo que a evolução do schema seja controlada e reprodutível, ao invés de usar o instável hibernate.ddl-auto.

**Qualidade de Código e Testes:**

**Unitários:** Mockito para testes rápidos de regras de negócio.
**Integração:** @EmbeddedKafka e banco H2 para validar o fluxo real sem depender de infraestrutura externa instalada.


