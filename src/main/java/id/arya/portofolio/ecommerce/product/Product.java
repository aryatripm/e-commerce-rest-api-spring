package id.arya.portofolio.ecommerce.product;

import id.arya.portofolio.ecommerce.discount.Discount;
import id.arya.portofolio.ecommerce.util.Auditable;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Product extends Auditable<String> {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    @Lob
    private String description;
    private Integer price;
    private String productImage;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private ProductCategory category;
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "inventory_id")
    private ProductInventory inventory;
    @ManyToOne
    @JoinColumn(name = "discount_id")
    private Discount discount;
}
