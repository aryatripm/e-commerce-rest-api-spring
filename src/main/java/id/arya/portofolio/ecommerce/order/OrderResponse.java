package id.arya.portofolio.ecommerce.order;

import id.arya.portofolio.ecommerce.address.AddressResponse;
import id.arya.portofolio.ecommerce.payment.PaymentResponse;
import id.arya.portofolio.ecommerce.product.ProductResponse;
import id.arya.portofolio.ecommerce.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Integer id;
    private Integer total;
    private String status;
    private List<OrderItemResponse> orderItems;
    private UserResponse user;
    private AddressResponse address;
    private PaymentResponse payment;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderItemResponse {
        private ProductResponse productResponse;
        private Integer quantity;
        private Integer price;
    }
}
