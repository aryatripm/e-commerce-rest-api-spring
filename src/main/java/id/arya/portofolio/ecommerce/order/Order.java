package id.arya.portofolio.ecommerce.order;

import id.arya.portofolio.ecommerce.address.Address;
import id.arya.portofolio.ecommerce.payment.Payment;
import id.arya.portofolio.ecommerce.user.User;
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
@Table(name = "orders")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Order extends Auditable<String> {
    @Id
    @GeneratedValue
    private Integer id;
    private Integer total;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;
    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;
}
