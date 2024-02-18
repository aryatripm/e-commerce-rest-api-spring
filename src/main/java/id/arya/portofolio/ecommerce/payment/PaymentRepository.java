package id.arya.portofolio.ecommerce.payment;

import id.arya.portofolio.ecommerce.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Optional<Payment> findFirstByUserAndId(User user, Integer id);
    List<Payment> findAllByUser(User user);
}
