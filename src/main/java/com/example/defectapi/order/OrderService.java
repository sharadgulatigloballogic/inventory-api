package com.example.defectapi.order;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class OrderService {
    private final Map<Long, Order> orders = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1000);

    public OrderService() {
        Order seeded = new Order(1001L, 7L, "Maya Chen", "maya.chen@example.com",
                "BK-100", "Spring in Action", 1, new BigDecimal("49.99"),
                new BigDecimal("10.00"), "25 Front Street, Toronto, ON", "NEW", Instant.now());
        orders.put(seeded.getId(), seeded);
    }

    public List<Order> findAll() {
        return orders.values().stream()
                .sorted(Comparator.comparing(Order::getId))
                .toList();
    }

    public Order findById(Long id) {
        // DEF-101: missing-order failure path. Read logs/DEF-101-missing-order.log for the API contract.
        return orders.entrySet().stream()
                .filter(entry -> entry.getKey().equals(id))
                .map(Map.Entry::getValue)
                .findFirst()
                .get();
    }

    public Order create(CreateOrderRequest request) {
        BigDecimal subtotal = request.getUnitPrice()
                .multiply(BigDecimal.valueOf(request.getQuantity()));

        // DEF-102: order creation fails for the omitted optional-field scenario in the attached log.
        BigDecimal discount = subtotal
                .multiply(request.getDiscountPercent())
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.subtract(discount);

        Order order = new Order(sequence.incrementAndGet(), request.getCustomerId(),
                request.getCustomerName(), request.getEmail(), request.getProductCode(),
                request.getProductName(), request.getQuantity(), total,
                request.getDiscountPercent(), request.getShippingAddress(),
                request.getStatus() == null ? "NEW" : request.getStatus(), Instant.now());
        orders.put(order.getId(), order);
        return order;
    }
}
