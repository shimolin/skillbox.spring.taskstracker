package org.example.taskstracker.mapper;

import org.example.taskstracker.entity.User;
import org.example.taskstracker.model.UserRequest;
import org.example.taskstracker.model.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class UserMapper {

    @Autowired
    protected PasswordEncoder passwordEncoder;
    public abstract UserResponse userToResponse(User user);

    @Mapping(target = "password", source = "password", qualifiedByName = "encodePassword")
    public abstract User requestToUser(UserRequest userRequest);

    public abstract User responseToUser(UserResponse userResponse);

    @Named("encodePassword")
    protected String encodePassword(String password){
        if(password != null){
            return passwordEncoder.encode(password);
        }else {
            return null;
        }

    }

}
