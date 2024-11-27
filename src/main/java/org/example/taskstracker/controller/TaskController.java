package org.example.taskstracker.controller;

import lombok.RequiredArgsConstructor;
import org.example.taskstracker.entity.Task;
import org.example.taskstracker.model.TaskModelRequest;
import org.example.taskstracker.model.TaskModelResponse;
import org.example.taskstracker.model.UserModel;
import org.example.taskstracker.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService service;

    @GetMapping
    public Flux<TaskModelResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<TaskModelResponse>> findById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{taskId}/author")
    public Mono<UserModel> getTaskAuthor(@PathVariable String taskId){
        return service.getTaskAuthor(taskId);
    }

    @PostMapping
    public Mono<TaskModelResponse> create(@RequestBody TaskModelRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public Mono<TaskModelResponse> update(@PathVariable String id, @RequestBody TaskModelRequest request) {
        return service.update(id, request);
    }

    @PutMapping("/{taskId}/addObserver")
    public Mono<TaskModelResponse> addObserver(@PathVariable String taskId, @RequestParam String observerId){
        return service.addObsever(taskId, observerId);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteById(@PathVariable String id) {
        return service.deleteById(id);
    }


}
