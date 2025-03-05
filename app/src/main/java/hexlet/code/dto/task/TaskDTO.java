package hexlet.code.dto.task;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TaskDTO {

    private Long id;

    private int index;

    private LocalDate createdAt;

    private long assignee_id;

    private String title;

    private String content;

    private String status;
}
