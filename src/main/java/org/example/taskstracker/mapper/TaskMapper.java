package org.example.taskstracker.mapper;

import org.example.taskstracker.entity.Task;
import org.example.taskstracker.entity.User;
import org.example.taskstracker.model.TaskModelRequest;
import org.example.taskstracker.model.TaskModelResponse;
import org.example.taskstracker.model.UserModel;
import org.example.taskstracker.service.TaskService;
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

    public abstract Task taskModelRequestToTask(TaskModelRequest model);

    public abstract TaskModelResponse taskToTaskModelResponse(Task task);

    public Mono<Task> enrich(Task task) {

        Mono<UserModel> author = task.getAuthorId() != null ? userService.findById(task.getAuthorId()) : Mono.just(new UserModel());
        Mono<UserModel> assignee = task.getAssigneeId() != null ? userService.findById(task.getAssigneeId()) : Mono.just(new UserModel());
        Mono<List<UserModel>> observers = task.getObserverIds() != null ? userService.findAllById(task.getObserverIds()).collectList() : Mono.just(new ArrayList<>());

        return Mono.zip(author, assignee, observers).flatMap(data -> {
            if (task.getAuthorId() != null) task.setAuthor(userMapper.toUser(data.getT1()));
            if (task.getAssigneeId() != null) task.setAssignee(userMapper.toUser(data.getT2()));
            if (task.getObserverIds() != null) {
                for (UserModel um : data.getT3()) {
                    task.getObservers().add(userMapper.toUser(um));
                }
            }
            return Mono.just(task);
        });
    }

}
