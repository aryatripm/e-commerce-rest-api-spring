package id.arya.portofolio.ecommerce.order;

import id.arya.portofolio.ecommerce.address.Address;
import id.arya.portofolio.ecommerce.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    Optional<Order> findFirstByUserAndId(User user, Integer id);
    Page<Order> findAllByUser(User user, Pageable pageable);
}
