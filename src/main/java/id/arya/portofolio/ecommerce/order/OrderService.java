package id.arya.portofolio.ecommerce.order;

import id.arya.portofolio.ecommerce.address.Address;
import id.arya.portofolio.ecommerce.address.AddressRepository;
import id.arya.portofolio.ecommerce.payment.Payment;
import id.arya.portofolio.ecommerce.payment.PaymentRepository;
import id.arya.portofolio.ecommerce.product.*;
import id.arya.portofolio.ecommerce.user.Role;
import id.arya.portofolio.ecommerce.user.User;
import id.arya.portofolio.ecommerce.user.UserRepository;
import id.arya.portofolio.ecommerce.user.UserResponse;
import id.arya.portofolio.ecommerce.util.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductInventoryRepository productInventoryRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public OrderResponse create(CreateOrderRequest request) {
        validationService.validate(request);

        User user = userRepository.findById(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Address address = addressRepository.findFirstByUserAndId(user, request.getAddress_id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));

        Payment payment = paymentRepository.findFirstByUserAndId(user, request.getPayment_id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));

        Order order = Order.builder()
                .user(user)
                .address(address)
                .payment(payment)
                .status(OrderStatus.CREATED)
                .build();

        int total = 0;
        List<OrderItem> items = new ArrayList<>();

        for (CreateOrderRequest.Item item : request.getItems()) {
            var product = productRepository.findById(item.getProduct_id())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

            ProductInventory inventory = product.getInventory();

            if (inventory.getQuantity() < item.getQuantity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product out of stock");
            }

            inventory.setQuantity(inventory.getQuantity() - item.getQuantity());
            productInventoryRepository.save(inventory);

            var priceProduct = getPriceProduct(item, product);

            total += priceProduct;

            items.add(
                    OrderItem.builder()
                            .product(product)
                            .quantity(item.getQuantity())
                            .price(priceProduct)
                            .order(order)
                            .build()
            );
        }

        order.setTotal(total);
        order.setOrderItems(items);

        orderRepository.save(order);

        return toOrderResponse(order);
    }

    public Page<OrderResponse> list(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderRepository.findAll(pageable);
        List<OrderResponse> orderResponses = orders.getContent().stream()
                .map(this::toOrderResponse)
                .toList();
        return new PageImpl<>(orderResponses, pageable, orders.getTotalElements());
    }

    public Page<OrderResponse> listByUser(Principal userPrincipal, Integer page, Integer size, String username) {
        User user = (User) ((UsernamePasswordAuthenticationToken) userPrincipal).getPrincipal();
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders;
        if (user.getRole() == Role.ADMIN) {
            User userFromUsername = userRepository.findById(username)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            orders = orderRepository.findAllByUser(userFromUsername, pageable);
        } else {
            orders = orderRepository.findAllByUser(user, pageable);
        }
        List<OrderResponse> orderResponses = orders.getContent().stream()
                .map(this::toOrderResponse)
                .toList();
        return new PageImpl<>(orderResponses, pageable, orders.getTotalElements());
    }

    public OrderResponse get(Principal userPrincipal, Integer id) {
        User user = (User) ((UsernamePasswordAuthenticationToken) userPrincipal).getPrincipal();
        Order order;
        if (user.getRole() == Role.ADMIN) {
            order = orderRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        } else {
            order = orderRepository.findFirstByUserAndId(user, id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        }
        return toOrderResponse(order);
    }

    public OrderResponse updateStatus(Principal userPrincipal, Integer id, UpdateOrderStatusRequest request) {
        User user = (User) ((UsernamePasswordAuthenticationToken) userPrincipal).getPrincipal();
        Order order;
        if (user.getRole() == Role.ADMIN) {
            order = orderRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        } else {
            order = orderRepository.findFirstByUserAndId(user, id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        }
        order.setStatus(request.getStatus());
        orderRepository.save(order);
        return toOrderResponse(order);
    }

    private int getPriceProduct(CreateOrderRequest.Item item, Product product) {
        var priceProduct = product.getPrice() * item.getQuantity();

        if (product.getDiscount() != null && product.getDiscount().getActive() && product.getDiscount().getMinPurchase() <= priceProduct) {
            var productDiscount = product.getDiscount().getPercentage();
            var discount = priceProduct * productDiscount / 100;
            if (discount > product.getDiscount().getMaxDiscount()) {
                discount = product.getDiscount().getMaxDiscount();
            }
            priceProduct -= discount;
        }
        return priceProduct;
    }

    private OrderResponse toOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .total(order.getTotal())
                .status(order.getStatus().name())
                .orderItems(order.getOrderItems().stream().map(this::toOrderItemResponse).collect(Collectors.toList()))
                .user(UserResponse.builder()
                        .username(order.getUser().getUsername())
                        .email(order.getUser().getEmail())
                        .build())
                .build();
    }

    private OrderResponse.OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        return OrderResponse.OrderItemResponse.builder()
                .productResponse(ProductResponse.builder()
                        .id(orderItem.getProduct().getId())
                        .name(orderItem.getProduct().getName())
                        .price(orderItem.getProduct().getPrice())
                        .build())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .build();
    }
}
