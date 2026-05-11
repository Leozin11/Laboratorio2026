package com.lab.sistema_pedidos.services;


import com.lab.sistema_pedidos.dto.RoleDTO;
import com.lab.sistema_pedidos.dto.UserDTO;
import com.lab.sistema_pedidos.entities.Role;
import com.lab.sistema_pedidos.entities.User;
import com.lab.sistema_pedidos.entities.enums.RoleEnum;
import com.lab.sistema_pedidos.repositories.RoleRepository;
import com.lab.sistema_pedidos.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        Optional<User> entity = repository.findById(id);
        User user = entity.orElseThrow(() -> new EntityNotFoundException("User not found"));
        return new UserDTO(user);
    }

    @Transactional
    public UserDTO insert(UserDTO dto) {
        User entity = new User();
        copyDtoToEntity(dto, entity);

        entity.getRoles().clear();
        Role role = roleRepository.findByAuthority(RoleEnum.RETAIL);
        entity.getRoles().add(role);
        entity.setPassword(dto.getPassword());

        entity = repository.save(entity);
        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO update(Long id, UserDTO dto) {
        try {
            User entity = repository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            entity = repository.save(entity);
            return new UserDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Id not found - " + id);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Entegrity Violation");
        }
    }

    private void copyDtoToEntity(UserDTO dto, User entity) {
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());

        entity.getRoles().clear();
        for (RoleDTO roleDTO : dto.getRoles()) {
            Role roleEntity = roleRepository.findByAuthority(roleDTO.getAuthority());
            entity.getRoles().add(roleEntity);
        }
    }


}
