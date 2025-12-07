package com.manufacturing.nlpsql.mapper;

import com.manufacturing.nlpsql.model.Inventory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InventoryMapper {
    List<Inventory> findAll();
    Inventory findById(Long id);
    void insert(Inventory inventory);
    void update(Inventory inventory);
    void delete(Long id);
}
