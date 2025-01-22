package org.example.taskstracker.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.taskstracker.model.TaskRequest;
import org.example.taskstracker.model.TaskResponse;
import org.example.taskstracker.security.UserPrincipal;
import org.example.taskstracker.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_USER')")
    public Flux<TaskResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_USER')")
    public Mono<ResponseEntity<TaskResponse>> findById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_MANAGER')")
    public Mono<ResponseEntity<TaskResponse>> create(@AuthenticationPrincipal UserPrincipal userDetails, @Valid @RequestBody TaskRequest request) {
        return service.create(request, userDetails)
                .map(ResponseEntity::ok);
    }

//    @PostMapping("/checkRequest")
//    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_USER')")
//    public Mono<Boolean> checkRequest(@RequestBody TaskRequest request) {
//        return service.checkRequestIds(request);
//
//    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER')")
    public Mono<ResponseEntity<TaskResponse>> update(@PathVariable String id, @RequestBody TaskRequest request) {
        return service.update(id, request)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{taskId}/addObserver")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_USER')")
    public Mono<ResponseEntity<TaskResponse>> addObserver(@PathVariable String taskId, @RequestParam String observerId) {
        return service.addObserver(taskId, observerId)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER')")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable String id) {
        return service.deleteById(id)
                .map(ResponseEntity::ok);
    }


}
