package id.arya.portofolio.ecommerce.payment;

import id.arya.portofolio.ecommerce.order.Order;
import id.arya.portofolio.ecommerce.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Payment {
    @Id
    @GeneratedValue
    private Integer id;
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;
    private String accountNumber;
    private String accountName;
    private String provider;
    private Date expiry;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @OneToMany(mappedBy = "payment")
    private List<Order> orders;
}
