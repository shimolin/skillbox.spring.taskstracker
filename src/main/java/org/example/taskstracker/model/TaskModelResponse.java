package org.example.taskstracker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.taskstracker.entity.Task;
import org.example.taskstracker.mapper.UserMapper;
import org.example.taskstracker.repository.UserRepository;
import org.example.taskstracker.service.UserService;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskModelResponse {
    private String id;
    private String name;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
    private TaskStatus status;
    private UserModel author;
    private UserModel assignee;
    private Set<UserModel> observers;

}
