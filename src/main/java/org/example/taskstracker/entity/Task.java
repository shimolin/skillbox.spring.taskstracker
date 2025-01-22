package org.example.taskstracker.entity;

import lombok.*;
import org.example.taskstracker.model.TaskStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "tasks")
public class Task {

    @Id
    private String id;
    private String name;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
    private TaskStatus status;
    private String authorId;
    private String assigneeId;
    private Set<String> observerIds = new HashSet<>();

    @ReadOnlyProperty
    private User author = new User();

    @ReadOnlyProperty
    private User assignee = new User();

    @ReadOnlyProperty
    private Set<User> observers = new HashSet<>();

}
