package com.ordermanager.ordermanager.services;

import com.ordermanager.ordermanager.model.Item;
import com.ordermanager.ordermanager.model.StockMovement;
import com.ordermanager.ordermanager.repositories.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    private static final Logger log = LoggerFactory.getLogger(ItemService.class);

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private StockMovementService stockMovementService;

    @Autowired
    private OrderService orderService;

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Item saveItem(Item item) {
        Item savedItem = itemRepository.save(item);
        log.info("Item criado com sucesso: {} (ID: {})", savedItem.getName(), savedItem.getId());
        return savedItem;
    }

    public Item updateItem(Long id, Item itemDetails) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item não encontrado com o ID: " + id));
        item.setName(itemDetails.getName());
        item.setStockQuantity(itemDetails.getStockQuantity());
        Item updated = itemRepository.save(item);
        log.info("Item atualizado: ID {}", id);
        return updated;
    }

    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new RuntimeException("Item não encontrado com o ID: " + id);
        }
        itemRepository.deleteById(id);
        log.info("Item apagado: ID {}", id);
    }

    public Item addStock(Long itemId, int quantity) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));

        item.setStockQuantity(item.getStockQuantity() + quantity);
        Item updatedItem = itemRepository.save(item);

        StockMovement stockMovement = stockMovementService.createStockMovement(updatedItem, quantity);
        log.info("Novo StockMovement criado: ID {}, Item ID {}, Quantidade: {}", stockMovement.getId(), itemId, quantity);

        orderService.processPendingOrders(updatedItem, stockMovement);

        return updatedItem;
    }
}
