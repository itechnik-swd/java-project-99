package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.task.TaskParamsDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.handler.GlobalExceptionHandler;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.Set;

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
public class TaskControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository statusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskMapper taskMapper;

    private Task testTask;

    private User anotherUser;

    private TaskStatus testTaskStatus;

    private Label testLabel;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        statusRepository.deleteAll();
        labelRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        User testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);

        anotherUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(anotherUser);

        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        statusRepository.save(testTaskStatus);

        testLabel = Instancio.of(modelGenerator.getLabelModel()).create();
        labelRepository.save(testLabel);
        Set<Label> labelSet = Set.of(testLabel);

        testTask = Instancio.of(modelGenerator.getTaskModel()).create();

        testTask.setAssignee(testUser);
        testTask.setTaskStatus(testTaskStatus);
        testTask.setLabels(labelSet);
    }

    @AfterEach
    void tearDown() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        statusRepository.deleteAll();
        labelRepository.deleteAll();
    }

    @Test
    void testIndex() throws Exception {
        taskRepository.save(testTask);

        var result = mockMvc.perform(get("/api/tasks").with(jwt()))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();
    }

    @Test
    public void testIndexWithTitleContains() throws Exception {
        var param = new TaskParamsDTO();
        param.setTitleCont(testTask.getName().substring(1).toLowerCase());
        taskRepository.save(testTask);
        var result = mockMvc.perform(get("/api/tasks?titleCont=" + param.getTitleCont()).with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray().allSatisfy(element ->
                assertThatJson(element)
                        .and(v -> v.node("title").asString().containsIgnoringCase(testTask.getName()))
        );
    }

    @Test
    public void testIndexWithAssigneeId() throws Exception {
        var param = new TaskParamsDTO();
        param.setAssigneeId(anotherUser.getId());
        taskRepository.save(testTask);

        var result = mockMvc.perform(get("/api/tasks?assigneeId=" + param.getAssigneeId()).with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray().allSatisfy(element ->
                assertThatJson(element)
                        .and(v -> v.node("assigneeId").isEqualTo(testTask.getAssignee().getId()))
        );
    }

    @Test
    public void testIndexWithStatus() throws Exception {
        var param = new TaskParamsDTO();
        param.setStatus(testTaskStatus.getSlug());
        taskRepository.save(testTask);

        var request = get("/api/tasks?status=" + param.getStatus()).with(jwt());
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray().allSatisfy(element ->
                assertThatJson(element)
                        .and(v -> v.node("status").isEqualTo(testTask.getTaskStatus().getSlug()))
        );
    }

    @Test
    public void testIndexWithParams() throws Exception {
        var param = new TaskParamsDTO();
        param.setAssigneeId(anotherUser.getId());
        param.setStatus(testTaskStatus.getSlug());
        param.setLabelId(testLabel.getId());
        param.setTitleCont(testTask.getName().toUpperCase());
        taskRepository.save(testTask);

        var result = mockMvc.perform(get("/api/tasks?titleCont=" + param.getTitleCont()
                        + "&assigneeId=" + param.getAssigneeId()
                        + "&status=" + param.getStatus()
                        + "&taskLabelIds=" + param.getLabelId()).with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray().allSatisfy(element ->
                assertThatJson(element)
                        .and(v -> v.node("title").isEqualTo(testTask.getName()))
                        .and(v -> v.node("status").isEqualTo(testTask.getTaskStatus().getSlug()))
                        .and(v -> v.node("assigneeId").isEqualTo(testTask.getAssignee().getId()))
                        .and(v -> v.node("taskLabelIds").isEqualTo(testTask.getLabels().stream()
                                .map(Label::getId)
                                .filter(id -> labelRepository.findById(testLabel.getId()).isPresent())))
        );
    }

    @Test
    void testShow() throws Exception {
        taskRepository.save(testTask);

        var request = get("/api/tasks/{id}", testTask.getId()).with(jwt());
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("title").isEqualTo(testTask.getName()),
                v -> v.node("content").isEqualTo(testTask.getDescription()),
                v -> v.node("status").isEqualTo(testTask.getTaskStatus().getSlug()),
                v -> v.node("assignee_id").isEqualTo(testTask.getAssignee().getId())
        );
    }

    @Test
    void testCreate() throws Exception {

        var dto = taskMapper.map(testTask);

        var request = post("/api/tasks").with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();
        var task = taskRepository.findByName(testTask.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        assertThat(task).isNotNull();
        assertThat(task.getName()).isEqualTo(testTask.getName());
        assertThat(task.getDescription()).isEqualTo(testTask.getDescription());
        assertThat(task.getAssignee().getId()).isEqualTo(testTask.getAssignee().getId());
        assertThat(task.getTaskStatus().getSlug()).isEqualTo(testTask.getTaskStatus().getSlug());
    }

    @Test
    void testUpdate() throws Exception {
        taskRepository.save(testTask);

        var dto = taskMapper.map(testTask);

        dto.setTitle("new title");
        dto.setContent("new description");
        dto.setStatus(testTaskStatus.getSlug());

        var request = put("/api/tasks/{id}", testTask.getId()).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        mockMvc.perform(request).andExpect(status().isOk());

        var task = taskRepository.findById(testTask.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        assertThat(task.getName()).isEqualTo(dto.getTitle());
        assertThat(task.getDescription()).isEqualTo(dto.getContent());
        assertThat(task.getTaskStatus().getSlug()).isEqualTo(dto.getStatus());
    }

    @Test
    void testDelete() throws Exception {
        taskRepository.save(testTask);
        var request = delete("/api/tasks/{id}", testTask.getId()).with(jwt());
        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertThat(taskRepository.existsById(testTask.getId())).isFalse();
    }
}
