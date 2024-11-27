package org.example.taskstracker.mapper;

import lombok.RequiredArgsConstructor;
import org.example.taskstracker.model.UserModel;
import org.example.taskstracker.service.UserService;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserMapperUtil {
    private final UserService userService;

    @Named("getTaskAuthor")
    public UserModel getTaskAuthor(String authorId){
        return userService.findById(authorId).block(); //TODO так не работает
   }
    @Named("getTaskAssignee")
    public UserModel getTaskAssignee(String assigneeId){
        return userService.findById(assigneeId).block(); //TODO так не работает
    }

    @Named("getTaskObservers")
    public Set<UserModel> getTaskObservers(String observerIds){
        return new HashSet<>(); //TODO
    }

}
