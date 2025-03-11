package hexlet.code.service;

import hexlet.code.dto.taskStatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskStatus.TaskStatusDTO;
import hexlet.code.dto.taskStatus.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceAlreadyExistsException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskStatusService {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskStatusMapper taskStatusMapper;

    public List<TaskStatusDTO> getAllTaskStatuses() {
        return taskStatusRepository.findAll().stream()
                .map(taskStatusMapper::map)
                .toList();
    }

    public TaskStatusDTO getTaskStatusById(Long id) {
        return taskStatusMapper.map(taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaskStatus with id " + id + " not found!")));
    }

    public TaskStatusDTO createTaskStatus(TaskStatusCreateDTO taskStatusDTO) {
        if (taskStatusRepository.findBySlug(taskStatusDTO.getSlug()).isPresent()) {
            throw new ResourceAlreadyExistsException("Status " + taskStatusDTO.getName() + " already exists");
        }

        return taskStatusMapper.map(taskStatusRepository.save(taskStatusMapper.map(taskStatusDTO)));
    }

    public TaskStatusDTO updateTaskStatus(long id, TaskStatusUpdateDTO taskStatusDTO) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaskStatus with id " + id + " not found!"));
        taskStatusMapper.update(taskStatusDTO, taskStatus);
        return taskStatusMapper.map(taskStatusRepository.save(taskStatus));
    }

    public void deleteTaskStatus(long id) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaskStatus with id " + id + " not found!"));
        // Если статус связан хотя бы с одной задачей, его нельзя удалить.
        if (!taskStatus.getTasks().isEmpty()) {
            throw new RuntimeException("Can't delete status, because it has tasks");
        }
        taskStatusRepository.delete(taskStatus);
    }
}
