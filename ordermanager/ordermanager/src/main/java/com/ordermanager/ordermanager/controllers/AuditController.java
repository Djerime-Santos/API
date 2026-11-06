package com.ordermanager.ordermanager.controllers;

import com.ordermanager.ordermanager.model.StockMovement;
import com.ordermanager.ordermanager.services.AuditService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditController {
    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping("/items/{itemId}/movements")
    public ResponseEntity<List<StockMovement>> getItemAuditTrail(@PathVariable Long itemId) {
        List<StockMovement> history = auditService.getItemMovementHistory(itemId);
        return ResponseEntity.ok(history);
    }
}
