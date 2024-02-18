package id.arya.portofolio.ecommerce.product;

import id.arya.portofolio.ecommerce.util.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class ProductInventory extends Auditable<String> {
    @Id
    @GeneratedValue
    private Integer id;
    private Integer quantity;
    @OneToOne(mappedBy = "inventory", cascade = CascadeType.REMOVE)
    private Product product;
}
