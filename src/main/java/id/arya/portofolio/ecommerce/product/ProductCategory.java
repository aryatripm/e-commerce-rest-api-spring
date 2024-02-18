package id.arya.portofolio.ecommerce.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ProductCategory {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    @Lob
    private String description;
    @OneToMany(mappedBy = "category")
    private List<Product> products;
}
