package id.arya.portofolio.ecommerce.cart;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteCartItemRequest {
    @NotBlank
    private Integer productId;
    private Integer userId;
}
