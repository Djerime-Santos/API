package com.ordermanager.ordermanager.services;

import com.ordermanager.ordermanager.model.Item;
import com.ordermanager.ordermanager.model.Order;
import com.ordermanager.ordermanager.model.StockMovement;
import com.ordermanager.ordermanager.model.User;
import com.ordermanager.ordermanager.repositories.ItemRepository;
import com.ordermanager.ordermanager.repositories.OrderRepository;
import com.ordermanager.ordermanager.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order createOrder(Long userId, Long itemId, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));

        Order order = new Order();
        order.setUser(user);
        order.setItem(item);
        order.setQuantity(quantity);

        if (item.getStockQuantity() >= quantity) {
            item.setStockQuantity(item.getStockQuantity() - quantity);
            itemRepository.save(item);
            order.setStatus("COMPLETED");
            log.info("Encomenda ID criada e completada imediatamente com o stock existente.");
            sendCompletionEmail(user, order);
        } else {
            order.setStatus("PENDING");
            log.warn("Stock insuficiente. Encomenda ID colocada como PENDING.");
        }

        return orderRepository.save(order);
    }

    public void processPendingOrders(Item item, StockMovement stockMovement) {
        List<Order> pendingOrders = orderRepository.findByItemAndStatus(item, "PENDING");

        for (Order order : pendingOrders) {
            if (item.getStockQuantity() >= order.getQuantity()) {

                item.setStockQuantity(item.getStockQuantity() - order.getQuantity());
                itemRepository.save(item);

                order.setStatus("COMPLETED");

                order.getStockMovements().add(stockMovement);
                stockMovement.getOrders().add(order);

                orderRepository.save(order);
                log.info("Encomenda PENDING ID {} resolvida automaticamente pelo StockMovement ID {}", order.getId(), stockMovement.getId());

                sendCompletionEmail(order.getUser(), order);
            }
        }
    }

    private void sendCompletionEmail(User user, Order order) {
        try {
            if (mailSender != null) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(user.getEmail());
                message.setSubject("Encomenda Concluída - ID: " + order.getId());
                message.setText("Olá " + user.getName() + ",\n\nA sua encomenda do item '" + order.getItem().getName() + "' (Quantidade: " + order.getQuantity() + ") foi concluída com sucesso!");
                mailSender.send(message);
                log.info("Email de conclusão enviado com sucesso para: {}", user.getEmail());
            } else {
                log.info("[Simulação] Email enviado para {} - Encomenda {} concluída.", user.getEmail(), order.getId());
            }
        } catch (Exception e) {
            log.error("Erro ao enviar email para {}: {}", user.getEmail(), e.getMessage());
        }
    }

    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Encomenda não encontrada com o ID: " + id);
        }
        orderRepository.deleteById(id);
        log.info("Encomenda apagada: ID {}", id);
    }
}