package id.arya.portofolio.ecommerce.product;

import id.arya.portofolio.ecommerce.discount.DiscountResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private Integer id;
    private String name;
    private String description;
    private Integer price;
    private String productImage;
    private Date creationDate;
    private Date lastModifiedDate;
    private ProductCategoryResponse category;
    private Integer quantity;
    private DiscountResponse discount;
}
