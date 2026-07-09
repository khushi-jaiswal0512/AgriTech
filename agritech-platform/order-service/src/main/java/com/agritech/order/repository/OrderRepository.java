package com.agritech.order.repository;

import com.agritech.order.entity.Order;
import com.agritech.order.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByBuyerId(Long buyerId, Pageable pageable);
    Page<Order> findByFarmerId(Long farmerId, Pageable pageable);
    Page<Order> findByFarmerIdAndOrderStatus(Long farmerId, OrderStatus orderStatus, Pageable pageable);
    Optional<Order> findByOrderNumber(String orderNumber);
}
