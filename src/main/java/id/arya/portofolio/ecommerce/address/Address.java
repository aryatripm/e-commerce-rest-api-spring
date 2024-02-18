package id.arya.portofolio.ecommerce.address;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.arya.portofolio.ecommerce.order.Order;
import id.arya.portofolio.ecommerce.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String address;
    private String city;
    private String province;
    private String postalCode;
    private String phoneNumber;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @OneToMany(mappedBy = "address")
    private List<Order> orders;
}
