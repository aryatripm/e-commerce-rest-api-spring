package id.arya.portofolio.ecommerce.discount;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateDiscountRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private Integer percentage;
    private Integer maxDiscount;
    private Integer minPurchase;
    private Boolean active;
}
