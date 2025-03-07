package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Setter
@Getter
public class TaskUpdateDTO {

    @NotNull
    private JsonNullable<String> title;

    @NotNull
    private JsonNullable<Integer> index;

    @NotNull
    private JsonNullable<String> content;

    @NotNull
    private JsonNullable<String> status;

    @NotNull
    @JsonProperty("assignee_id")
    private JsonNullable<Long> assigneeId;
}
