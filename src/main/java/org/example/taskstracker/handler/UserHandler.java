package org.example.taskstracker.handler;

import lombok.extern.slf4j.Slf4j;
import org.example.taskstracker.entity.User;
import org.example.taskstracker.model.UserModel;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@Slf4j
public class UserHandler {

    public Mono<ServerResponse> getAllUsers(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Flux.just(
                        new UserModel(),
                        new UserModel()
                ), UserModel.class);
    }

    public Mono<ServerResponse> findById(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(
                        new UserModel()
                ), UserModel.class);
    }

    public Mono<ServerResponse> createUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserModel.class)
                .flatMap(user -> {
                    log.info("User for create: {}", user);
                    return Mono.just(user);
                })
                .flatMap(user -> ServerResponse.created(URI.create("/api/v1/functions/user" + user.getId())).build());
    }

    public Mono<ServerResponse> errorRequest(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.error(new RuntimeException("Exception in errorRequest")), String.class)
                .onErrorResume(ex -> {
                    log.error("Error in errorRequest", ex);
                    return ServerResponse.badRequest().body(Mono.error(ex), String.class);
                });
    }
}
