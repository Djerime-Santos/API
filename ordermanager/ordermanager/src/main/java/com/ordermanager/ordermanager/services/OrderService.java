package com.ordermanager.ordermanager.services;

import com.ordermanager.ordermanager.event.OrderCompletedEvent;
import com.ordermanager.ordermanager.exceptions.NotFoundException;
import com.ordermanager.ordermanager.model.*;
import com.ordermanager.ordermanager.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Autowired
    private OrderAllocationRepository orderAllocationRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Encomenda não encontrada com o ID: " + id));
    }

    @Transactional
    public Order createOrder(Long userId, Long itemId, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Utilizador não encontrado"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item não encontrado"));

        Order order = new Order();
        order.setUser(user);
        order.setItem(item);
        order.setRequestedQuantity(quantity);

        int availableStock = item.getStockQuantity();
        int fulfilled = Math.min(quantity, availableStock);
        order.setFulfilledQuantity(fulfilled);

        if (fulfilled == quantity) {
            order.setStatus("COMPLETED");
        } else if (fulfilled > 0) {
            order.setStatus("PARTIAL");
        } else {
            order.setStatus("PENDING");
        }

        order = orderRepository.save(order);

        if (fulfilled > 0) {
            item.setStockQuantity(availableStock - fulfilled);
            itemRepository.save(item);

            StockMovement movement = new StockMovement(item, -fulfilled);
            movement.setType("OUT");
            movement = stockMovementRepository.save(movement);

            OrderAllocation allocation = new OrderAllocation(order, movement, fulfilled);
            orderAllocationRepository.save(allocation);
            order.getAllocations().add(allocation);

            log.info("Encomenda ID {} criada com satisfação de {}/{} unidades.", order.getId(), fulfilled, quantity);

            eventPublisher.publishEvent(new OrderCompletedEvent(order));
        } else {
            log.warn("Stock insuficiente. Encomenda ID {} colocada como PENDING.", order.getId());
        }

        return order;
    }

    @Transactional
    public void processPendingOrders(Item item, StockMovement inStockMovement) {
        List<Order> pendingOrders = orderRepository.findByItemAndStatusInOrderByCreationDateAsc(item, Arrays.asList("PENDING", "PARTIAL"));
        int currentAvailableStock = item.getStockQuantity();

        for (Order order : pendingOrders) {
            int needed = order.getRequestedQuantity() - order.getFulfilledQuantity();
            if (needed <= 0) continue;

            if (currentAvailableStock >= needed) {
                currentAvailableStock -= needed;
                order.setFulfilledQuantity(order.getRequestedQuantity());
                order.setStatus("COMPLETED");

                item.setStockQuantity(currentAvailableStock);
                itemRepository.save(item);

                StockMovement outMovement = new StockMovement(item, -needed);
                outMovement.setType("OUT");
                outMovement = stockMovementRepository.save(outMovement);

                OrderAllocation allocation = new OrderAllocation(order, outMovement, needed);
                orderAllocationRepository.save(allocation);
                order.getAllocations().add(allocation);

                orderRepository.save(order);
                log.info("Encomenda ID {} resolvida na totalidade via novo StockMovement OUT ID {}", order.getId(), outMovement.getId());

                eventPublisher.publishEvent(new OrderCompletedEvent(order));
            } else if (currentAvailableStock > 0) {
                int canFulfill = currentAvailableStock;
                order.setFulfilledQuantity(order.getFulfilledQuantity() + canFulfill);
                order.setStatus("PARTIAL");

                currentAvailableStock = 0;
                item.setStockQuantity(0);
                itemRepository.save(item);

                StockMovement outMovement = new StockMovement(item, -canFulfill);
                outMovement.setType("OUT");
                outMovement = stockMovementRepository.save(outMovement);

                OrderAllocation allocation = new OrderAllocation(order, outMovement, canFulfill);
                orderAllocationRepository.save(allocation);
                order.getAllocations().add(allocation);

                orderRepository.save(order);

                log.info("Encomenda ID {} parcialmente atendida com {} unidades via StockMovement OUT ID {}.", order.getId(), canFulfill, outMovement.getId());
                break;
            } else {
                break;
            }
        }
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = getOrderById(id);
        orderRepository.delete(order);
        log.info("Encomenda apagada: ID {}", id);
    }
}