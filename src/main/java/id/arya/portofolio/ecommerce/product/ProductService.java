package id.arya.portofolio.ecommerce.product;

import id.arya.portofolio.ecommerce.discount.Discount;
import id.arya.portofolio.ecommerce.discount.DiscountRepository;
import id.arya.portofolio.ecommerce.discount.DiscountResponse;
import id.arya.portofolio.ecommerce.util.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ValidationService validationService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ProductInventoryRepository productInventoryRepository;

    @Autowired
    private DiscountRepository discountRepository;

    @Value("${application.product.image-path}")
    private String uploadDir;

    public ProductResponse create(CreateProductRequest request) throws IOException {
        validationService.validate(request);

        ProductCategory category = productCategoryRepository.findById(request.getCategory_id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category Not Found"));

        Optional<Discount> discount = discountRepository.findById(request.getDiscount_id());

        ProductInventory inventory = ProductInventory.builder().quantity(request.getQuantity()).build();
        productInventoryRepository.save(inventory);

        Path destinationFile;
        try {
            if (request.getProductImage().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image is required");
            }
            Path rootLocation = Paths.get(uploadDir);
            Files.createDirectories(rootLocation);
            destinationFile = rootLocation.resolve(Paths.get(Objects.requireNonNull(request.getProductImage().getOriginalFilename()))).normalize().toAbsolutePath();

            try (InputStream inputStream = request.getProductImage().getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (ResponseStatusException | IOException e) {
            throw new RuntimeException(e);
        }

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .productImage(destinationFile.toString())
                .category(category)
                .inventory(inventory)
                .build();

        discount.ifPresent(product::setDiscount);

        productRepository.save(product);

        return toProductResponse(product);
    }

    public ProductResponse get(Integer id) {
        var product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        return toProductResponse(product);
    }

    public Page<ProductResponse> list(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findAll(pageable);
        List<ProductResponse> productResponses = products.getContent().stream()
                .map(this::toProductResponse)
                .toList();
        return new PageImpl<>(productResponses, pageable, products.getTotalElements());
    }

    public Page<ProductResponse> listByCategory(Integer categoryId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findAllByCategoryId(categoryId, pageable);
        List<ProductResponse> productResponses = products.getContent().stream()
                .map(this::toProductResponse)
                .toList();
        return new PageImpl<>(productResponses, pageable, products.getTotalElements());
    }

    public ProductResponse update(Integer id, UpdateProductRequest request) {
        validationService.validate(request);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        ProductCategory category = productCategoryRepository.findById(request.getCategory_id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category Not Found"));

        Discount discount = discountRepository.findById(request.getDiscount_id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Discount Not Found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setProductImage(request.getProductImage());
        product.setCategory(category);
        product.setDiscount(discount);

        productRepository.save(product);

        return toProductResponse(product);
    }

    public ProductResponse updateQuantity(Integer id, UpdateProductInventoryRequest request) {
        validationService.validate(request);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        ProductInventory inventory = product.getInventory();
        inventory.setQuantity(request.getQuantity());

        productInventoryRepository.save(inventory);

        return toProductResponse(product);
    }

    public void delete(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        productRepository.delete(product);
    }

    private ProductResponse toProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .productImage(product.getProductImage())
                .creationDate(product.getCreationDate())
                .lastModifiedDate(product.getLastModifiedDate())
                .category(toProductCategoryResponse(product.getCategory()))
                .quantity(product.getInventory().getQuantity())
                .discount(toDiscountResponse(product.getDiscount()))
                .build();
    }

    private ProductCategoryResponse toProductCategoryResponse(ProductCategory productCategory) {
        return ProductCategoryResponse.builder().name(productCategory.getName()).description(productCategory.getDescription()).build();
    }

    private DiscountResponse toDiscountResponse(Discount discount) {
        return DiscountResponse.builder()
                .name(discount.getName())
                .description(discount.getDescription())
                .percentage(discount.getPercentage())
                .maxDiscount(discount.getMaxDiscount())
                .minPurchase(discount.getMinPurchase())
                .build();
    }
}
