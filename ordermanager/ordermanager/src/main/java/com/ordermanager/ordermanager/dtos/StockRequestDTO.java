package com.ordermanager.ordermanager.dtos;

public class StockRequestDTO {
    private Integer quantity;

    public StockRequestDTO() {}

    public StockRequestDTO(Integer quantity) {
        this.quantity = quantity;
    }
    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
