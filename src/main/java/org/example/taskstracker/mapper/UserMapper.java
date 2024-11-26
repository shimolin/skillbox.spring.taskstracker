package org.example.taskstracker.mapper;

import org.example.taskstracker.entity.User;
import org.example.taskstracker.model.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserModel toUserModel(User user);
    User toUser(UserModel model);

}
