package id.arya.portofolio.ecommerce.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.arya.portofolio.ecommerce.address.AddressResponse;
import id.arya.portofolio.ecommerce.address.CreateAddressRequest;
import id.arya.portofolio.ecommerce.config.JwtService;
import id.arya.portofolio.ecommerce.discount.*;
import id.arya.portofolio.ecommerce.token.Token;
import id.arya.portofolio.ecommerce.token.TokenRepository;
import id.arya.portofolio.ecommerce.user.Role;
import id.arya.portofolio.ecommerce.user.User;
import id.arya.portofolio.ecommerce.user.UserRepository;
import id.arya.portofolio.ecommerce.util.WebResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DiscountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private DiscountRepository discountRepository;

    @BeforeEach
    void setUp() {
        tokenRepository.deleteAll();
        discountRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createDiscountSuccess() throws Exception {
        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                .email("admin@email.com")
                .role(Role.ADMIN)
                .build();
        var jwtToken = jwtService.generateToken(admin);
        Token token = Token.builder()
                .user(admin)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        userRepository.save(admin);
        tokenRepository.save(token);

        CreateDiscountRequest request = CreateDiscountRequest.builder()
                .name("Discount 1")
                .description("Discount 1")
                .percentage(10)
                .maxDiscount(10000)
                .minPurchase(100000)
                .active(true)
                .build();

        mockMvc.perform(
                post("/api/v1/discounts/")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                    WebResponse<DiscountResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrors());
                    assertNotNull(response.getData());

                    assertTrue(discountRepository.existsById(response.getData().getId()));

                    assertEquals(request.getName(), response.getData().getName());
                    assertEquals(request.getDescription(), response.getData().getDescription());
                    assertEquals(request.getPercentage(), response.getData().getPercentage());
                    assertEquals(request.getMaxDiscount(), response.getData().getMaxDiscount());
                    assertEquals(request.getMinPurchase(), response.getData().getMinPurchase());
                    assertEquals(request.getActive(), response.getData().getActive());
                }
        );
    }

    @Test
    void createDiscountFailedNotAdmin() throws Exception {
        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                .email("admin@email.com")
                .role(Role.USER)
                .build();
        var jwtToken = jwtService.generateToken(admin);
        Token token = Token.builder()
                .user(admin)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        userRepository.save(admin);
        tokenRepository.save(token);

        CreateDiscountRequest request = CreateDiscountRequest.builder()
                .name("Discount 1")
                .description("Discount 1")
                .percentage(10)
                .maxDiscount(10000)
                .minPurchase(100000)
                .active(true)
                .build();

        mockMvc.perform(
                post("/api/v1/discounts/")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isForbidden()
        );
    }

    @Test
    void getOneDiscountSuccess() throws Exception {
        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                .email("admin@email.com")
                .role(Role.ADMIN)
                .build();
        var jwtToken = jwtService.generateToken(admin);
        Token token = Token.builder()
                .user(admin)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        Discount discount1 = Discount.builder()
                .id(1)
                .name("Discount 1")
                .description("Discount 1")
                .percentage(10)
                .maxDiscount(10000)
                .minPurchase(100000)
                .active(true)
                .build();
        Discount discount2 = Discount.builder()
                .id(2)
                .name("Discount 2")
                .description("Discount 2")
                .percentage(20)
                .maxDiscount(20000)
                .minPurchase(200000)
                .active(true)
                .build();
        userRepository.save(admin);
        tokenRepository.save(token);
        discountRepository.save(discount1);
        discountRepository.save(discount2);

        mockMvc.perform(
                get("/api/v1/discounts/" + discount1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                    WebResponse<DiscountResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrors());
                    assertNotNull(response.getData());

                    assertTrue(discountRepository.existsById(response.getData().getId()));

                    assertEquals(discount1.getName(), response.getData().getName());
                    assertEquals(discount1.getDescription(), response.getData().getDescription());
                    assertEquals(discount1.getPercentage(), response.getData().getPercentage());
                    assertEquals(discount1.getMaxDiscount(), response.getData().getMaxDiscount());
                    assertEquals(discount1.getMinPurchase(), response.getData().getMinPurchase());
                    assertEquals(discount1.getActive(), response.getData().getActive());
                }
        );
    }

    @Test
    void getOneDiscountFailedNotFound() throws Exception {
        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                .email("admin@email.com")
                .role(Role.ADMIN)
                .build();
        var jwtToken = jwtService.generateToken(admin);
        Token token = Token.builder()
                .user(admin)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        Discount discount1 = Discount.builder()
                .id(1)
                .name("Discount 1")
                .description("Discount 1")
                .percentage(10)
                .maxDiscount(10000)
                .minPurchase(100000)
                .active(true)
                .build();
        Discount discount2 = Discount.builder()
                .id(2)
                .name("Discount 2")
                .description("Discount 2")
                .percentage(20)
                .maxDiscount(20000)
                .minPurchase(200000)
                .active(true)
                .build();
        userRepository.save(admin);
        tokenRepository.save(token);
        discountRepository.save(discount1);
        discountRepository.save(discount2);

        mockMvc.perform(
                get("/api/v1/discounts/99")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                    WebResponse<DiscountResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrors());
                    assertNull(response.getData());
                }
        );
    }

    @Test
    void getAllDiscountSuccess() throws Exception {
        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                .email("admin@email.com")
                .role(Role.ADMIN)
                .build();
        var jwtToken = jwtService.generateToken(admin);
        Token token = Token.builder()
                .user(admin)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        Discount discount1 = Discount.builder()
                .id(1)
                .name("Discount 1")
                .description("Discount 1")
                .percentage(10)
                .maxDiscount(10000)
                .minPurchase(100000)
                .active(true)
                .build();
        Discount discount2 = Discount.builder()
                .id(2)
                .name("Discount 2")
                .description("Discount 2")
                .percentage(20)
                .maxDiscount(20000)
                .minPurchase(200000)
                .active(true)
                .build();
        userRepository.save(admin);
        tokenRepository.save(token);
        discountRepository.save(discount1);
        discountRepository.save(discount2);

        mockMvc.perform(
                get("/api/v1/discounts/")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                    WebResponse<List<DiscountResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrors());
                    assertNotNull(response.getData());

                    assertEquals(2, response.getData().size());
                }
        );
    }

    @Test
    void getAllDiscountFailedNotAdmin() throws Exception {
        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                .email("admin@email.com")
                .role(Role.USER)
                .build();
        var jwtToken = jwtService.generateToken(admin);
        Token token = Token.builder()
                .user(admin)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        Discount discount1 = Discount.builder()
                .id(1)
                .name("Discount 1")
                .description("Discount 1")
                .percentage(10)
                .maxDiscount(10000)
                .minPurchase(100000)
                .active(true)
                .build();
        Discount discount2 = Discount.builder()
                .id(2)
                .name("Discount 2")
                .description("Discount 2")
                .percentage(20)
                .maxDiscount(20000)
                .minPurchase(200000)
                .active(true)
                .build();
        userRepository.save(admin);
        tokenRepository.save(token);
        discountRepository.save(discount1);
        discountRepository.save(discount2);

        mockMvc.perform(
                get("/api/v1/discounts/")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
        ).andExpectAll(
                status().isForbidden()
        );
    }

    @Test
    void updateDiscountSuccess() throws Exception {
        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                .email("admin@email.com")
                .role(Role.ADMIN)
                .build();
        var jwtToken = jwtService.generateToken(admin);
        Token token = Token.builder()
                .user(admin)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        Discount discount1 = Discount.builder()
                .id(1)
                .name("Discount 1")
                .description("Discount 1")
                .percentage(10)
                .maxDiscount(10000)
                .minPurchase(100000)
                .active(true)
                .build();
        Discount discount2 = Discount.builder()
                .id(2)
                .name("Discount 2")
                .description("Discount 2")
                .percentage(20)
                .maxDiscount(20000)
                .minPurchase(200000)
                .active(true)
                .build();
        userRepository.save(admin);
        tokenRepository.save(token);
        discountRepository.save(discount1);
        discountRepository.save(discount2);

        UpdateDiscountRequest request = UpdateDiscountRequest.builder()
                .id(1)
                .name("Discount 1 Updated")
                .description("Discount 1 Updated")
                .percentage(5)
                .maxDiscount(5000)
                .minPurchase(50000)
                .active(true)
                .build();

        mockMvc.perform(
                patch("/api/v1/discounts/" + discount1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                    WebResponse<DiscountResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrors());
                    assertNotNull(response.getData());

                    assertTrue(discountRepository.existsById(response.getData().getId()));

                    assertEquals(request.getName(), response.getData().getName());
                    assertEquals(request.getDescription(), response.getData().getDescription());
                    assertEquals(request.getPercentage(), response.getData().getPercentage());
                    assertEquals(request.getMaxDiscount(), response.getData().getMaxDiscount());
                    assertEquals(request.getMinPurchase(), response.getData().getMinPurchase());
                    assertEquals(request.getActive(), response.getData().getActive());
                }
        );
    }

    @Test
    void updateDiscountFailedNotFound() throws Exception {
        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                .email("admin@email.com")
                .role(Role.ADMIN)
                .build();
        var jwtToken = jwtService.generateToken(admin);
        Token token = Token.builder()
                .user(admin)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        Discount discount1 = Discount.builder()
                .id(1)
                .name("Discount 1")
                .description("Discount 1")
                .percentage(10)
                .maxDiscount(10000)
                .minPurchase(100000)
                .active(true)
                .build();
        Discount discount2 = Discount.builder()
                .id(2)
                .name("Discount 2")
                .description("Discount 2")
                .percentage(20)
                .maxDiscount(20000)
                .minPurchase(200000)
                .active(true)
                .build();
        userRepository.save(admin);
        tokenRepository.save(token);
        discountRepository.save(discount1);
        discountRepository.save(discount2);

        UpdateDiscountRequest request = UpdateDiscountRequest.builder()
                .id(1)
                .name("Discount 1 Updated")
                .description("Discount 1 Updated")
                .percentage(5)
                .maxDiscount(5000)
                .minPurchase(50000)
                .active(true)
                .build();

        mockMvc.perform(
                patch("/api/v1/discounts/99")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                    WebResponse<DiscountResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrors());
                    assertNull(response.getData());
                }
        );
    }

    @Test
    void deleteDiscountSuccess() throws Exception {
        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                .email("admin@email.com")
                .role(Role.ADMIN)
                .build();
        var jwtToken = jwtService.generateToken(admin);
        Token token = Token.builder()
                .user(admin)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        Discount discount1 = Discount.builder()
                .id(1)
                .name("Discount 1")
                .description("Discount 1")
                .percentage(10)
                .maxDiscount(10000)
                .minPurchase(100000)
                .active(true)
                .build();
        Discount discount2 = Discount.builder()
                .id(2)
                .name("Discount 2")
                .description("Discount 2")
                .percentage(20)
                .maxDiscount(20000)
                .minPurchase(200000)
                .active(true)
                .build();
        userRepository.save(admin);
        tokenRepository.save(token);
        discountRepository.save(discount1);
        discountRepository.save(discount2);

        mockMvc.perform(
                delete("/api/v1/discounts/" + discount1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrors());
                    assertNotNull(response.getData());

                    assertFalse(discountRepository.existsById(discount1.getId()));

                    assertEquals("OK", response.getData());
                }
        );
    }
}
