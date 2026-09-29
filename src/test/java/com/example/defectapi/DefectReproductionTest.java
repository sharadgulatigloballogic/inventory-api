package com.example.defectapi;

import com.example.defectapi.inventory.InventoryService;
import com.example.defectapi.order.CreateOrderRequest;
import com.example.defectapi.order.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DefectReproductionTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void lookupScenarioShouldRemainStable() {
        // DEF-101 reproduction; see logs/DEF-101-missing-order.log for expected response semantics.
        OrderService service = new OrderService();

        assertDoesNotThrow(() -> service.findById(9999L));
    }

    @Test
    void createScenarioShouldRemainStable() {
        // DEF-102 reproduction; the omitted input and expected value are defined in the attachment.
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerId(42L);
        request.setCustomerName("Ava Patel");
        request.setEmail("ava.patel@example.com");
        request.setProductCode("BK-100");
        request.setProductName("Spring in Action");
        request.setQuantity(2);
        request.setUnitPrice(new BigDecimal("49.99"));
        request.setShippingAddress("100 King Street, Toronto, ON");

        // Order has no distinct `total` field; the computed total is stored in unitPrice.
        // No discountPercent supplied -> treated as 0% -> total equals subtotal (99.98).
        assertEquals(new BigDecimal("99.98"), new OrderService().create(request).getUnitPrice());
    }

    @Test
    void createScenarioWithSuppliedDiscountShouldApplyRoundedDiscount() {
        // quantity=2, unitPrice=49.99 -> subtotal=99.98; discountPercent=10 -> raw discount=9.998,
        // which HALF_UP-rounds to 10.00 -> total (stored in Order.unitPrice) = 89.98.
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerId(42L);
        request.setCustomerName("Ava Patel");
        request.setEmail("ava.patel@example.com");
        request.setProductCode("BK-100");
        request.setProductName("Spring in Action");
        request.setQuantity(2);
        request.setUnitPrice(new BigDecimal("49.99"));
        request.setDiscountPercent(new BigDecimal("10"));
        request.setShippingAddress("100 King Street, Toronto, ON");

        assertEquals(new BigDecimal("89.98"), new OrderService().create(request).getUnitPrice());
    }

    @Test
    void createOrderEndpointShouldReturn201WhenDiscountPercentIsOmitted() throws Exception {
        // Ticket reproduction payload (VDLC-2344): omitting discountPercent must not throw a 500.
        String payload = """
                {
                  "customerId": 42,
                  "customerName": "Ava Patel",
                  "email": "ava.patel@example.com",
                  "productCode": "BK-100",
                  "productName": "Spring in Action",
                  "quantity": 2,
                  "unitPrice": 49.99,
                  "shippingAddress": "100 King Street, Toronto, ON",
                  "status": "NEW"
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());
    }

    @Test
    void reservationScenarioShouldRemainStable() {
        // DEF-103 reproduction; see logs/DEF-103-inventory-comparison.log for the boundary contract.
        InventoryService service = new InventoryService();

        assertEquals(23, service.reserve("BK-100", 2).getAvailableQuantity());
    }
}
