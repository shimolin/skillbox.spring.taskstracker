package org.example.taskstracker.publisher;

import org.example.taskstracker.model.UserRequest;
import org.example.taskstracker.model.UserResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;

@Component
public class UserUpdatesPublisher {

    private final Sinks.Many<UserResponse> userModelUpdateSink;

    public UserUpdatesPublisher() {
        this.userModelUpdateSink = Sinks.many().multicast().onBackpressureBuffer();
    }

    public void publish(UserResponse model){
        userModelUpdateSink.tryEmitNext(model);
    }

    public Sinks.Many<UserResponse> getUpdateSink(){
        return userModelUpdateSink;
    }

}
