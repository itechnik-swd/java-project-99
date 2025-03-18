package hexlet.code.component;


import com.nimbusds.jose.util.Pair;
import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.CustomUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
// отключить DataInitializer на время тестов
//@Profile("!test")
// Класс DataInitializer используется для инициализации базы данных при старте приложения
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private CustomUserDetailsService userService;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var user = new User();
        user.setEmail("hexlet@example.com");
        user.setPasswordDigest("qwerty");
        userService.createUser(user);

        //В приложении уже доступны дефолтные статусы со слагами: draft, to_review, to_be_fixed, to_publish, published
        List<Pair<String, String>> statuses = List.of(
                Pair.of("draft", "Draft"),
                Pair.of("to_review", "ToReview"),
                Pair.of("to_be_fixed", "ToBeFixed"),
                Pair.of("to_publish", "ToPublish"),
                Pair.of("published", "Published")
        );
        statuses.stream()
                .map(status -> {
                    var taskStatus = new TaskStatus();
                    taskStatus.setName(status.getRight());
                    taskStatus.setSlug(status.getLeft());
                    return taskStatus;
                })
                .forEach(taskStatusRepository::save);

        // при старте в приложении уже доступны дефолтные метки с названиями feature, bug.
        var feature = new Label();
        feature.setName("feature");
        labelRepository.save(feature);

        var bug = new Label();
        bug.setName("bug");
        labelRepository.save(bug);
    }
}
