package com.ordermanager.ordermanager.services;

import com.ordermanager.ordermanager.model.StockMovement;
import com.ordermanager.ordermanager.repositories.StockMovementRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditService {
    private final StockMovementRepository stockMovementRepository;

    public AuditService(StockMovementRepository stockMovementRepository) {
        this.stockMovementRepository = stockMovementRepository;
    }

    public List<StockMovement> getItemMovementHistory(Long itemId) {
        return stockMovementRepository.findByItemId(itemId);
    }
}
