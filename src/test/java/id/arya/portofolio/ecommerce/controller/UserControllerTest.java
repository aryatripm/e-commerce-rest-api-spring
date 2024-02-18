package id.arya.portofolio.ecommerce.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.arya.portofolio.ecommerce.config.JwtService;
import id.arya.portofolio.ecommerce.token.Token;
import id.arya.portofolio.ecommerce.token.TokenRepository;
import id.arya.portofolio.ecommerce.user.*;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        tokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getCurrentUserSuccess() throws Exception {
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
                get("/api/v1/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                    WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrors());
                    assertNotNull(response.getData());

                    assertEquals(user.getUsername(), response.getData().getUsername());
                    assertEquals(user.getEmail(), response.getData().getEmail());
                }
        );
    }

    @Test
    void changePasswordFailedOldPasswordNotSame() throws Exception {
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

        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword("diff_password")
                .newPassword("new_password")
                .confirmPassword("confirm_password")
                .build();

        mockMvc.perform(
                patch("/api/v1/users/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
                    WebResponse<ChangePasswordResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrors());
                }
        );
    }

    @Test
    void changePasswordFailedNewPasswordNotSame() throws Exception {
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

        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword("password")
                .newPassword("new_password")
                .confirmPassword("confirm_password")
                .build();

        mockMvc.perform(
                patch("/api/v1/users/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                    WebResponse<ChangePasswordResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrors());
                }
        );
    }

    @Test
    void changePasswordSuccess() throws Exception {
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

        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword("password")
                .newPassword("new_password")
                .confirmPassword("new_password")
                .build();

        mockMvc.perform(
                patch("/api/v1/users/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                    WebResponse<ChangePasswordResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrors());
                    assertNotNull(response.getData());
                }
        );
    }

    @Test
    void getAllUsersSuccess() throws Exception {
        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                .email("admin@email.com")
                .role(Role.ADMIN)
                .build();
        User user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .email("user@email.com")
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
        userRepository.save(user);
        tokenRepository.save(token);

        mockMvc.perform(
                get("/api/v1/users/")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                    WebResponse<List<UserResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrors());
                    assertNotNull(response.getData());
                    assertNotNull(response.getPaging());
                    assertEquals(1, response.getData().size());
                }
        );
    }

    @Test
    void getAllUsersSuccessWithQuery() throws Exception {
        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                .email("admin@email.com")
                .role(Role.ADMIN)
                .build();
        User user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .email("user@email.com")
                .role(Role.USER)
                .build();
        User user2 = User.builder()
                .username("arya")
                .password(passwordEncoder.encode("password"))
                .email("arya@email.com")
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
        userRepository.save(user);
        userRepository.save(user2);
        tokenRepository.save(token);

        mockMvc.perform(
                get("/api/v1/users/")
                        .queryParam("query", "user")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                    WebResponse<List<UserResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrors());
                    assertNotNull(response.getData());
                    assertNotNull(response.getPaging());
                    assertEquals(1, response.getData().size());
                    assertEquals(user.getUsername(), response.getData().getFirst().getUsername());
                }
        );
    }

    @Test
    void getAllUsersFailedUnauthorized() throws Exception {
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
                get("/api/v1/users/")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
        ).andExpectAll(
                status().isForbidden()
        );
    }

    @Test
    void getOneUserSuccess() throws Exception {
        User user = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                .email("admin@email.com")
                .role(Role.ADMIN)
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
                get("/api/v1/users/admin")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                    WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrors());
                    assertNotNull(response.getData());
                    assertEquals(user.getUsername(), response.getData().getUsername());
                }
        );
    }

    @Test
    void getOneUserFailedNotFound() throws Exception {
        User user = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                .email("admin@email.com")
                .role(Role.ADMIN)
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
                get("/api/v1/users/user")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                    WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrors());
                    assertNull(response.getData());
                }
        );
    }
}