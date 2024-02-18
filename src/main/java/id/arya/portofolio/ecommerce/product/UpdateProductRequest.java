package id.arya.portofolio.ecommerce.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductRequest {
    private Integer id;
    private String name;
    private String description;
    private Integer price;
    private String productImage;
    private Integer category_id;
    private Integer discount_id;
}
