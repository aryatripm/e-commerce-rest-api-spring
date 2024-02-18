package id.arya.portofolio.ecommerce.discount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDiscountRequest {
    private Integer id;
    private String name;
    private String description;
    private Integer percentage;
    private Integer maxDiscount;
    private Integer minPurchase;
    private Boolean active;
}
