package org.example.taskstracker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.taskstracker.entity.Task;
import org.example.taskstracker.repository.UserRepository;
import org.example.taskstracker.service.UserService;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
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

    public static TaskModelResponse from(Task task){
        TaskModelResponse model = new TaskModelResponse();
        model.setId(task.getId());
        model.setName(task.getName());
        model.setDescription(task.getDescription());
        model.setCreatedAt(task.getCreatedAt());
        model.setUpdatedAt(task.getUpdatedAt());
        model.setStatus(task.getStatus());
        if(task.getAuthor() != null) model.setAuthor(UserModel.from(task.getAuthor()));
        if(task.getAssignee() != null) model.setAssignee(UserModel.from(task.getAssignee()));
        if(task.getObservers() != null) {
            model.setObservers(
                    task.getObservers()
                            .stream()
                            .map(UserModel::from)
                            .collect(Collectors.toSet())
            );
        }
        return model;
    }
}
