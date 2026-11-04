package com.ordermanager.ordermanager.controllers;

import com.ordermanager.ordermanager.dtos.OrderRequestDTO;
import com.ordermanager.ordermanager.model.Order;
import com.ordermanager.ordermanager.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequestDTO request) {
        return ResponseEntity.created(URI.create("/api/orders/"))
                .body(orderService.createOrder(
                request.getUserId(),
                request.getItemId(),
                request.getQuantity()
        ));
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
