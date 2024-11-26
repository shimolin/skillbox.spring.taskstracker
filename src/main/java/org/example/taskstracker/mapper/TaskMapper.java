package org.example.taskstracker.mapper;

import org.example.taskstracker.entity.Task;
import org.example.taskstracker.model.TaskModelRequest;
import org.example.taskstracker.model.TaskModelResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskMapper {
    TaskModelResponse toTaskModelResponse(Task task);
    Task fromTaskModelRequest(TaskModelRequest model);

}
