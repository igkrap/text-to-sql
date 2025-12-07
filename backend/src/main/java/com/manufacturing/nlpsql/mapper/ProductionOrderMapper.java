package com.manufacturing.nlpsql.mapper;

import com.manufacturing.nlpsql.model.ProductionOrder;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductionOrderMapper {
    List<ProductionOrder> findAll();
    ProductionOrder findById(Long id);
    void insert(ProductionOrder order);
    void update(ProductionOrder order);
    void delete(Long id);
}
