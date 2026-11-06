package com.ordermanager.ordermanager.event;

import com.ordermanager.ordermanager.model.Order;

public class OrderCompletedEvent {

    private final Order order;

    public OrderCompletedEvent(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }
}
