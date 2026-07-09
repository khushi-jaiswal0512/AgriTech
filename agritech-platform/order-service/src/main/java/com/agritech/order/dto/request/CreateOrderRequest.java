package com.agritech.order.dto.request;

import com.agritech.order.enums.BuyerType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {

    @NotNull(message = "Farmer ID is required")
    private Long farmerId;

    @NotNull(message = "Buyer type is required")
    private BuyerType buyerType;

    @NotNull(message = "Shipping address ID is required")
    private Long shippingAddressId;

    private String notes;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemRequest> items;
}
