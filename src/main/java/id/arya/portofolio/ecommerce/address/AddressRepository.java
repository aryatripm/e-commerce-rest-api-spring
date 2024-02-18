package id.arya.portofolio.ecommerce.address;

import id.arya.portofolio.ecommerce.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Integer> {
    Optional<Address> findFirstByUserAndId(User user, Integer id);
    List<Address> findAllByUser(User user);
}
