# Relatório de Integração — Sistema de Pedidos com RabbitMQ

## 1. Visão geral da integração

Este projeto implementa uma integração entre dois sistemas Spring Boot utilizando **RabbitMQ** como ferramenta de mensageria. A proposta é simular um fluxo de pedidos em que um serviço produtor envia mensagens para uma fila, enquanto outro serviço consumidor recebe essas mensagens e realiza o processamento, salvando os pedidos no banco de dados.

A arquitetura foi dividida em dois serviços principais:

- **Publisher / Producer**: responsável por receber uma requisição HTTP e publicar uma mensagem na fila do RabbitMQ.
- **Consumer / sistema-pedidos**: responsável por escutar a fila, consumir a mensagem recebida e registrar o pedido no banco de dados PostgreSQL.

Com essa estrutura, os sistemas não precisam se comunicar diretamente entre si. O publisher não chama diretamente o consumer. Em vez disso, ele envia uma mensagem para o RabbitMQ, que fica responsável por armazenar e entregar essa mensagem quando o consumer estiver disponível.

---

## 2. Objetivo da integração

O objetivo principal da integração é demonstrar o uso de **comunicação assíncrona** entre sistemas. Nesse modelo, o envio do pedido não depende do processamento imediato pelo sistema consumidor.

Isso significa que o producer consegue enviar o pedido para a fila mesmo que o consumer esteja temporariamente desligado. Quando o consumer volta a ficar online, ele consome automaticamente as mensagens pendentes e atualiza o banco de dados.

Esse comportamento é importante em sistemas distribuídos, pois aumenta a tolerância a falhas e reduz o acoplamento entre serviços.

---

## 3. Tecnologias utilizadas

Foram utilizadas as seguintes tecnologias no projeto:

- **Java**: linguagem principal da aplicação.
- **Spring Boot**: framework utilizado para criação dos serviços backend.
- **Spring AMQP**: integração do Spring com o RabbitMQ.
- **RabbitMQ**: broker de mensagens utilizado para intermediar a comunicação entre publisher e consumer.
- **Docker**: utilizado para executar o RabbitMQ, PostgreSQL e pgAdmin em containers.
- **PostgreSQL**: banco de dados utilizado pelo sistema de pedidos.
- **pgAdmin**: ferramenta visual para consulta e administração do banco de dados.
- **Postman**: ferramenta utilizada para testar os endpoints HTTP das aplicações.
- **Git/GitHub**: controle de versão e entrega do projeto.

---

## 4. Estrutura da arquitetura

O fluxo da aplicação segue a seguinte ordem:

```text
Postman → Publisher → RabbitMQ → Consumer / sistema-pedidos → PostgreSQL
```

### Explicação do fluxo

1. O usuário envia uma requisição pelo Postman para o serviço publisher.
2. O publisher recebe os dados do pedido.
3. O publisher publica uma mensagem na fila configurada no RabbitMQ.
4. O RabbitMQ armazena a mensagem na fila `MY_TEST_QUEUE`.
5. O consumer escuta essa fila por meio do `@RabbitListener`.
6. Quando uma mensagem chega, o consumer processa os dados do pedido.
7. O pedido é salvo no banco de dados PostgreSQL.
8. O pedido pode ser consultado posteriormente pela API do sistema de pedidos ou diretamente pelo pgAdmin.

---

## 5. Configuração do RabbitMQ

O RabbitMQ foi executado em container Docker, com duas portas principais:

```text
5672  → porta usada pela aplicação Spring para comunicação com o RabbitMQ
8090  → porta usada para acessar o painel visual do RabbitMQ no navegador
```

A configuração correta nos arquivos `application.properties` dos dois serviços foi:

```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
spring.rabbitmq.port=5672
```

Durante o desenvolvimento, foi identificado que a aplicação estava tentando se conectar pela porta `8090`, o que gerava erro de timeout. Esse problema foi corrigido ao trocar a porta para `5672`, que é a porta correta de comunicação entre o Spring Boot e o RabbitMQ.

---

## 6. Fila utilizada

A fila utilizada na integração foi:

```text
MY_TEST_QUEUE
```

Ela é responsável por armazenar as mensagens de pedidos enviadas pelo publisher até que o consumer esteja disponível para processá-las.

A configuração da fila foi feita nos arquivos de propriedades:

```properties
rabbitmq.queuename=MY_TEST_QUEUE
```

---

## 7. Serviço Publisher

O serviço publisher é responsável por iniciar o fluxo de mensageria.

Ele expõe um endpoint HTTP que recebe os dados de um pedido. Após receber a requisição, o serviço não salva diretamente o pedido no banco. Em vez disso, ele envia os dados para o RabbitMQ.

Exemplo de endpoint utilizado no Postman:

```http
POST http://localhost:8081/publish/order
```

Exemplo de corpo da requisição:

```json
{
  "productName": "Fogao Teste Offline",
  "price": 1500.0,
  "quantity": 1,
  "user": {
    "id": 1
  }
}
```

Nesse caso, o `id` do usuário precisa ser de um usuário existente no banco de dados e com perfil adequado para realizar pedidos, como o perfil `RETAIL`.

---

## 8. Serviço Consumer / sistema-pedidos

O serviço consumer, chamado de `sistema-pedidos`, é responsável por receber as mensagens da fila e salvar os pedidos no banco.

Ele roda na porta:

```text
8082
```

Os principais endpoints disponíveis no controller de pedidos são:

```http
GET    http://localhost:8082/orders
GET    http://localhost:8082/orders/{id}
POST   http://localhost:8082/orders
PUT    http://localhost:8082/orders
DELETE http://localhost:8082/orders/{id}
```

Para consultar todos os pedidos salvos, foi utilizado:

```http
GET http://localhost:8082/orders
```

O consumer também possui um listener do RabbitMQ, responsável por escutar a fila configurada e processar automaticamente as mensagens recebidas.

---

## 9. Banco de dados

O banco utilizado foi o PostgreSQL. O acesso visual ao banco foi feito pelo pgAdmin, disponível em:

```text
http://localhost:5050
```

As principais tabelas identificadas foram:

```text
tb_user
tb_role
tb_user_role
tb_order
```

A tabela `tb_user` armazena os usuários do sistema. A tabela `tb_role` armazena os perfis ou autoridades, como:

```text
RETAIL
PRODUCER
```

A tabela `tb_user_role` faz a ligação entre usuários e perfis. A tabela `tb_order` armazena os pedidos.

Para visualizar usuários e seus perfis, foi utilizada a seguinte consulta SQL:

```sql
SELECT 
    u.id AS user_id,
    u.name AS nome,
    u.email,
    r.authority AS profissao
FROM tb_user u
JOIN tb_user_role ur ON u.id = ur.user_id
JOIN tb_role r ON r.id = ur.role_id;
```

Para criar pedidos corretamente, foi necessário utilizar um usuário com autoridade `RETAIL`, pois esse perfil representa o lojista/cliente que realiza pedidos.

---

## 10. Demonstração da troca de mensagens

A troca de mensagens foi demonstrada por meio do seguinte fluxo:

1. O publisher foi iniciado na porta `8081`.
2. O consumer foi iniciado na porta `8082`.
3. O RabbitMQ foi mantido ativo no Docker.
4. Uma requisição foi enviada pelo Postman para o publisher.
5. O publisher publicou a mensagem na fila `MY_TEST_QUEUE`.
6. O consumer recebeu a mensagem automaticamente.
7. O pedido foi salvo no banco de dados.
8. O pedido pôde ser consultado pelo endpoint `GET /orders`.

É importante destacar que o Postman não representa a mensageria. Ele apenas simula o cliente enviando uma requisição HTTP. A mensageria acontece entre o publisher, o RabbitMQ e o consumer.

O fluxo real de mensageria é:

```text
Publisher → RabbitMQ → Consumer
```

---

## 11. Demonstração de assincronicidade

A assincronicidade foi demonstrada desligando temporariamente o serviço consumer.

O teste foi feito da seguinte forma:

1. O RabbitMQ permaneceu ligado.
2. O PostgreSQL permaneceu ligado.
3. O publisher permaneceu ligado.
4. O consumer `sistema-pedidos` foi desligado.
5. Foi enviado um pedido pelo Postman para o publisher.
6. O pedido foi publicado na fila do RabbitMQ.
7. Como o consumer estava desligado, a mensagem permaneceu na fila com status `Ready`.
8. Ao ligar novamente o consumer, ele consumiu automaticamente a mensagem pendente.
9. A mensagem saiu da fila e o pedido foi salvo no banco de dados.

Esse teste demonstra a comunicação assíncrona porque o publisher conseguiu enviar o pedido mesmo sem o consumer estar online no momento do envio.

---

## 12. Evidência esperada no RabbitMQ

Durante o teste com o consumer desligado, no painel do RabbitMQ, a fila `MY_TEST_QUEUE` deve mostrar algo semelhante a:

```text
Ready: 1
Consumers: 0
```

Isso significa que existe uma mensagem aguardando processamento e nenhum consumidor ativo naquele momento.

Após religar o consumer, a fila deve voltar para:

```text
Ready: 0
```

Isso demonstra que a mensagem foi consumida corretamente.

---

## 13. Problemas encontrados e correções

Durante o desenvolvimento e os testes, alguns problemas foram encontrados.

### 13.1 Porta incorreta do RabbitMQ

Inicialmente, o Spring estava tentando se conectar ao RabbitMQ pela porta `8090`. Essa porta é usada apenas para acessar o painel web do RabbitMQ.

A correção foi alterar a configuração para:

```properties
spring.rabbitmq.port=5672
```

### 13.2 Usuário inexistente

Durante um teste, foi enviado um pedido com:

```json
"user": {
  "id": 7
}
```

O sistema retornou erro porque o usuário com `id = 7` não existia no banco de dados.

A correção foi consultar os usuários existentes no pgAdmin e utilizar um usuário com perfil `RETAIL`.

### 13.3 Mensagem presa na fila

Como o consumer tentou processar uma mensagem com usuário inexistente, ocorreu erro durante o processamento. A mensagem permaneceu na fila.

A correção foi limpar a fila pelo painel do RabbitMQ utilizando a opção `Purge Messages` e reenviar uma mensagem válida.

### 13.4 Arquivos locais do Docker no Git

Durante o envio para o GitHub, arquivos da pasta `.data` do Docker/PostgreSQL foram adicionados por engano.

A correção foi criar um arquivo `.gitignore` com as seguintes regras:

```gitignore
**/target/
**/.idea/
**/.data/
**/postgre_docker/.data/
```

Depois, os arquivos da `.data` foram removidos do controle de versão sem apagar os arquivos locais.

---

## 14. Como executar a demonstração

Para apresentar o funcionamento ao professor, recomenda-se seguir este roteiro:

### Parte 1 — Mensageria com tudo online

1. Iniciar Docker com RabbitMQ e PostgreSQL.
2. Iniciar o publisher.
3. Iniciar o consumer.
4. Enviar um pedido pelo Postman para:

```http
POST http://localhost:8081/publish/order
```

5. Consultar os pedidos em:

```http
GET http://localhost:8082/orders
```

6. Mostrar que o pedido foi salvo no banco.

### Parte 2 — Assincronicidade com consumer offline

1. Parar o consumer.
2. Manter o publisher, RabbitMQ e PostgreSQL ligados.
3. Enviar um pedido pelo Postman.
4. Mostrar no RabbitMQ que a mensagem ficou em `Ready`.
5. Ligar o consumer novamente.
6. Mostrar que a fila ficou vazia.
7. Consultar os pedidos e mostrar que o novo pedido foi salvo.

---

## 15. Conclusão

A integração implementada demonstra corretamente o uso de mensageria com RabbitMQ em uma arquitetura com producer e consumer.

O publisher recebe a requisição HTTP e publica uma mensagem na fila. O consumer escuta essa fila, processa a mensagem e salva os dados no banco de dados. A comunicação é assíncrona, pois o publisher não depende do consumer estar online no momento do envio.

A demonstração com o consumer desligado comprova que as mensagens não são perdidas. Elas permanecem armazenadas no RabbitMQ até que o consumer volte a funcionar. Assim, o projeto evidencia uma integração desacoplada, mais resiliente e adequada para sistemas distribuídos.
