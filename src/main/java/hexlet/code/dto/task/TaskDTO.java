package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class TaskDTO {

    private Long id;

    private int index;

    private LocalDate createdAt;

    @JsonProperty("assignee_id")
    private long assigneeId;

    private String title;

    private String content;

    private String status;

    private Set<Long> taskLabelIds;
}
