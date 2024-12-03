package org.example.taskstracker.service;

import lombok.RequiredArgsConstructor;
import org.example.taskstracker.entity.Task;
import org.example.taskstracker.mapper.TaskMapper;
import org.example.taskstracker.model.TaskModelRequest;
import org.example.taskstracker.model.TaskModelResponse;
import org.example.taskstracker.repository.TaskRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final TaskMapper taskMapper;

    public Flux<TaskModelResponse> findAll() {
        return taskRepository.findAll()
                .flatMap(task -> taskMapper.enrich(task)
                        .map(taskMapper::taskToTaskModelResponse)
                );
    }

    public Mono<TaskModelResponse> findById(String id) {
        return taskRepository.findById(id)
                .flatMap(task -> taskMapper.enrich(task)
                        .map(taskMapper::taskToTaskModelResponse));
    }

    public Mono<TaskModelResponse> create(TaskModelRequest request) {
        Task task = taskMapper.taskModelRequestToTask(request);
        task.setId(UUID.randomUUID().toString());
        task.setCreatedAt(Instant.now());
        Mono<Task> taskMono = Mono.just(task);
        return taskMono.flatMap(t -> taskRepository.save(t).map(taskMapper::taskToTaskModelResponse));
    }

    public Mono<TaskModelResponse> update(String id, TaskModelRequest request) {
        return taskRepository.findById(id).flatMap(taskForUpdate -> {
            if (request.getName() != null) taskForUpdate.setName(request.getName());
            if (request.getDescription() != null) taskForUpdate.setDescription(request.getDescription());
            if (request.getStatus() != null) taskForUpdate.setStatus(request.getStatus());
            if (request.getAuthorId() != null) taskForUpdate.setAuthorId(request.getAuthorId());
            if (request.getAssigneeId() != null) taskForUpdate.setAssigneeId(request.getAssigneeId());
            if (request.getObserverIds() != null) taskForUpdate.setObserverIds(request.getObserverIds());
            taskForUpdate.setUpdatedAt(Instant.now());
            Mono<Task> taskMono = Mono.just(taskForUpdate);
            return taskMono.flatMap(t -> taskRepository.save(t).map(taskMapper::taskToTaskModelResponse));
        });
    }

    public Mono<TaskModelResponse> addObserver(String taskId, String observerId) {
        return taskRepository.findById(taskId).flatMap(taskForUpdate -> {
            if (observerId != null) taskForUpdate.getObserverIds().add(observerId);
            Mono<Task> taskMono = Mono.just(taskForUpdate);
            return taskMono.flatMap(task -> taskRepository.save(task).map(taskMapper::taskToTaskModelResponse));
        });
    }

    public Mono<Void> deleteById(String id) {
        return taskRepository.deleteById(id);
    }

}
