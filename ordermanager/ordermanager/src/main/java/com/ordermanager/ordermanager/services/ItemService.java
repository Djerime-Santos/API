package com.ordermanager.ordermanager.services;

import com.ordermanager.ordermanager.exceptions.NotFoundException;
import com.ordermanager.ordermanager.model.Item;
import com.ordermanager.ordermanager.model.StockMovement;
import com.ordermanager.ordermanager.repositories.ItemRepository;
import com.ordermanager.ordermanager.repositories.StockMovementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ItemService {

    private static final Logger log = LoggerFactory.getLogger(ItemService.class);

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Autowired
    private OrderService orderService;

    public List<Item> getAllItems() {
        return itemRepository.findByActiveTrue();
    }

    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item não encontrado com o ID: " + id));
    }

    @Transactional
    public Item saveItem(Item item) {
        if (item.getStockQuantity() == null || item.getStockQuantity() < 0) {
            throw new IllegalArgumentException("A quantidade inicial de stock não pode ser negativa.");
        }
        Item savedItem = itemRepository.save(item);
        log.info("Item criado com sucesso: {} (ID: {})", savedItem.getName(), savedItem.getId());
        return savedItem;
    }

    @Transactional
    public Item updateItem(Long id, Item itemDetails) {
        Item item = getItemById(id);
        item.setName(itemDetails.getName());
        if (itemDetails.getStockQuantity() != null && itemDetails.getStockQuantity() >= 0) {
            item.setStockQuantity(itemDetails.getStockQuantity());
        }
        Item updated = itemRepository.save(item);
        log.info("Item atualizado: ID {}", id);
        return updated;
    }

    @Transactional
    public void deleteItem(Long id) {
        Item item = itemRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Items não foi encontrado com o id: " + id));
        item.setActive(false);
        itemRepository.save(item);
        log.info("Item desativado: ID {}", id);
    }

    @Transactional
    public Item addStock(Long itemId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("A quantidade a adicionar deve ser maior que zero.");
        }

        Item item = getItemById(itemId);
        item.setStockQuantity(item.getStockQuantity() + quantity);
        Item updatedItem = itemRepository.save(item);

        StockMovement stockMovement = new StockMovement(updatedItem, quantity);
        stockMovement.setType("IN");
        stockMovement = stockMovementRepository.save(stockMovement);

        log.info("Novo StockMovement criado: ID {}, Item ID {}, Quantidade: {}", stockMovement.getId(), itemId, quantity);

        orderService.processPendingOrders(updatedItem, stockMovement);

        return updatedItem;
    }
}