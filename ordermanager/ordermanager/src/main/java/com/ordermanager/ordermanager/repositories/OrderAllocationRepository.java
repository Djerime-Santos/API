package com.ordermanager.ordermanager.repositories;

import com.ordermanager.ordermanager.model.OrderAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderAllocationRepository extends JpaRepository<OrderAllocation, Long> {
}
