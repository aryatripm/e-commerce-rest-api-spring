package id.arya.portofolio.ecommerce.cart;

import id.arya.portofolio.ecommerce.discount.DiscountResponse;
import id.arya.portofolio.ecommerce.product.Product;
import id.arya.portofolio.ecommerce.product.ProductCategoryResponse;
import id.arya.portofolio.ecommerce.product.ProductRepository;
import id.arya.portofolio.ecommerce.product.ProductResponse;
import id.arya.portofolio.ecommerce.user.User;
import id.arya.portofolio.ecommerce.user.UserRepository;
import id.arya.portofolio.ecommerce.user.UserResponse;
import id.arya.portofolio.ecommerce.util.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    public CartResponse createOrUpdate(CreateCartRequest request, Principal principal) {
        validationService.validate(request);

        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        Cart cart = cartRepository.findByUser(user).orElse(Cart.builder().user(user).items(List.of()).build());

        List<CartItem> cartItems = cart.getItems();

        if (cartItems.stream().anyMatch(cartItem -> cartItem.getProduct().getId().equals(request.getProductId()))) {
            cartItems.stream()
                    .filter(cartItem -> cartItem.getProduct().getId().equals(request.getProductId()))
                    .findFirst()
                    .ifPresent(cartItem -> cartItem.setQuantity(request.getQuantity()));
            cartRepository.save(cart);
            return toCartResponse(cart);
        } else {
            cartItems.add(
                    CartItem.builder()
                            .product(product)
                            .quantity(request.getQuantity())
                            .cart(cart)
                            .build()
            );
            cartRepository.save(cart);
            return toCartResponse(cart);
        }
    }

    public CartResponse get(Principal principal) {
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));

        return toCartResponse(cart);
    }

    public CartResponse get(String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));

        return toCartResponse(cart);
    }

    public void delete(Principal principal) {
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));

        cartRepository.delete(cart);
    }

    public void deleteOne(DeleteCartItemRequest request, Principal principal) {
        validationService.validate(request);

        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found"));

        cart.getItems().removeIf(cartItem -> cartItem.getProduct().getId().equals(request.getProductId()));

        cartRepository.save(cart);
    }

    private CartResponse toCartResponse(Cart cart) {
        return CartResponse.builder()
                .id(cart.getId())
                .user(UserResponse.builder().username(cart.getUser().getUsername()).email(cart.getUser().getEmail()).build())
                .items(cart.getItems().stream().map(this::toCartItemResponse).toList())
                .build();
    }

    private CartItemResponse toCartItemResponse(CartItem cartItem) {
        return CartItemResponse.builder()
                .id(cartItem.getId())
                .product(ProductResponse.builder()
                        .id(cartItem.getProduct().getId())
                        .name(cartItem.getProduct().getName())
                        .price(cartItem.getProduct().getPrice())
                        .description(cartItem.getProduct().getDescription())
                        .productImage(cartItem.getProduct().getProductImage())
                        .creationDate(cartItem.getProduct().getCreationDate())
                        .category(ProductCategoryResponse.builder()
                                .id(cartItem.getProduct().getCategory().getId())
                                .name(cartItem.getProduct().getCategory().getName())
                                .build())
                        .quantity(cartItem.getProduct().getInventory().getQuantity())
                        .discount(cartItem.getProduct().getDiscount() != null ? DiscountResponse.builder()
                                .id(cartItem.getProduct().getDiscount().getId())
                                .name(cartItem.getProduct().getDiscount().getName())
                                .percentage(cartItem.getProduct().getDiscount().getPercentage())
                                .maxDiscount(cartItem.getProduct().getDiscount().getMaxDiscount())
                                .minPurchase(cartItem.getProduct().getDiscount().getMinPurchase())
                                .active(cartItem.getProduct().getDiscount().getActive())
                                .build() : null)
                        .build())
                .quantity(cartItem.getQuantity())
                .build();
    }
}
