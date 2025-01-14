package org.example.taskstracker.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.taskstracker.model.TaskModelRequest;
import org.example.taskstracker.model.TaskModelResponse;
import org.example.taskstracker.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
                .map(ResponseEntity::ok);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Mono<ResponseEntity<TaskModelResponse>> create(@Valid @RequestBody TaskModelRequest request) {
        return service.create(request)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/checkRequest")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Mono<Boolean> checkRequest(@RequestBody TaskModelRequest request) {
        return service.checkRequestIds(request);

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Mono<ResponseEntity<TaskModelResponse>> update(@PathVariable String id, @Valid @RequestBody TaskModelRequest request) {
        return service.update(id, request)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{taskId}/addObserver")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Mono<ResponseEntity<TaskModelResponse>> addObserver(@PathVariable String taskId, @RequestParam String observerId) {
        return service.addObserver(taskId, observerId)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable String id) {
        return service.deleteById(id)
                .map(ResponseEntity::ok);
    }


}
