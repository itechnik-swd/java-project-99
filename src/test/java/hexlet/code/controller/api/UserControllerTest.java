package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
// Чтобы отключить DataInitializer на время тестов
//@ActiveProfiles("test")
class UserControllerTest {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper mapper;

    @Autowired
    private ModelGenerator modelGenerator;

    private User testUser;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);
    }

    @Test
    public void testIndex() throws Exception {
        var request = get("/api/users").with(jwt());
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testIndexWithoutAuth() throws Exception {
        var request = get("/api/users");
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/users/" + testUser.getId()).with(jwt());
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("email").isEqualTo(testUser.getEmail()),
                v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
                v -> v.node("lastName").isEqualTo(testUser.getLastName()));
    }

    @Test
    public void testShowWithoutAuth() throws Exception {
        var request = get("/api/users/" + testUser.getId());
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testCreate() throws Exception {
        var data = Instancio.of(modelGenerator.getUserModel()).create();

        var request = post("/api/users").with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var user = userRepository.findByEmail(data.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        assertThat(user).isNotNull();
        assertThat(user.getFirstName()).isEqualTo(data.getFirstName());
        assertThat(user.getLastName()).isEqualTo(data.getLastName());
        assertThat(user.getEmail()).isEqualTo(data.getEmail());
        assertThat(user.getPasswordDigest()).isNotEqualTo(data.getPasswordDigest());
    }

    @Test
    public void testCreateWithoutAuth() throws Exception {
        var request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(testUser));
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testUpdate() throws Exception {
        var data = new HashMap<>();
        data.put("firstName", "Mike");

        var request = put("/api/users/" + testUser.getId()).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var user = userRepository.findByEmail(testUser.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        assertThat(user).isNotNull();
        assertThat(user.getFirstName()).isEqualTo(data.get("firstName"));
    }

    @Test
    public void testUpdateWithoutAuth() throws Exception {
        var data = new HashMap<>();
        data.put("firstName", "Mike");

        var request = put("/api/users/" + testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testDelete() throws Exception {
        var request = delete("/api/users/" + testUser.getId()).with(jwt());
        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        var user = userRepository.findByEmail(testUser.getEmail());
        assertThat(user).isEmpty();
    }

    @Test
    public void testDeleteWithoutAuth() throws Exception {
        var request = delete("/api/users/" + testUser.getId());
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }
}
