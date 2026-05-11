package com.lab.sistema_pedidos.services;

import com.lab.sistema_pedidos.dto.OrderDTO;
import com.lab.sistema_pedidos.dto.OrderStatusDTO;
import com.lab.sistema_pedidos.entities.Order;
import com.lab.sistema_pedidos.entities.User;
import com.lab.sistema_pedidos.repositories.OrderRepository;
import com.lab.sistema_pedidos.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.lab.sistema_pedidos.entities.enums.OrderStatus.PENDING;

@Service
public class OrderService {

    @Autowired
    private OrderRepository repository;

    @Autowired
    private UserRepository userRepository;

    public OrderDTO findById(Long id) {
        Optional<Order> entity = repository.findById(id);
        Order order = entity.orElseThrow(() -> new EntityNotFoundException("Order not found"));
        return new OrderDTO(order);
    }

    public List<OrderDTO> findAll() {
        return repository.findAll().stream().map(OrderDTO::new).toList();
    }

    public OrderDTO insert(OrderDTO orderDTO) {
        Order entity = new Order();
        copyDtoToEntity(orderDTO, entity);

        entity.setStatus(PENDING);
        return new OrderDTO(repository.save(entity));
    }

    public OrderDTO updateStatus(OrderStatusDTO orderStatusDTO) {
        Optional<Order> entity = repository.findById(orderStatusDTO.getId());
        Order order = entity.orElseThrow(() -> new EntityNotFoundException("Order not found!"));

        order.setStatus(orderStatusDTO.getStatus());

        return new OrderDTO(repository.save(order));
    }

    public void deleteOrder(Long id) {
        repository.deleteById(id);
    }


    private void copyDtoToEntity(OrderDTO orderDTO, Order entity) {
        entity.setProductName(orderDTO.getProductName());
        entity.setQuantity(orderDTO.getQuantity());
        entity.setPrice(orderDTO.getPrice());

        User userEntity = userRepository.findById(orderDTO.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        entity.setUser(userEntity);

    }

}
