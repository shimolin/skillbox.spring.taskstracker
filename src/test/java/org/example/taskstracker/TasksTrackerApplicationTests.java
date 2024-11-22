package org.example.taskstracker;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

@SpringBootTest
class TasksTrackerApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void createAFlux_range() {
        Flux<Long> integerFlux = Flux
                .interval(Duration.ofSeconds(2))
                .take(5)
                .map(l -> {
                            System.out.println(l);
                            return l;
                        }
                );

        StepVerifier.create(integerFlux)
                .expectNext(0L)
                .expectNext(1L)
                .expectNext(2L)
                .expectNext(3L)
                .expectNext(4L)
                .verifyComplete();
    }
}
