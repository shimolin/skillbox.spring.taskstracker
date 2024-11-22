package org.example.taskstracker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.taskstracker.entity.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {
    private String id;
    private String username;
    private String email;

    public static UserModel from(User user){
        var model = new UserModel();
        model.setId(user.getId());
        model.setUsername(user.getUsername());
        model.setEmail(user.getEmail());
        return model;
    }
}
