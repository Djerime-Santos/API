package com.ordermanager.ordermanager.repositories;

import com.ordermanager.ordermanager.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item,Long> {
    List<Item> findByActiveTrue();
}
