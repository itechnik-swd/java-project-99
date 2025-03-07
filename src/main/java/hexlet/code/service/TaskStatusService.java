package hexlet.code.service;

import hexlet.code.dto.taskStatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskStatus.TaskStatusDTO;
import hexlet.code.dto.taskStatus.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceAlreadyExistsException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskStatusService {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskStatusMapper taskStatusMapper;

    public ResponseEntity<List<TaskStatusDTO>> getAllTaskStatuses() {
        var taskStatuses = taskStatusRepository.findAll().stream()
                .map(taskStatusMapper::map)
                .toList();

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(taskStatusRepository.count()))
                .body(taskStatuses);
    }

    public TaskStatusDTO getTaskStatusById(Long id) {
        return taskStatusMapper.map(taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaskStatus not found")));
    }

    public TaskStatusDTO createTaskStatus(TaskStatusCreateDTO taskStatusDTO) {
        if (taskStatusRepository.findBySlug(taskStatusDTO.getSlug()).isPresent()) {
            throw new ResourceAlreadyExistsException("Status " + taskStatusDTO.getName() + " already exists");
        }

        return taskStatusMapper.map(taskStatusRepository.save(taskStatusMapper.map(taskStatusDTO)));
    }

    public TaskStatusDTO updateTaskStatus(long id, TaskStatusUpdateDTO taskStatusDTO) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaskStatus not found"));
        taskStatusMapper.update(taskStatusDTO, taskStatus);
        return taskStatusMapper.map(taskStatusRepository.save(taskStatus));
    }

    public void deleteTaskStatus(long id) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaskStatus not found"));
        taskStatusRepository.delete(taskStatus);
    }
}
