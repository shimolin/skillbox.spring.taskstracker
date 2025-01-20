package org.example.taskstracker.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskstracker.model.UserModel;
import org.example.taskstracker.publisher.UserUpdatesPublisher;
import org.example.taskstracker.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
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

    @GetMapping
    public Flux<UserModel> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserModel>> findById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/isPresent/{id}")
    public Mono<Boolean> isPresent(@PathVariable String id) {
        return service.userIsPresent(id);
    }

    @PostMapping
    public Mono<ResponseEntity<UserModel>> create(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody UserModel model) {
        return service.create(model)
                .doOnSuccess(publisher::publish)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<UserModel>> update(@PathVariable String id, @RequestBody UserModel model) {
        return service.update(id, model)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable String id) {
        return service.deleteById(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<UserModel>> getUserUpdates() {
        return publisher.getUpdateSink()
                .asFlux()
                .map(user -> ServerSentEvent.builder(user).build());
    }

}
