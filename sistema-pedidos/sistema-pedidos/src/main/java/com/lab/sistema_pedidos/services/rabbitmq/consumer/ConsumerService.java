package com.lab.sistema_pedidos.services.rabbitmq.consumer;

import com.lab.sistema_pedidos.dto.OrderDTO;
import com.lab.sistema_pedidos.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class ConsumerService {

    @Autowired
    private OrderService orderservice;

    private final ObjectMapper objectMapper;

    @Transactional
    @RabbitListener(queues = "${rabbitmq.queuename}")
    public void receiveMessage(Message message){
        String texto = new String(message.getBody(), StandardCharsets.UTF_8);
        OrderDTO order = objectMapper.readValue(texto, OrderDTO.class);
        OrderDTO oderInserted = orderservice.insert(order);
        System.out.printf("Order id [%s] inserted: [%s]", oderInserted.getId(), oderInserted.toString());
    }

}
