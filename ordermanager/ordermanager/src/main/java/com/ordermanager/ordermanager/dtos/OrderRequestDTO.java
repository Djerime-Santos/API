package com.ordermanager.ordermanager.dtos;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class OrderRequestDTO {

    @NotNull(message = "O ID do utilizador é obrigatório")
    private Long userId;

    @NotNull(message = "O ID do item é obrigatório")
    private Long itemId;

    @NotNull(message = "A quantidade é obrigatória")
    @Min(value = 1, message = "A quantidade mínima de encomenda é 1")
    private Integer quantity;

    public OrderRequestDTO() {}

    public OrderRequestDTO(Long userId, Long itemId, Integer quantity) {
        this.userId = userId;
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}