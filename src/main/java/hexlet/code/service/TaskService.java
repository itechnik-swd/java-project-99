package hexlet.code.service;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskParamsDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.specification.TaskSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskSpecification taskSpecification;

    @Autowired
    private LabelRepository labelRepository;

    public List<TaskDTO> getAllTasks(TaskParamsDTO params) {
        var spec = taskSpecification.build(params);
        return taskRepository.findAll(spec).stream().map(taskMapper::map).toList();
    }

    public TaskDTO getTaskById(Long id) {
        return taskMapper.map(taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id=%d not found".formatted(id))));
    }

    public TaskDTO createTask(TaskCreateDTO taskCreateDTO) {
        Task task = taskMapper.map(taskCreateDTO); // Task map(TaskCreateDTO dto);
        taskRepository.save(task);
        return taskMapper.map(task); // TaskDTO map(Task model)
    }

    public TaskDTO updateTask(Long id, TaskUpdateDTO taskUpdateDTO) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id=%d not found".formatted(id)));
        taskMapper.update(taskUpdateDTO, task);
        return taskMapper.map(taskRepository.save(task));
    }

    public void deleteTask(Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id=%d not found".formatted(id)));
        taskRepository.delete(task);
    }
}
