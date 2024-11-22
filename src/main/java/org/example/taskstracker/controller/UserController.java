package org.example.taskstracker.controller;

import lombok.RequiredArgsConstructor;
import org.example.taskstracker.entity.User;
import org.example.taskstracker.model.UserModel;
import org.example.taskstracker.publisher.UserUpdatesPublisher;
import org.example.taskstracker.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;
    private final UserUpdatesPublisher publisher;

    @GetMapping
    public Flux<UserModel> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Mono<UserModel> findById(@PathVariable String id) {
        return service.findById(id);
    }

    @PostMapping
    public Mono<UserModel> create(@RequestBody UserModel model) {
        return service.create(model)
                .doOnSuccess(publisher::publish);
    }

    @PutMapping("/{id}")
    public Mono<UserModel> update(@PathVariable String id, @RequestBody UserModel model){
        return service.update(id, model);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteById(@PathVariable String id) {
        return service.deleteById(id);
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<UserModel>> getUserUpdates() {
        return publisher.getUpdateSink()
                .asFlux()
                .map(user -> ServerSentEvent.builder(user).build());
    }

}
