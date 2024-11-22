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
        return taskRepository.findAll().flatMap(task -> {
            Mono<UserModel> author = userService.findById(task.getAuthorId());
            Mono<UserModel> assignee = userService.findById(task.getAssigneeId());
            Mono<List<UserModel>> observers = userService.findAllById(task.getObserverIds()).collectList();

            Mono<Task> taskMono = Mono.zip(author, assignee, observers).flatMap(data -> {
                task.setAuthor(User.from(data.getT1()));
                task.setAssignee(User.from(data.getT2()));
                data.getT3().forEach(um -> {
                    task.getObservers().add(User.from(um));
                });
                return Mono.just(task);
            });
            return taskMono.map(TaskModelResponse::from);
        });
    }

    public Mono<TaskModelResponse> findById(String id) {
        return taskRepository.findById(id).flatMap(task -> {
            Mono<UserModel> author = userService.findById(task.getAuthorId());
            Mono<UserModel> assignee = userService.findById(task.getAssigneeId());
            Mono<List<UserModel>> observers = userService.findAllById(task.getObserverIds()).collectList();

            Mono<Task> taskMono = Mono.zip(author, assignee, observers).flatMap(data -> {
                task.setAuthor(User.from(data.getT1()));
                task.setAssignee(User.from(data.getT2()));
                data.getT3().forEach(um -> {
                    task.getObservers().add(User.from(um));
                });
                return Mono.just(task);
            });
            return taskMono.map(TaskModelResponse::from);
        });
    }

    public Mono<TaskModelResponse> create(TaskModelRequest request) {
        Task task = Task.fromRequest(request);
        task.setId(UUID.randomUUID().toString());
        task.setCreatedAt(Instant.now());
        task.setUpdatedAt(Instant.now());
//        task.setAuthorId(request.getAuthorId());
//        task.setAssigneeId(request.getAssigneeId());
//        task.setObserverIds(request.getObserverIds());

        Mono<UserModel> author = userService.findById(request.getAuthorId());
        Mono<UserModel> assignee = userService.findById(request.getAssigneeId());
        Mono<List<UserModel>> observers = userService.findAllById(request.getObserverIds()).collectList();

        Mono<Task> taskMono = Mono.zip(author, assignee, observers).flatMap(data -> {
            task.setAuthor(User.from(data.getT1()));
            task.setAssignee(User.from(data.getT2()));
            data.getT3().forEach(um -> {
                task.getObservers().add(User.from(um));
            });
            return Mono.just(task);
        });

        return taskMono.flatMap(t -> taskRepository.save(t).map(TaskModelResponse::from));
    }

    public Mono<TaskModelResponse> update(String id, TaskModelRequest request) {
        return taskRepository.findById(id).flatMap(taskForUpdate -> {

            if (request.getName() != null) taskForUpdate.setName(request.getName());
            if (request.getDescription() != null) taskForUpdate.setDescription(request.getDescription());
            if (request.getStatus() != null) taskForUpdate.setStatus(request.getStatus());
            taskForUpdate.setUpdatedAt(Instant.now());

            Mono<UserModel> author = request.getAuthorId() != null ?
                    userService.findById(request.getAuthorId()) : Mono.just(new UserModel());
            Mono<UserModel> assignee = request.getAssigneeId() != null ?
                    userService.findById(request.getAssigneeId()) : Mono.just(new UserModel());
            Mono<List<UserModel>> observers = request.getObserverIds() != null ?
                    userService.findAllById(request.getObserverIds()).collectList() : Mono.just(new ArrayList<>());

            Mono<Task> taskMono = Mono.zip(author, assignee, observers).flatMap(data -> {
                if (request.getAuthorId() != null) {
                    taskForUpdate.setAuthorId(data.getT1().getId());
                    taskForUpdate.setAuthor(User.from(data.getT1()));
                }
                if (request.getAssigneeId() != null) {
                    taskForUpdate.setAssigneeId(data.getT2().getId());
                    taskForUpdate.setAssignee(User.from(data.getT2()));
                }
                if (request.getObserverIds() != null) {
                    taskForUpdate.getObserverIds().clear();
                    taskForUpdate.getObservers().clear();
                    data.getT3().forEach(um -> {
                        taskForUpdate.getObserverIds().add(um.getId());
                        taskForUpdate.getObservers().add(User.from(um));
                    });
                }
                return Mono.just(taskForUpdate);
            });

            return taskMono.flatMap(t -> taskRepository.save(t).map(TaskModelResponse::from));
        });
    }

    public Mono<Void> deleteById(String id) {
        return taskRepository.deleteById(id);
    }

}
