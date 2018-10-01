package com.secondthorn.solitaire.pyramid.service.queue;

import com.secondthorn.solitaire.pyramid.service.repository.ChallengeRepository;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class QueueConfig {
    @Bean
    public Queue challenges() {
        return new Queue("challenges", true);
    }

    @Profile("challenge_sender")
    @Bean
    public ChallengeSender challengeSender() {
        return new ChallengeSender();
    }

    @Profile("challenge_solver")
    @Bean
    public ChallengeSolver challengeSolver(ChallengeRepository repository) {
        return new ChallengeSolver(repository);
    }
}
