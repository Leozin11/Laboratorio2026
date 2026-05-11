package com.lab.sistema_pedidos.dto;

import com.lab.sistema_pedidos.entities.Order;
import com.lab.sistema_pedidos.entities.enums.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderStatusDTO {

    private Long id;
    private OrderStatus status;

    public OrderStatusDTO(Long id, OrderStatus status) {
        this.id = id;
        this.status = status;
    }

    public OrderStatusDTO(Order entity) {
        id = entity.getId();
        status = entity.getStatus();
    }
}
