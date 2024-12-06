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
import reactor.core.publisher.MonoSink;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserService userService;

    public Flux<TaskModelResponse> findAll() {
        return taskRepository.findAll()
                .flatMap(taskMapper::enrich)
                .map(taskMapper::taskToTaskModelResponse);
    }

    public Mono<TaskModelResponse> findById(String id) {
        return taskRepository.findById(id)
                .flatMap(taskMapper::enrich)
                .map(taskMapper::taskToTaskModelResponse);
    }

    public Mono<TaskModelResponse> create(TaskModelRequest request) {

        //TODO проверить id пользователей в request на существование в таблице users

        return checkRequestIds(request).flatMap(requestIsValid ->{

            if (requestIsValid){
                Task task = taskMapper.taskModelRequestToTask(request);
                task.setId(UUID.randomUUID().toString());
                task.setCreatedAt(Instant.now());
                Mono<Task> taskMono = Mono.just(task);
                return taskMono.flatMap(taskRepository::save)
                        .flatMap(taskMapper::enrich)
                        .map(taskMapper::taskToTaskModelResponse);
            }
            else {
                return Mono.empty();
            }
        });

//        Task task = taskMapper.taskModelRequestToTask(request);
//        task.setId(UUID.randomUUID().toString());
//        task.setCreatedAt(Instant.now());
//        Mono<Task> taskMono = Mono.just(task);
//        return taskMono.flatMap(taskRepository::save)
//                .flatMap(taskMapper::enrich)
//                .map(taskMapper::taskToTaskModelResponse);
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
            return taskMono.flatMap(taskRepository::save)
                    .flatMap(taskMapper::enrich)
                    .map(taskMapper::taskToTaskModelResponse);
        });
    }

    public Mono<TaskModelResponse> addObserver(String taskId, String observerId) {
        return taskRepository.findById(taskId).flatMap(taskForUpdate -> {
            if (observerId != null) taskForUpdate.getObserverIds().add(observerId);
            Mono<Task> taskMono = Mono.just(taskForUpdate);
            return taskMono.flatMap(taskRepository::save)
                    .flatMap(taskMapper::enrich)
                    .map(taskMapper::taskToTaskModelResponse);
        });
    }

    public Mono<Void> deleteById(String id) {
        return taskRepository.deleteById(id);
    }

    public Mono<Boolean> checkRequestIds(TaskModelRequest request) {

        Mono<Boolean> authorIdIsValid = Mono.just(true);
        if (request.getAuthorId() != null) {
            authorIdIsValid = userService.findById(request.getAuthorId()).hasElement();
        }

        Mono<Boolean> assigneeIdIsValid = Mono.just(true);
        if (request.getAssigneeId() != null) {
            assigneeIdIsValid = userService.findById(request.getAssigneeId()).hasElement();
        }

        Mono<List<Boolean>> observerIdsIsValid = Mono.just(new ArrayList<>());

        if (request.getObserverIds() != null){
            observerIdsIsValid = Flux.fromIterable(request.getObserverIds()).flatMap(s->{
                return userService.findById(s).hasElement();
            }).collectList();

        }

        return Mono.zip(authorIdIsValid, assigneeIdIsValid, observerIdsIsValid).flatMap(data -> {
            boolean result = data.getT1() && data.getT2();
            for(Boolean  b :data.getT3()){
                result = result && b;
            }
            return Mono.just(result);
        });
    }
}
