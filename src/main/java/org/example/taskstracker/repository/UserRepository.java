package org.example.taskstracker.repository;

import org.example.taskstracker.entity.User;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {

}
