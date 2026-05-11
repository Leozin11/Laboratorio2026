package com.lab.sistema_pedidos.repositories;

import com.lab.sistema_pedidos.entities.Role;
import com.lab.sistema_pedidos.entities.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByAuthority(RoleEnum authority);

}
