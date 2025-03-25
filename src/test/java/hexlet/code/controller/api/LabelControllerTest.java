package hexlet.code.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.util.ModelGenerator;
import org.assertj.core.api.Assertions;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
@ActiveProfiles("test")
class LabelControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LabelMapper labelMapper;

    private Label testLabel;

    @BeforeEach
    void setUp() {
        labelRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        testLabel = Instancio.of(modelGenerator.getLabelModel()).create();
    }

    @Test
    void testIndex() throws Exception {
        labelRepository.save(testLabel);
        var result = mockMvc.perform(get("/api/labels").with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();

        List<LabelDTO> labelDTOList = om.readValue(body, new TypeReference<>() {
        });
        List<Label> actual = new ArrayList<>();
        labelDTOList.forEach(labelDTO -> {
            actual.add(labelMapper.map(labelDTO));
        });

        List<Label> expected = labelRepository.findAll();
        assertThat(actual.size()).isEqualTo(expected.size());
        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testCreate() throws Exception {
        var data = Instancio.of(modelGenerator.getLabelModel()).create();

        var request = post("/api/labels").with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        var result = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("id").isPresent(),
                v -> v.node("name").isEqualTo(data.getName()),
                v -> v.node("createdAt").isPresent());

        var label = labelRepository.findByName(data.getName()).orElseThrow();
        assertThat(label.getName()).isEqualTo(data.getName());
    }

    @Test
    void testUpdate() throws Exception {
        labelRepository.save(testLabel);
        var name = "update";

        var data = new HashMap<>();
        data.put("name", name);

        var request = put("/api/labels/" + testLabel.getId()).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var label = labelRepository.findById(testLabel.getId()).orElseThrow();
        assertThat(label.getName()).isEqualTo(name);
    }

    @Test
    void testShow() throws Exception {
        labelRepository.save(testLabel);

        var result = mockMvc.perform(get("/api/labels/{id}", testLabel.getId()).with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("id").isPresent(),
                v -> v.node("name").isEqualTo(testLabel.getName())
        );
    }

    @Test
    void testDelete() throws Exception {
        labelRepository.save(testLabel);

        var request = delete("/api/labels/" + testLabel.getId()).with(jwt());
        mockMvc.perform(request)
                .andExpect(status().isNoContent())
                .andReturn();

        var data = labelRepository.findById(testLabel.getId()).orElse(null);

        assertThat(data).isNull();
    }
}
