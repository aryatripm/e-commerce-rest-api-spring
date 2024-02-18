package id.arya.portofolio.ecommerce.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.arya.portofolio.ecommerce.address.*;
import id.arya.portofolio.ecommerce.config.JwtService;
import id.arya.portofolio.ecommerce.token.Token;
import id.arya.portofolio.ecommerce.token.TokenRepository;
import id.arya.portofolio.ecommerce.user.Role;
import id.arya.portofolio.ecommerce.user.User;
import id.arya.portofolio.ecommerce.user.UserRepository;
import id.arya.portofolio.ecommerce.user.UserResponse;
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
public class AddressControllerTest {

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
    private AddressRepository addressRepository;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        tokenRepository.deleteAll();
        addressRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createAddressFailedUserNotFound() throws Exception {
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

        CreateAddressRequest request = CreateAddressRequest.builder()
                .address("Jl. Jalan")
                .city("Kota")
                .province("Provinsi")
                .postalCode("12345")
                .phoneNumber("081234567890")
                .build();

        mockMvc.perform(
                post("/api/v1/users/notfound/address")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                    WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrors());
                    assertNull(response.getData());
                }
        );
    }

    @Test
    void createAddressFailedAddressNotCompleted() throws Exception {
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

        CreateAddressRequest request = CreateAddressRequest.builder()
                .city("Kota")
                .province("Provinsi")
                .postalCode("12345")
                .phoneNumber("081234567890")
                .build();

        mockMvc.perform(
                post("/api/v1/users/user/address")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                    WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrors());
                    assertNull(response.getData());
                }
        );
    }

    @Test
    void createAddressSuccess() throws Exception {
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

        CreateAddressRequest request = CreateAddressRequest.builder()
                .address("Jl. Jalan")
                .city("Kota")
                .province("Provinsi")
                .postalCode("12345")
                .phoneNumber("081234567890")
                .build();

        mockMvc.perform(
                post("/api/v1/users/user/address")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                    WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrors());
                    assertNotNull(response.getData());

                    assertEquals(request.getAddress(), response.getData().getAddress());
                    assertEquals(request.getCity(), response.getData().getCity());
                    assertEquals(request.getProvince(), response.getData().getProvince());
                    assertEquals(request.getPostalCode(), response.getData().getPostalCode());
                    assertEquals(request.getPhoneNumber(), response.getData().getPhoneNumber());
                }
        );
    }

    @Test
    void getAllAddressesFailedUserNotFound() throws Exception {
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
                get("/api/v1/users/notfound/address")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                    WebResponse<List<AddressResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrors());
                    assertNull(response.getData());
                }
        );
    }

    @Test
    void getAllAddressesSuccess() throws Exception {
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
        Address address1 = Address.builder()
                .address("Jl. Jalan 1")
                .city("Kota 1")
                .province("Provinsi 1")
                .postalCode("12345")
                .phoneNumber("081234567890")
                .user(user)
                .build();
        Address address2 = Address.builder()
                .address("Jl. Jalan 2")
                .city("Kota 2")
                .province("Provinsi 2")
                .postalCode("12345")
                .phoneNumber("081234567890")
                .user(user)
                .build();
        userRepository.save(user);
        tokenRepository.save(token);
        addressRepository.save(address1);
        addressRepository.save(address2);

        mockMvc.perform(
                get("/api/v1/users/user/address")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                    WebResponse<List<AddressResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrors());
                    assertNotNull(response.getData());
                    assertEquals(2, response.getData().size());
                }
        );
    }

    @Test
    void getAddressFailedUserNotFound() throws Exception {
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
        Address address1 = Address.builder()
                .id(1)
                .address("Jl. Jalan 1")
                .city("Kota 1")
                .province("Provinsi 1")
                .postalCode("12345")
                .phoneNumber("081234567890")
                .user(user)
                .build();
        Address address2 = Address.builder()
                .id(2)
                .address("Jl. Jalan 2")
                .city("Kota 2")
                .province("Provinsi 2")
                .postalCode("12345")
                .phoneNumber("081234567890")
                .user(user)
                .build();
        userRepository.save(user);
        tokenRepository.save(token);
        addressRepository.save(address1);
        addressRepository.save(address2);

        mockMvc.perform(
                get("/api/v1/users/notfound/address/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                    WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrors());
                    assertNull(response.getData());
                }
        );
    }

    @Test
    void getAddressFailedAddressNotFound() throws Exception {
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
        Address address1 = Address.builder()
                .id(1)
                .address("Jl. Jalan 1")
                .city("Kota 1")
                .province("Provinsi 1")
                .postalCode("12345")
                .phoneNumber("081234567890")
                .user(user)
                .build();
        Address address2 = Address.builder()
                .id(2)
                .address("Jl. Jalan 2")
                .city("Kota 2")
                .province("Provinsi 2")
                .postalCode("12345")
                .phoneNumber("081234567890")
                .user(user)
                .build();
        userRepository.save(user);
        tokenRepository.save(token);
        addressRepository.save(address1);
        addressRepository.save(address2);

        mockMvc.perform(
                get("/api/v1/users/user/address/3")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
                    WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrors());
                    assertNull(response.getData());
                }
        );
    }

    @Test
    void getAddressSuccess() throws Exception {
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
        Address address1 = Address.builder()
                .id(1)
                .address("Jl. Jalan 1")
                .city("Kota 1")
                .province("Provinsi 1")
                .postalCode("12345")
                .phoneNumber("081234567890")
                .user(user)
                .build();
        Address address2 = Address.builder()
                .id(2)
                .address("Jl. Jalan 2")
                .city("Kota 2")
                .province("Provinsi 2")
                .postalCode("12345")
                .phoneNumber("081234567890")
                .user(user)
                .build();
        userRepository.save(user);
        tokenRepository.save(token);
        addressRepository.save(address1);
        addressRepository.save(address2);

        mockMvc.perform(
                get("/api/v1/users/user/address/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                    WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrors());
                    assertNotNull(response.getData());

                    assertEquals(address1.getId(), response.getData().getId());
                    assertEquals(address1.getAddress(), response.getData().getAddress());
                    assertEquals(address1.getCity(), response.getData().getCity());
                    assertEquals(address1.getProvince(), response.getData().getProvince());
                    assertEquals(address1.getPostalCode(), response.getData().getPostalCode());
                    assertEquals(address1.getPhoneNumber(), response.getData().getPhoneNumber());
                }
        );
    }

    @Test
    void updateAddressSuccess() throws Exception {
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
        Address address = Address.builder()
                .id(1)
                .address("Jl. Jalan 1")
                .city("Kota 1")
                .province("Provinsi 1")
                .postalCode("12345")
                .phoneNumber("081234567890")
                .user(user)
                .build();
        userRepository.save(user);
        tokenRepository.save(token);
        addressRepository.save(address);

        UpdateAddressRequest request = UpdateAddressRequest.builder()
                .address("Jl. Jalan Updated")
                .city("Kota Updated")
                .province("Provinsi Updated")
                .build();

        mockMvc.perform(
                patch("/api/v1/users/user/address/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                    WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrors());
                    assertNotNull(response.getData());

                    assertEquals(request.getAddress(), response.getData().getAddress());
                    assertEquals(request.getCity(), response.getData().getCity());
                    assertEquals(request.getProvince(), response.getData().getProvince());
                    assertEquals(address.getPostalCode(), response.getData().getPostalCode());
                    assertEquals(address.getPhoneNumber(), response.getData().getPhoneNumber());

                    assertTrue(addressRepository.existsById(response.getData().getId()));
                }
        );
    }

    @Test
    void deleteAddressSuccess() throws Exception {
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
        Address address = Address.builder()
                .id(1)
                .address("Jl. Jalan 1")
                .city("Kota 1")
                .province("Provinsi 1")
                .postalCode("12345")
                .phoneNumber("081234567890")
                .user(user)
                .build();
        userRepository.save(user);
        tokenRepository.save(token);
        addressRepository.save(address);

        mockMvc.perform(
                delete("/api/v1/users/user/address/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrors());
                    assertEquals("OK", response.getData());

                    assertFalse(addressRepository.existsById(address.getId()));
                }
        );
    }
}
