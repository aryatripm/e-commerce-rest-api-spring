package id.arya.portofolio.ecommerce.address;

import id.arya.portofolio.ecommerce.util.ValidationService;
import id.arya.portofolio.ecommerce.user.User;
import id.arya.portofolio.ecommerce.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ValidationService validationService;

    public AddressResponse create(CreateAddressRequest request, String username) {
        validationService.validate(request);

        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        var address = Address.builder()
                .address(request.getAddress())
                .city(request.getCity())
                .province(request.getProvince())
                .postalCode(request.getPostalCode())
                .phoneNumber(request.getPhoneNumber())
                .user(user)
                .build();

        addressRepository.save(address);

        return toAddressResponse(address);
    }

    public AddressResponse get(Integer id, String username) {
        var user = userRepository.findById(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        var address = addressRepository.findFirstByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));

        return AddressResponse.builder()
                .id(address.getId())
                .address(address.getAddress())
                .city(address.getCity())
                .province(address.getProvince())
                .postalCode(address.getPostalCode())
                .phoneNumber(address.getPhoneNumber())
                .build();
    }

    public List<AddressResponse> list(String username) {
        var user = userRepository.findById(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        var addresses = addressRepository.findAllByUser(user);
        return addresses.stream().map(this::toAddressResponse).toList();
    }

    public AddressResponse update(Integer id, UpdateAddressRequest request, String username) {
        validationService.validate(request);

        var user = userRepository.findById(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        var address = addressRepository.findFirstByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));

        address.setAddress(request.getAddress() == null ? address.getAddress() : request.getAddress());
        address.setCity(request.getCity() == null ? address.getCity() : request.getCity());
        address.setProvince(request.getProvince() == null ? address.getProvince() : request.getProvince());
        address.setPostalCode(request.getPostalCode() == null ? address.getPostalCode() : request.getPostalCode());
        address.setPhoneNumber(request.getPhoneNumber() == null ? address.getPhoneNumber() : request.getPhoneNumber());

        addressRepository.save(address);

        return toAddressResponse(address);
    }

    public void delete(Integer id, String username) {
        var user = userRepository.findById(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        var address = addressRepository.findFirstByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));
        addressRepository.delete(address);
    }

    private AddressResponse toAddressResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .address(address.getAddress())
                .city(address.getCity())
                .province(address.getProvince())
                .postalCode(address.getPostalCode())
                .phoneNumber(address.getPhoneNumber())
                .build();
    }
}
