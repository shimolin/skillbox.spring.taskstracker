package org.example.taskstracker.service;

import lombok.RequiredArgsConstructor;
import org.example.taskstracker.entity.Task;
import org.example.taskstracker.mapper.TaskMapper;
import org.example.taskstracker.mapper.UserMapper;
import org.example.taskstracker.model.TaskModelRequest;
import org.example.taskstracker.model.TaskModelResponse;
import org.example.taskstracker.model.UserModel;
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
    private final UserMapper userMapper;
    private final TaskMapper taskMapper;

    public Flux<TaskModelResponse> findAll() {
        return taskRepository.findAll().flatMap(task -> fill(task).map(taskMapper::toTaskModelResponse));
    }

    public Mono<TaskModelResponse> findById(String id) {
        return taskRepository.findById(id).flatMap(task -> fill(task).map(taskMapper::toTaskModelResponse));
    }

    public Mono<TaskModelResponse> create(TaskModelRequest request) {
        Task task = taskMapper.fromTaskModelRequest(request);
        task.setId(UUID.randomUUID().toString());
        task.setCreatedAt(Instant.now());
        Mono<Task> taskMono = fill(task);
        return taskMono.flatMap(t -> taskRepository.save(t).map(taskMapper::toTaskModelResponse));
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
            Mono<Task> taskMono = fill(taskForUpdate);
            return taskMono.flatMap(t -> taskRepository.save(t).map(taskMapper::toTaskModelResponse));
        });
    }

    public Mono<Void> deleteById(String id) {
        return taskRepository.deleteById(id);
    }

    //TODO move to task.class
    private Mono<Task> fill(Task task) {
        Mono<UserModel> author = task.getAuthorId() != null ?  userService.findById(task.getAuthorId()) : Mono.just(new UserModel());
        Mono<UserModel> assignee = task.getAssigneeId() != null ? userService.findById(task.getAssigneeId()) : null;
        Mono<List<UserModel>> observers = task.getObserverIds() != null ? userService.findAllById(task.getObserverIds()).collectList() : Mono.just(new ArrayList<UserModel>());         ;

        return Mono.zip(author, assignee, observers).flatMap(data -> {
            if (task.getAuthorId() != null) task.setAuthor(userMapper.toUser(data.getT1()));
            if (task.getAssigneeId() != null) task.setAssignee(userMapper.toUser(data.getT2()));
            if (task.getObserverIds() != null) {
                data.getT3().forEach(um -> {
                    task.getObservers().add(userMapper.toUser(um));
                });
            }
            return Mono.just(task);
        });
    }


}
