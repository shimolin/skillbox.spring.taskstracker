package org.example.taskstracker.publisher;

import org.example.taskstracker.model.UserModel;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;

@Component
public class UserUpdatesPublisher {

    private final Sinks.Many<UserModel> userModelUpdateSink;

    public UserUpdatesPublisher() {
        this.userModelUpdateSink = Sinks.many().multicast().onBackpressureBuffer();
    }

    public void publish(UserModel model){
        userModelUpdateSink.tryEmitNext(model);
    }

    public Sinks.Many<UserModel> getUpdateSink(){
        return userModelUpdateSink;
    }

}
