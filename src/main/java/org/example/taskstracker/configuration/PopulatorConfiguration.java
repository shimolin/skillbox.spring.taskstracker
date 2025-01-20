package org.example.taskstracker.configuration;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.taskstracker.entity.Task;
import org.example.taskstracker.entity.User;
import org.example.taskstracker.repository.TaskRepository;
import org.example.taskstracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.util.Arrays;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "populateOnStart", havingValue = "true")
@EnableReactiveMongoRepositories(basePackages = "org.example.taskstracker.repository")
@Slf4j
public class PopulatorConfiguration {

    @Value("classpath:users.json")
    private Resource userResource;

    @Value("classpath:tasks.json")
    private Resource taskResource;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Bean
    public Disposable userpopulator(ObjectMapper objectMapper) {
        return Flux.just(userResource)
                .flatMap(r -> {
                    try {
                        String json = new String(r.getInputStream().readAllBytes());
                        User[] users = objectMapper.readValue(json, User[].class);
                        return userRepository.saveAll(Arrays.stream(users).toList());
                    } catch (Exception e) {
                        log.error("Error sending MongoDB", e);
                        return Flux.error(e);
                    }
                }).subscribe();
    }

    @Bean
    public Disposable taskpopulator(ObjectMapper objectMapper) {
        return Flux.just(taskResource)
                .flatMap(r -> {
                    try {
                        String json = new String(r.getInputStream().readAllBytes());
                        Task[] tasks = objectMapper.readValue(json, Task[].class);
                        return taskRepository.saveAll(Arrays.stream(tasks).toList());
                    } catch (Exception e) {
                        log.error("Error sending MongoDB", e);
                        return Flux.error(e);
                    }
                }).subscribe();
    }

}

