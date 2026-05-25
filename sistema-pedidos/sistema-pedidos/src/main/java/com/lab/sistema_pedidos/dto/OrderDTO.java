package com.lab.sistema_pedidos.dto;

import com.lab.sistema_pedidos.entities.Order;
import com.lab.sistema_pedidos.entities.enums.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderDTO {

    private Long id;
    private String productName;
    private Integer quantity;
    private Double price;
    private OrderStatus status;
    private UserOrderDTO user = new UserOrderDTO();

    public OrderDTO(Long id, String productName, Integer quantity, Double price, OrderStatus status) {
        this.id = id;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.status = status;
    }

    public OrderDTO(Order entity) {
        id = entity.getId();
        productName = entity.getProductName();
        quantity = entity.getQuantity();
        price = entity.getPrice();
        status = entity.getStatus();
        user = new UserOrderDTO(entity.getUser());
    }
}
