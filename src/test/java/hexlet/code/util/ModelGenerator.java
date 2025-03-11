package hexlet.code.util;

import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;

@Getter
@Component
public class ModelGenerator {
    private Model<User> userModel;
    private Model<TaskStatus> taskStatusModel;
    private Model<Task> taskModel;
    private Model<Label> labelModel;

    @Autowired
    private Faker faker;

    @PostConstruct
    private void init() {
        userModel = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getPasswordDigest), () -> faker.internet().password(3, 16))
                .supply(Select.field(User::getTasks), () -> new ArrayList<>())
                .toModel();

        taskStatusModel = Instancio.of(TaskStatus.class)
                .ignore(Select.field(TaskStatus::getId))
                .supply(Select.field(TaskStatus::getName), () -> faker.name().title())
                .supply(Select.field(TaskStatus::getSlug), () -> faker.lorem().sentence(3))
                .supply(Select.field(TaskStatus::getTasks), () -> new ArrayList<>())
                .toModel();

        taskModel = Instancio.of(Task.class)
                .ignore(Select.field(Task::getId))
                .supply(Select.field(Task::getName), () -> faker.name().title())
                .supply(Select.field(Task::getIndex), () -> faker.number().numberBetween(1, 10))
                .supply(Select.field(Task::getDescription), () -> faker.lorem().sentence())
                .supply(Select.field(Task::getTaskStatus), () -> Instancio.create(taskStatusModel))
                .supply(Select.field(Task::getAssignee), () -> Instancio.create(userModel))
                .supply(Select.field(Task::getLabels), () -> new HashSet<>())
                .toModel();

        labelModel = Instancio.of(Label.class)
                .ignore(Select.field(Label::getId))
                .supply(Select.field(Label::getName), () -> faker.regexify("[a-z]{3,100}"))
                .supply(Select.field(Label::getTasks), () -> new HashSet<>())
                .toModel();
    }
}
