package id.arya.portofolio.ecommerce.cart;

import id.arya.portofolio.ecommerce.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByUser(User user);
}
