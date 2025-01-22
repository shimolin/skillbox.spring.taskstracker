package org.example.taskstracker.service;

import lombok.RequiredArgsConstructor;
import org.example.taskstracker.entity.Task;
import org.example.taskstracker.mapper.TaskMapper;
import org.example.taskstracker.model.TaskRequest;
import org.example.taskstracker.model.TaskResponse;
import org.example.taskstracker.repository.TaskRepository;
import org.example.taskstracker.security.UserPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserService userService;

    public Flux<TaskResponse> findAll() {
        return taskRepository.findAll()
                .flatMap(taskMapper::enrich)
                .map(taskMapper::taskToTaskResponse);
    }

    public Mono<TaskResponse> findById(String id) {
        return taskRepository.findById(id)
                .flatMap(taskMapper::enrich)
                .map(taskMapper::taskToTaskResponse);
    }

    public Mono<TaskResponse> create(TaskRequest request, UserPrincipal userDetails) {

        return checkRequestIds(request).flatMap(requestIsValid -> {
            if (requestIsValid) {
                Task task = taskMapper.taskRequestToTask(request);
                task.setId(UUID.randomUUID().toString());
                System.out.println(userDetails.getUsername());
                task.setAuthor(userDetails.getUser());
                task.setCreatedAt(Instant.now());
                return Mono.just(task).flatMap(taskRepository::save)
                        .flatMap(taskMapper::enrich)
                        .map(taskMapper::taskToTaskResponse);
            } else {
                return Mono.error(new RuntimeException("Task create error!!! User in request not found !!!"));
            }
        });
    }

    public Mono<TaskResponse> update(String id, TaskRequest request) {

        return checkRequestIds(request).flatMap(requestIsValid -> {
            if (requestIsValid) {
                return taskRepository.findById(id).flatMap(taskForUpdate -> {
                    if (request.getName() != null) taskForUpdate.setName(request.getName());
                    if (request.getDescription() != null) taskForUpdate.setDescription(request.getDescription());
                    if (request.getStatus() != null) taskForUpdate.setStatus(request.getStatus());
//                    if (request.getAuthorId() != null) taskForUpdate.setAuthorId(request.getAuthorId());
                    if (request.getAssigneeId() != null) taskForUpdate.setAssigneeId(request.getAssigneeId());
                    if (request.getObserverIds() != null) taskForUpdate.setObserverIds(request.getObserverIds());
                    taskForUpdate.setUpdatedAt(Instant.now());
                    return Mono.just(taskForUpdate).flatMap(taskRepository::save)
                            .flatMap(taskMapper::enrich)
                            .map(taskMapper::taskToTaskResponse);
                });
            } else {
                return Mono.error(new RuntimeException("Task update error!!! User in request not found !!!"));
            }
        });
    }

    public Mono<TaskResponse> addObserver(String taskId, String observerId) {

        return userService.findById(observerId).hasElement().flatMap(observerIdIsValid -> {
            if (observerIdIsValid) {
                return taskRepository.findById(taskId).flatMap(taskForUpdate -> {
                    if (observerId != null) taskForUpdate.getObserverIds().add(observerId);
                    return Mono.just(taskForUpdate).flatMap(taskRepository::save)
                            .flatMap(taskMapper::enrich)
                            .map(taskMapper::taskToTaskResponse);
                });
            } else {
                return Mono.error(new RuntimeException("Task update error!!! ObserverId not found !!!"));
            }
        });
    }

    public Mono<Void> deleteById(String id) {
        return taskRepository.deleteById(id);
    }

    public Mono<Boolean> checkRequestIds(TaskRequest request) {

        Mono<Boolean> authorIdIsValid = Mono.just(true);
//        if (request.getAuthorId() != null) {
//            authorIdIsValid = userService.findById(request.getAuthorId()).hasElement();
//        }

        Mono<Boolean> assigneeIdIsValid = Mono.just(true);
        if (request.getAssigneeId() != null) {
            assigneeIdIsValid = userService.findById(request.getAssigneeId()).hasElement();
        }

        Mono<List<Boolean>> observerIdsIsValid = Mono.just(new ArrayList<>());

        if (request.getObserverIds() != null) {
            observerIdsIsValid = Flux.fromIterable(request.getObserverIds())
                    .flatMap(s -> userService.findById(s).hasElement()).collectList();

        }

        return Mono.zip(authorIdIsValid, assigneeIdIsValid, observerIdsIsValid).flatMap(data -> {
            boolean result = data.getT1() && data.getT2();
            for (Boolean b : data.getT3()) {
                result = result && b;
            }
            return Mono.just(result);
        });
    }
}
