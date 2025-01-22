package org.example.taskstracker.mapper;

import org.example.taskstracker.entity.Task;
import org.example.taskstracker.entity.User;
import org.example.taskstracker.model.TaskRequest;
import org.example.taskstracker.model.TaskResponse;
import org.example.taskstracker.service.UserService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class TaskMapper {

    @Autowired
    protected UserService userService;
    @Autowired
    protected UserMapper userMapper;

    public abstract Task taskRequestToTask(TaskRequest model);

    public abstract TaskResponse taskToTaskResponse(Task task);

    public Mono<Task> enrich(Task task) {

        Mono<User> author = task.getAuthorId() != null ? userService.findById(task.getAuthorId()) : Mono.just(new User());
        Mono<User> assignee = task.getAssigneeId() != null ? userService.findById(task.getAssigneeId()) : Mono.just(new User());
        Mono<List<User>> observers = task.getObserverIds() != null ? userService.findAllById(task.getObserverIds()).collectList() : Mono.just(new ArrayList<>());

        return Mono.zip(author, assignee, observers).flatMap(data -> {
            if (task.getAuthorId() != null) task.setAuthor(data.getT1());
            if (task.getAssigneeId() != null) task.setAssignee(data.getT2());
            if (task.getObserverIds() != null) {
                for (User u : data.getT3()) {
                    task.getObservers().add(u);
                }
            }
            return Mono.just(task);
        });
    }

}
