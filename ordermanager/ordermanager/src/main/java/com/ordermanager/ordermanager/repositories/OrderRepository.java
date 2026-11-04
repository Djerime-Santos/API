package com.ordermanager.ordermanager.repositories;

import com.ordermanager.ordermanager.model.Item;
import com.ordermanager.ordermanager.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {

    List<Order> findByItemAndStatus(Item item, String status);
}
