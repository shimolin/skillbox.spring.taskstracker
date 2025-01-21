package org.example.taskstracker.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.taskstracker.entity.RoleType;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    @NotBlank(message = "username must be not blank!!!")
    private String username;

    private String email;

    private Set<RoleType> roles = new HashSet<>();

    private String password;

}
