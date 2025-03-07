package hexlet.code.controller.api;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.mapper.ReferenceMapper;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ReferenceMapper referenceMapper;

    @GetMapping(path = "")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<UserDTO>> index() {
        return ResponseEntity.ok(userService.getAllUsers().getBody());
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO show(@PathVariable long id) {
        return userService.getUserById(id);
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@Valid @RequestBody UserCreateDTO userData) {
        return userService.createUser(userData);
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    /*
    При попытке совершить операцию,
    на которую у пользователя нет прав — без авторизации (например, удаление и редактирование другого пользователя),
    должен возвращаться ответ со статусом 403: Forbidden.
     */
    @PreAuthorize("@userUtils.getCurrentUser().id == #id")
    public UserDTO update(@PathVariable long id, @Valid @RequestBody UserUpdateDTO userData) {
        return userService.updateUser(id, userData);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    /*
    При попытке совершить операцию,
    на которую у пользователя нет прав — без авторизации (например, удаление и редактирование другого пользователя),
    должен возвращаться ответ со статусом 403: Forbidden.
     */
    @PreAuthorize("@userUtils.getCurrentUser().id == #id")
    public void delete(@PathVariable long id) {
        // Если пользователь связан хотя бы с одной задачей, его нельзя удалить.
        var user = referenceMapper.toEntity(id, User.class);
        if (!user.getTasks().isEmpty()) {
            throw new RuntimeException("Can't delete user, because it has tasks");
        }
        userService.deleteUser(id);
    }
}
