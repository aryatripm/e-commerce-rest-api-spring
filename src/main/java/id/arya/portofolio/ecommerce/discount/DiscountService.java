package id.arya.portofolio.ecommerce.discount;

import id.arya.portofolio.ecommerce.util.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DiscountService {

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private ValidationService validationService;

    public DiscountResponse create(CreateDiscountRequest request) {
        validationService.validate(request);

        Discount discount = Discount.builder()
                .name(request.getName())
                .description(request.getDescription())
                .percentage(request.getPercentage())
                .maxDiscount(request.getMaxDiscount())
                .minPurchase(request.getMinPurchase())
                .active(request.getActive())
                .build();

        discountRepository.save(discount);
        return toDiscountResponse(discount);
    }

    public DiscountResponse get(Integer id) {
        var discount = discountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Discount not found"));

        return toDiscountResponse(discount);
    }

    public List<DiscountResponse> list() {
        return discountRepository.findAll().stream()
                .map(this::toDiscountResponse)
                .toList();
    }

    public DiscountResponse update(Integer id, UpdateDiscountRequest request) {
        validationService.validate(request);

        var discount = discountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Discount not found"));

        discount.setName(request.getName());
        discount.setDescription(request.getDescription());
        discount.setPercentage(request.getPercentage());
        discount.setMaxDiscount(request.getMaxDiscount());
        discount.setMinPurchase(request.getMinPurchase());
        discount.setActive(request.getActive());

        discountRepository.save(discount);
        return toDiscountResponse(discount);
    }

    public void delete(Integer id) {
        var discount = discountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Discount not found"));

        discountRepository.delete(discount);
    }

    private DiscountResponse toDiscountResponse(Discount discount) {
        return DiscountResponse.builder()
                .id(discount.getId())
                .name(discount.getName())
                .description(discount.getDescription())
                .percentage(discount.getPercentage())
                .maxDiscount(discount.getMaxDiscount())
                .minPurchase(discount.getMinPurchase())
                .active(discount.getActive())
                .build();
    }
}
