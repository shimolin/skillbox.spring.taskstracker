package org.example.taskstracker.service;

import lombok.RequiredArgsConstructor;
import org.example.taskstracker.entity.User;
import org.example.taskstracker.mapper.UserMapper;
import org.example.taskstracker.model.UserRequest;
import org.example.taskstracker.model.UserResponse;
import org.example.taskstracker.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public Flux<User> findAll() {
        return repository.findAll();
    }

    public Mono<Boolean> userIsPresent(String userId) {
        return repository.findById(userId)
                .hasElement();
    }

    public Mono<User> findById(String id) {
        return repository.findById(id);
    }

    public Mono<User> findByUsername(String username){
        return repository.findUserByUsername(username);
    }


    public Flux<User> findAllById(Set<String> id) {
        return repository.findAllById(id);
    }



    public Mono<User> create(UserRequest userRequest) {
        User user = userMapper.requestToUser(userRequest);
        user.setId(UUID.randomUUID().toString());
        return repository.save(user);
    }

    public Mono<User> update(String id, UserRequest userRequest) {

        return findById(id).flatMap(user -> {
            if (userRequest.getUsername() != null) user.setUsername(userRequest.getUsername());
            if (userRequest.getEmail() != null) user.setEmail(userRequest.getEmail());
            if (userRequest.getPassword() != null) user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            if (!userRequest.getRoles().isEmpty()) user.setRoles(userRequest.getRoles());

            return repository.save(user);
        });
    }

    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }
}
