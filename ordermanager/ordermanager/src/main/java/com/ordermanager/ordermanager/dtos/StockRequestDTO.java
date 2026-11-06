package com.ordermanager.ordermanager.dtos;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class StockRequestDTO {

    @NotNull(message = "A quantidade é obrigatória")
    @Min(value = 1, message = "A quantidade a adicionar deve ser maior que zero")
    private Integer quantity;

    public StockRequestDTO() {}

    public StockRequestDTO(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}