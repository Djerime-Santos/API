package com.ordermanager.ordermanager.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@Table(name = "order_allocations")
public class OrderAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "allocations"})
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_movement_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "allocations"})
    private StockMovement stockMovement;

    @Column(nullable = false)
    private int allocatedQuantity;

    public OrderAllocation() {}

    public OrderAllocation(Order order, StockMovement stockMovement, int allocatedQuantity) {
        this.order = order;
        this.stockMovement = stockMovement;
        this.allocatedQuantity = allocatedQuantity;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public StockMovement getStockMovement() { return stockMovement; }
    public void setStockMovement(StockMovement stockMovement) { this.stockMovement = stockMovement; }

    public int getAllocatedQuantity() { return allocatedQuantity; }
    public void setAllocatedQuantity(int allocatedQuantity) { this.allocatedQuantity = allocatedQuantity; }
}