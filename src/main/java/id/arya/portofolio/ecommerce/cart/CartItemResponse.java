package id.arya.portofolio.ecommerce.cart;

import id.arya.portofolio.ecommerce.product.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse {
    private Integer id;
    private Integer quantity;
    private ProductResponse product;
}
