package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.util.ModelGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LabelControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LabelMapper labelMapper;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    private User testUser;
    private TaskStatus testTaskStatus;
    private Task testTask;
    private Label testLabel;

    @BeforeEach
    public void setUp() {
        labelRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

//        testUser = Instancio.of(User.class)
//                .ignore(Select.field(User::getId))
//                .ignore(Select.field(User::getTasks))
//                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
//                .supply(Select.field(User::getPasswordDigest), () -> faker.internet().password(3, 16))
//                .create();
//
//        testTaskStatus = Instancio.of(TaskStatus.class)
//                .ignore(Select.field(TaskStatus::getId))
//                .ignore(Select.field(TaskStatus::getTasks))
//                .supply(Select.field(TaskStatus::getName), () -> faker.name().title())
//                .supply(Select.field(TaskStatus::getSlug), () -> faker.lorem().word())
//                .create();
//
//        testTask = Instancio.of(Task.class)
//                .ignore(Select.field(Task::getId))
//                .ignore(Select.field(TaskStatus::getTasks))
//                .ignore(Select.field(Task::getAssignee))
//                .ignore(Select.field(Task::getLabels))
//                .supply(Select.field(Task::getName), () -> faker.name().title())
//                .supply(Select.field(Task::getIndex), () -> faker.number().numberBetween(1, 10))
//                .supply(Select.field(Task::getDescription), () -> faker.lorem().sentence())
//                .supply(Select.field(Task::getTaskStatus), () -> testTaskStatus)
//                .create();
//
//        testLabel = Instancio.of(Label.class)
//                .ignore(Select.field(Label::getId))
//                .ignore(Select.field(Label::getTasks))
//                .supply(Select.field(Label::getName), () -> faker.name().title())
//                .create();
    }

    @Test
    public void testIndexWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/labels"))
                .andExpect(status().isUnauthorized());
    }
}
