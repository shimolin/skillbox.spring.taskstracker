package org.example.taskstracker.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.taskstracker.model.UserModel;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;
    private String username;
    private String email;

    public static User from(UserModel model){
        var user = new User();
        user.setId(model.getId());
        user.setUsername(model.getUsername());
        user.setEmail(model.getEmail());
        return user;
    }
}
