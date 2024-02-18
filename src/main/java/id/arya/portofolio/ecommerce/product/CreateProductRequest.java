package id.arya.portofolio.ecommerce.product;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private Integer price;
    private MultipartFile productImage;
    private Integer category_id;
    private Integer quantity;
    private Integer discount_id;
}
