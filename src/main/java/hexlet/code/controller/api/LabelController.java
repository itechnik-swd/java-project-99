package hexlet.code.controller.api;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.service.LabelService;
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
@RequestMapping("/api/labels")

public class LabelController {

    @Autowired
    private LabelService labelService;

    @GetMapping(path = "")
    @ResponseStatus(HttpStatus.OK)
    // просматривать метки могут только аутентифицированные пользователи
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<LabelDTO>> index() {
        var labels = labelService.getAllLabels();

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(labels.size()))
                .body(labels);
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    // просматривать метку могут только аутентифицированные пользователи
    @PreAuthorize("isAuthenticated()")
    public LabelDTO show(@PathVariable long id) {
        return labelService.getLabelById(id);
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    // добавлять метку могут только аутентифицированные пользователи
    @PreAuthorize("isAuthenticated()")
    public LabelDTO create(@Valid @RequestBody LabelCreateDTO labelCreateDTO) {
        return labelService.createLabel(labelCreateDTO);
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    // обновлять метку могут только аутентифицированные пользователи
    @PreAuthorize("isAuthenticated()")
    public LabelDTO update(@PathVariable long id, @Valid @RequestBody LabelUpdateDTO labelUpdateDTO) {
        return labelService.updateLabel(id, labelUpdateDTO);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    // удалять метку могут только аутентифицированные пользователи
    @PreAuthorize("isAuthenticated()")
    public void delete(@PathVariable long id) {
        labelService.deleteLabel(id);
    }
}
