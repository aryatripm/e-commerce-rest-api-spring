package id.arya.portofolio.ecommerce.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.arya.portofolio.ecommerce.payment.*;
import id.arya.portofolio.ecommerce.config.JwtService;
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

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        tokenRepository.deleteAll();
        paymentRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createPaymentFailedUserNotFound() throws Exception {
        User user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .email("user@email.com")
                .role(Role.USER)
                .build();
        var jwtToken = jwtService.generateToken(user);
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        userRepository.save(user);
        tokenRepository.save(token);

        CreatePaymentRequest request = CreatePaymentRequest.builder()
                .accountName("Account Name")
                .accountNumber("1234567890")
                .paymentType(PaymentType.BANK_TRANSFER)
                .provider("Bank")
                .expiry(new Date())
                .build();

        mockMvc.perform(
                post("/api/v1/users/notfound/payments")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                    WebResponse<PaymentResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrors());
                    assertNull(response.getData());
                }
        );
    }

    @Test
    void createPaymentFailedPaymentNotCompleted() throws Exception {
        User user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .email("user@email.com")
                .role(Role.USER)
                .build();
        var jwtToken = jwtService.generateToken(user);
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        userRepository.save(user);
        tokenRepository.save(token);

        CreatePaymentRequest request = CreatePaymentRequest.builder()
                .accountNumber("1234567890")
                .paymentType(PaymentType.BANK_TRANSFER)
                .provider("Bank")
                .build();

        mockMvc.perform(
                post("/api/v1/users/user/payments")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                    WebResponse<PaymentResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrors());
                    assertNull(response.getData());
                }
        );
    }

    @Test
    void createPaymentSuccess() throws Exception {
        User user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .email("user@email.com")
                .role(Role.USER)
                .build();
        var jwtToken = jwtService.generateToken(user);
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        userRepository.save(user);
        tokenRepository.save(token);

        CreatePaymentRequest request = CreatePaymentRequest.builder()
                .accountName("Account Name")
                .accountNumber("1234567890")
                .paymentType(PaymentType.BANK_TRANSFER)
                .provider("Bank")
                .expiry(new Date())
                .build();

        mockMvc.perform(
                post("/api/v1/users/user/payments")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                    WebResponse<PaymentResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrors());
                    assertNotNull(response.getData());

                    assertEquals(request.getAccountName(), response.getData().getAccountName());
                    assertEquals(request.getAccountNumber(), response.getData().getAccountNumber());
                    assertEquals(request.getPaymentType(), response.getData().getPaymentType());
                    assertEquals(request.getProvider(), response.getData().getProvider());
                    assertEquals(request.getExpiry(), response.getData().getExpiry());
                }
        );
    }

    @Test
    void getAllPaymentsFailedUserNotFound() throws Exception {
        User user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .email("user@email.com")
                .role(Role.USER)
                .build();
        var jwtToken = jwtService.generateToken(user);
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        userRepository.save(user);
        tokenRepository.save(token);

        mockMvc.perform(
                get("/api/v1/users/notfound/payments")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                    WebResponse<List<PaymentResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrors());
                    assertNull(response.getData());
                }
        );
    }

    @Test
    void getAllPaymentsSuccess() throws Exception {
        User user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .email("user@email.com")
                .role(Role.USER)
                .build();
        var jwtToken = jwtService.generateToken(user);
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        Payment payment1 = Payment.builder()
                .accountName("Account Name")
                .accountNumber("1234567890")
                .paymentType(PaymentType.BANK_TRANSFER)
                .provider("Bank")
                .expiry(new Date())
                .user(user)
                .build();
        Payment payment2 = Payment.builder()
                .accountName("Account Name 2")
                .accountNumber("9876543210")
                .paymentType(PaymentType.E_WALLET)
                .provider("E-Wallet")
                .expiry(new Date())
                .user(user)
                .build();
        userRepository.save(user);
        tokenRepository.save(token);
        paymentRepository.save(payment1);
        paymentRepository.save(payment2);

        mockMvc.perform(
                get("/api/v1/users/user/payments")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                    WebResponse<List<PaymentResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrors());
                    assertNotNull(response.getData());
                    assertEquals(2, response.getData().size());
                }
        );
    }

    @Test
    void getPaymentFailedUserNotFound() throws Exception {
        User user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .email("user@email.com")
                .role(Role.USER)
                .build();
        var jwtToken = jwtService.generateToken(user);
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        Payment payment1 = Payment.builder()
                .accountName("Account Name")
                .accountNumber("1234567890")
                .paymentType(PaymentType.BANK_TRANSFER)
                .provider("Bank")
                .expiry(new Date())
                .user(user)
                .build();
        Payment payment2 = Payment.builder()
                .accountName("Account Name 2")
                .accountNumber("9876543210")
                .paymentType(PaymentType.E_WALLET)
                .provider("E-Wallet")
                .expiry(new Date())
                .user(user)
                .build();
        userRepository.save(user);
        tokenRepository.save(token);
        paymentRepository.save(payment1);
        paymentRepository.save(payment2);

        mockMvc.perform(
                get("/api/v1/users/notfound/payments/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                    WebResponse<PaymentResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrors());
                    assertNull(response.getData());
                }
        );
    }

    @Test
    void getPaymentFailedPaymentNotFound() throws Exception {
        User user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .email("user@email.com")
                .role(Role.USER)
                .build();
        var jwtToken = jwtService.generateToken(user);
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        Payment payment1 = Payment.builder()
                .accountName("Account Name")
                .accountNumber("1234567890")
                .paymentType(PaymentType.BANK_TRANSFER)
                .provider("Bank")
                .expiry(new Date())
                .user(user)
                .build();
        Payment payment2 = Payment.builder()
                .accountName("Account Name 2")
                .accountNumber("9876543210")
                .paymentType(PaymentType.E_WALLET)
                .provider("E-Wallet")
                .expiry(new Date())
                .user(user)
                .build();
        userRepository.save(user);
        tokenRepository.save(token);
        paymentRepository.save(payment1);
        paymentRepository.save(payment2);

        mockMvc.perform(
                get("/api/v1/users/user/payments/3")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                    WebResponse<PaymentResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrors());
                    assertNull(response.getData());
                }
        );
    }

    @Test
    void getPaymentSuccess() throws Exception {
        User user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .email("user@email.com")
                .role(Role.USER)
                .build();
        var jwtToken = jwtService.generateToken(user);
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        Payment payment1 = Payment.builder()
                .id(1)
                .accountName("Account Name")
                .accountNumber("1234567890")
                .paymentType(PaymentType.BANK_TRANSFER)
                .provider("Bank")
                .expiry(new Date())
                .user(user)
                .build();
        Payment payment2 = Payment.builder()
                .id(2)
                .accountName("Account Name 2")
                .accountNumber("9876543210")
                .paymentType(PaymentType.E_WALLET)
                .provider("E-Wallet")
                .expiry(new Date())
                .user(user)
                .build();
        userRepository.save(user);
        tokenRepository.save(token);
        paymentRepository.save(payment1);
        paymentRepository.save(payment2);

        mockMvc.perform(
                get("/api/v1/users/user/payments/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                    WebResponse<PaymentResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrors());
                    assertNotNull(response.getData());

                    assertEquals(payment1.getAccountName(), response.getData().getAccountName());
                    assertEquals(payment1.getAccountNumber(), response.getData().getAccountNumber());
                    assertEquals(payment1.getPaymentType(), response.getData().getPaymentType());
                    assertEquals(payment1.getProvider(), response.getData().getProvider());
                    assertEquals(payment1.getExpiry(), response.getData().getExpiry());
                }
        );
    }

    @Test
    void updatePaymentSuccess() throws Exception {
        User user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .email("user@email.com")
                .role(Role.USER)
                .build();
        var jwtToken = jwtService.generateToken(user);
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        Payment payment = Payment.builder()
                .accountName("Account Name")
                .accountNumber("1234567890")
                .paymentType(PaymentType.BANK_TRANSFER)
                .provider("Bank")
                .expiry(new Date())
                .user(user)
                .build();
        userRepository.save(user);
        tokenRepository.save(token);
        paymentRepository.save(payment);

        UpdatePaymentRequest request = UpdatePaymentRequest.builder()
                .accountName("Account Name Updated")
                .accountNumber("987654321")
                .paymentType(PaymentType.BANK_TRANSFER)
                .provider("Bank")
                .expiry(new Date())
                .build();

        mockMvc.perform(
                patch("/api/v1/users/user/payments/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                    WebResponse<PaymentResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrors());
                    assertNotNull(response.getData());

                    assertEquals(request.getAccountName(), response.getData().getAccountName());
                    assertEquals(request.getAccountNumber(), response.getData().getAccountNumber());
                    assertEquals(request.getPaymentType(), response.getData().getPaymentType());
                    assertEquals(request.getProvider(), response.getData().getProvider());
                    assertEquals(request.getExpiry(), response.getData().getExpiry());

                    assertTrue(paymentRepository.existsById(response.getData().getId()));
                }
        );
    }

    @Test
    void deletePaymentSuccess() throws Exception {
        User user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .email("user@email.com")
                .role(Role.USER)
                .build();
        var jwtToken = jwtService.generateToken(user);
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        Payment payment = Payment.builder()
                .id(1)
                .accountName("Account Name")
                .accountNumber("1234567890")
                .paymentType(PaymentType.BANK_TRANSFER)
                .provider("Bank")
                .expiry(new Date())
                .user(user)
                .build();
        userRepository.save(user);
        tokenRepository.save(token);
        paymentRepository.save(payment);

        mockMvc.perform(
                delete("/api/v1/users/user/payments/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrors());
                    assertEquals("OK", response.getData());

                    assertFalse(paymentRepository.existsById(payment.getId()));
                }
        );
    }
}
