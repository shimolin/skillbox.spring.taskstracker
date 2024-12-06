package org.example.taskstracker.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.taskstracker.entity.Task;

import java.time.Instant;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskModelRequest {

    private String id;

    @NotNull(message = "task.name must be not blank!!!")
    private String name;

    private String description;

    @NotNull (message = "task.status must be not blank!!!")
    private TaskStatus status;

    @NotNull (message = "task.authorId must be not blank!!!")
    private String authorId;

    @NotNull (message = "task.assigneeId must be not blank!!!")
    private String assigneeId;

    private Set<String> observerIds;
}
