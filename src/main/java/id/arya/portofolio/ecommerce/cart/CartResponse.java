package id.arya.portofolio.ecommerce.cart;

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
public class CartResponse {
    private Integer id;
    private UserResponse user;
    private List<CartItemResponse> items;
}
