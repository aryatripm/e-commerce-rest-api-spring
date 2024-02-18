package id.arya.portofolio.ecommerce.discount;

import id.arya.portofolio.ecommerce.product.Product;
import id.arya.portofolio.ecommerce.util.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Discount extends Auditable<String> {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    @Lob
    private String description;
    private Integer percentage;
    private Integer maxDiscount;
    private Integer minPurchase;
    private Boolean active;
    @OneToMany(mappedBy = "discount")
    private List<Product> products;
}
