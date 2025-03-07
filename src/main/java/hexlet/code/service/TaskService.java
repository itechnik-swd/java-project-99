package hexlet.code.service;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.exception.ResourceAlreadyExistsException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskMapper taskMapper;

    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        var tasks = taskRepository.findAll().stream()
                .map(taskMapper::map)
                .toList();

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(taskRepository.count()))
                .body(tasks);
    }

    public TaskDTO getTaskById(Long id) {
        return taskMapper.map(taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found")));
    }

    public TaskDTO createTask(TaskCreateDTO taskDTO) {
        if (taskRepository.findByTitle(taskDTO.getTitle()).isPresent()) {
            throw new ResourceAlreadyExistsException("Task " + taskDTO.getTitle() + " already exists");
        }

        return taskMapper.map(taskRepository.save(taskMapper.map(taskDTO)));
    }

    public TaskDTO updateTask(Long id, TaskUpdateDTO taskUpdateDTO) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        taskMapper.update(taskUpdateDTO, task);
        return taskMapper.map(taskRepository.save(task));
    }

    public void deleteTask(Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        taskRepository.delete(task);
    }
}
