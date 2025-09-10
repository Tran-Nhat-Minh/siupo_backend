package supbo.restaurant.userservice.client;

import org.springframework.context.annotation.Primary;
import supbo.restaurant.userservice.dto.response.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "order-service",
        url = "http://localhost:8082",
        fallback = OrderClientFallback.class
)
@Primary
public interface OrderClient {

    @GetMapping("/api/orders/customer/{customerId}")
    List<OrderResponse> getOrdersByCustomerId(@PathVariable("customerId") Long customerId);
}