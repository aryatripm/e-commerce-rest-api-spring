package id.arya.portofolio.ecommerce.order;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest {
    @NotBlank
    private String username;
    @NotBlank
    private Integer address_id;
    @NotBlank
    private Integer payment_id;
    @NotBlank
    private List<Item> items;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item {
        @NotBlank
        private Integer product_id;
        @NotBlank
        private Integer quantity;
    }
}
