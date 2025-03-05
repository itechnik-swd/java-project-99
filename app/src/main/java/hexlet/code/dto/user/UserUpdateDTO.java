package hexlet.code.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Setter
@Getter
public class UserUpdateDTO {

    @Email
    private String email;

    @Size(min = 3)
    private String password;

    @NotNull
    private JsonNullable<String> firstName;

    @NotNull
    private JsonNullable<String> lastName;
}
