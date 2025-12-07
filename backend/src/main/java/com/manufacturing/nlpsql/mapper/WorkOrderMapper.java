package com.manufacturing.nlpsql.mapper;

import com.manufacturing.nlpsql.model.WorkOrder;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WorkOrderMapper {
    List<WorkOrder> findAll();
    WorkOrder findById(Long id);
    void insert(WorkOrder workOrder);
    void update(WorkOrder workOrder);
    void delete(Long id);
}
