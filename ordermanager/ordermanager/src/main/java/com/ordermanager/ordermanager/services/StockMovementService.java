package com.ordermanager.ordermanager.services;

import com.ordermanager.ordermanager.model.Item;
import com.ordermanager.ordermanager.model.StockMovement;
import com.ordermanager.ordermanager.repositories.StockMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockMovementService {

    @Autowired
    private StockMovementRepository stockMovementRepository;

    public List<StockMovement> getAllStockMovements() {
        return stockMovementRepository.findAll();
    }

    public StockMovement createStockMovement(Item item, int quantity) {
        StockMovement movement = new StockMovement(item, quantity);
        return stockMovementRepository.save(movement);
    }
}
