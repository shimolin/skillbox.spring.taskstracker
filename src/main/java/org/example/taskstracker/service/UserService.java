package org.example.taskstracker.service;

import lombok.RequiredArgsConstructor;
import org.example.taskstracker.entity.User;
import org.example.taskstracker.mapper.UserMapper;
import org.example.taskstracker.model.UserModel;
import org.example.taskstracker.repository.UserRepository;
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

    public Flux<UserModel> findAll() {
        return repository.findAll()
                .map(userMapper::toUserModel);
    }

    public Mono<Boolean> userIsPresent(String userId) {
        return repository.findById(userId)
                .hasElement();
    }

    public Mono<UserModel> findById(String id) {
        return repository.findById(id)
                .map(userMapper::toUserModel);
    }

    public Mono<User> findByUsername(String username){
        return repository.findUserByUsername(username);
    }


    public Flux<UserModel> findAllById(Set<String> id) {
        return repository.findAllById(id)
                .map(userMapper::toUserModel);
    }



    public Mono<UserModel> create(UserModel model) {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(model.getUsername());
        user.setEmail(model.getEmail());
        return repository.save(user)
                .map(userMapper::toUserModel);
    }

    public Mono<UserModel> update(String id, UserModel model) {

        return findById(id).flatMap(um -> {
            if (model.getUsername() != null) um.setUsername(model.getUsername());
            if (model.getEmail() != null) um.setEmail(model.getEmail());
            return repository.save(userMapper.toUser(um))//User.from(um))
                    .map(userMapper::toUserModel);
        });
    }

    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }
}
