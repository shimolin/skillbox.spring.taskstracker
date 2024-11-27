package org.example.taskstracker.mapper;

import org.example.taskstracker.entity.Task;
import org.example.taskstracker.model.TaskModelRequest;
import org.example.taskstracker.model.TaskModelResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = UserMapperUtil.class
)
public interface TaskMapper {
    @Mapping(target = "author", source = "authorId", qualifiedByName = "getTaskAuthor")
    @Mapping(target = "assignee", source = "assigneeId", qualifiedByName = "getTaskAssignee")
//    @Mapping(target = "observers", source= "observerIds", qualifiedByName = "getTaskObservers")
    TaskModelResponse taskToTaskModelResponse(Task task);
    Task taskModelRequestToTask(TaskModelRequest model);


}
