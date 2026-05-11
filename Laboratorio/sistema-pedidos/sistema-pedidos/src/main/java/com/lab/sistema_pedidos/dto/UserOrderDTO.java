package com.lab.sistema_pedidos.dto;

import com.lab.sistema_pedidos.entities.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public class UserOrderDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;

    public UserOrderDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public UserOrderDTO(User entity) {
        id = entity.getId();
        name = entity.getName();
    }
}
