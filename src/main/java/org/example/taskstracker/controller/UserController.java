package org.example.taskstracker.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskstracker.mapper.UserMapper;
import org.example.taskstracker.model.UserRequest;
import org.example.taskstracker.model.UserResponse;
import org.example.taskstracker.publisher.UserUpdatesPublisher;
import org.example.taskstracker.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService service;
    private final UserUpdatesPublisher publisher;
    private final UserMapper userMapper;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_USER')")
    public Flux<UserResponse> findAll() {
        return service.findAll().map(userMapper::userToResponse);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_USER')")
    public Mono<ResponseEntity<UserResponse>> findById(@PathVariable String id) {
        return service.findById(id)
                .map(userMapper::userToResponse)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/isPresent/{id}")
    public Mono<Boolean> isPresent(@PathVariable String id) {
        return service.userIsPresent(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_USER')")
    public Mono<ResponseEntity<UserResponse>> create(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody UserRequest model) {
        return service.create(model)
                .map(userMapper::userToResponse)
                .doOnSuccess(publisher::publish)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_USER')")
    public Mono<ResponseEntity<UserResponse>> update(@PathVariable String id, @RequestBody UserRequest model) {
        return service.update(id, model)
                .map(userMapper::userToResponse)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_USER')")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable String id) {
        return service.deleteById(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<UserResponse>> getUserUpdates() {
        return publisher.getUpdateSink()
                .asFlux()
                .map(user -> ServerSentEvent.builder(user).build());
    }

}
