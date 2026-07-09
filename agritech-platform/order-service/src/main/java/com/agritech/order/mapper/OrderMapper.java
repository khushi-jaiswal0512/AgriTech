package com.agritech.order.mapper;

import com.agritech.order.dto.response.OrderItemResponse;
import com.agritech.order.dto.response.OrderResponse;
import com.agritech.order.dto.response.OrderStatusHistoryResponse;
import com.agritech.order.entity.Order;
import com.agritech.order.entity.OrderItem;
import com.agritech.order.entity.OrderStatusHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderResponse toResponse(Order order);
    
    @Mapping(target = "orderId", source = "order.id")
    OrderStatusHistoryResponse toHistoryResponse(OrderStatusHistory history);
}
