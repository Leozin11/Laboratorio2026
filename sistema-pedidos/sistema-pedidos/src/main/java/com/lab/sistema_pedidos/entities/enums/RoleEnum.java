package com.lab.sistema_pedidos.entities.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum RoleEnum {
    RETAIL("retail"),
    PRODUCER("producer");

    private final String name;

    @JsonValue
    public String getName() {
        return name;
    }

    @JsonCreator
    public static RoleEnum getFromCodeName(String name) {
        return Arrays.stream(RoleEnum.values())
                .filter(role -> role.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid role name: " + name));
    }

}
