package hexlet.code.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskCreateDTO {

    private int index;

    private long assignee_id;

    @NotBlank
    @Size(min = 1)
    private String title;

    private String content;

    @NotBlank
    private String status;
}
