package supbo.restaurant.userservice.client;

import supbo.restaurant.userservice.dto.response.OrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class OrderClientFallback implements OrderClient {

    @Override
    public List<OrderResponse> getOrdersByCustomerId(Long customerId) {
        log.warn("Order Service is not available. Returning empty orders for customer: {}", customerId);
        return new ArrayList<>();
    }
}