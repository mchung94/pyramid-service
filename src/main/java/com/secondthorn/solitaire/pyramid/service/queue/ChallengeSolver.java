package com.secondthorn.solitaire.pyramid.service.queue;

import com.secondthorn.solitaire.pyramid.service.model.Challenge;
import com.secondthorn.solitaire.pyramid.service.repository.ChallengeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.util.Optional;

/**
 * A queue listener that receives challenge IDs, queries and solves them, and
 * saves the solutions back to the database.
 */
@RabbitListener(queues = "challenges")
public class ChallengeSolver {
    private Logger logger = LoggerFactory.getLogger(ChallengeSolver.class);

    private ChallengeRepository repository;

    protected ChallengeSolver() {
    }

    public ChallengeSolver(ChallengeRepository repository) {
        this.repository = repository;
    }

    @RabbitHandler
    public void receive(String challengeIdString) {
        logger.info("Received challenge ID \"" + challengeIdString + "\" from queue.");
        Long id = Long.valueOf(challengeIdString);
        Optional<Challenge> ch = repository.findById(id);
        if (ch.isPresent()) {
            Challenge challenge = ch.get();
            logger.info("Solving challenge from " + challenge.getUriPath() + "?" + challenge.getUriQuery());
            challenge.solve();
            repository.save(challenge);
        }
    }
}
