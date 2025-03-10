package hexlet.code.dto.task;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskParamsDTO {
    private String titleCont; // название задачи содержит подстроку
    private Long assigneeId; // идентификатор исполнителя задачи
    private String status; // слаг статуса задачи
    private Long labelId; // идентификатор метки задачи
}
