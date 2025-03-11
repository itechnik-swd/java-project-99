package hexlet.code.service;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskParamsDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.exception.ResourceAlreadyExistsException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
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
        return taskMapper.map(taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found")));
    }

    public TaskDTO createTask(TaskCreateDTO taskDTO) {
        // Проверка на существование задачи с таким названием.
        if (taskRepository.findByName(taskDTO.getTitle()).isPresent()) {
            throw new ResourceAlreadyExistsException("Task " + taskDTO.getTitle() + " already exists");
        }
        // Добавления меток по их id в задачу при её создании.
        var task = taskMapper.map(taskDTO);
        if (taskDTO.getTaskLabelIds() != null) {
            taskDTO.getTaskLabelIds().forEach(labelId -> {
                task.getLabels().add(labelRepository.findById(labelId)
                        .orElseThrow(() -> new RuntimeException("Label not found")));
            });
        }

        return taskMapper.map(taskRepository.save(task));
    }

    public TaskDTO updateTask(Long id, TaskUpdateDTO taskUpdateDTO) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        taskMapper.update(taskUpdateDTO, task);
        return taskMapper.map(taskRepository.save(task));
    }

    public void deleteTask(Long id) {
        var task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        taskRepository.delete(task);
    }
}
