package org.example.taskstracker.configuration;

import org.example.taskstracker.handler.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class UserRouter {

    @Bean
    public RouterFunction<ServerResponse> userRouters(UserHandler userHandler){
        return RouterFunctions.route()
                .GET("/api/v1/functions/users", userHandler::getAllUsers)
                .GET("/api/v1/functions/user/{id}", userHandler::findById)
                .POST("/api/v1/functions/user", userHandler::createUser)
                .GET("/api/v1/functions/error", userHandler::errorRequest)
                .build();
    }
}
