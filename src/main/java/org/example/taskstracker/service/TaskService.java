package org.example.taskstracker.service;

import lombok.RequiredArgsConstructor;
import org.example.taskstracker.entity.Task;
import org.example.taskstracker.entity.User;
import org.example.taskstracker.model.TaskModelRequest;
import org.example.taskstracker.model.TaskModelResponse;
import org.example.taskstracker.model.UserModel;
import org.example.taskstracker.repository.TaskRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;

    public Flux<TaskModelResponse> findAll() {
        return taskRepository.findAll().flatMap(task -> fill(task).map(TaskModelResponse::from));
    }

    public Mono<TaskModelResponse> findById(String id) {
        return taskRepository.findById(id).flatMap(task -> fill(task).map(TaskModelResponse::from));
    }

    public Mono<TaskModelResponse> create(TaskModelRequest request) {
        Task task = Task.fromRequest(request);
        task.setId(UUID.randomUUID().toString());
        task.setCreatedAt(Instant.now());
        Mono<Task> taskMono = fill(task);
        return taskMono.flatMap(t -> taskRepository.save(t).map(TaskModelResponse::from));
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
            return taskMono.flatMap(t -> taskRepository.save(t).map(TaskModelResponse::from));
        });
    }

    public Mono<Void> deleteById(String id) {
        return taskRepository.deleteById(id);
    }

    //TODO move to task.class
    private Mono<Task> fill(Task task) {
        Mono<UserModel> author = userService.findById(task.getAuthorId());
        Mono<UserModel> assignee = userService.findById(task.getAssigneeId());
        Mono<List<UserModel>> observers = userService.findAllById(task.getObserverIds()).collectList();

        return Mono.zip(author, assignee, observers).flatMap(data -> {
            if (task.getAuthorId() != null) task.setAuthor(User.from(data.getT1()));
            if (task.getAssigneeId() != null) task.setAssignee(User.from(data.getT2()));
            if (task.getObserverIds() != null) {
                data.getT3().forEach(um -> {
                    task.getObservers().add(User.from(um));
                });
            }
            return Mono.just(task);
        });
    }


}
