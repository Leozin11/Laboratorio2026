package com.lab.sistema_pedidos.entities.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum OrderStatus {
    PENDING("pending"), // Purchased and Payed, awaiting to produce
    PRODUCING("producing"),
    SENDING("sending"),
    DELIVERED("delivered");

    private final String value;

    @JsonCreator
    public static OrderStatus getFromOrderValue(String value) {
        return Arrays.stream(OrderStatus.values())
                .filter(order -> order.getValue().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid role name: " + value));
    }

}
