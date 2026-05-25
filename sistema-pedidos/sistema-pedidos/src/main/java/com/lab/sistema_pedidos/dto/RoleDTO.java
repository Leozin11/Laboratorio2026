package com.lab.sistema_pedidos.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.lab.sistema_pedidos.entities.Role;
import com.lab.sistema_pedidos.entities.enums.RoleEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@JsonPropertyOrder({"id", "authority"})
public class RoleDTO implements Serializable {

    private Long id;
    private RoleEnum authority;

    public RoleDTO(Long id, RoleEnum authority) {
        this.id = id;
        this.authority = authority;
    }

    public RoleDTO(Role entity) {
        id = entity.getId();
        authority = entity.getAuthority();
    }

}
