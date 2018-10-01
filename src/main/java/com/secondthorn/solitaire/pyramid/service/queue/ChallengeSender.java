package com.secondthorn.solitaire.pyramid.service.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Send challenges to a queue for a worker process to solve.
 */
public class ChallengeSender {
    private Logger logger = LoggerFactory.getLogger(ChallengeSender.class);

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private Queue queue;

    public void send(String challengeIdString) {
        template.convertAndSend(queue.getName(), challengeIdString);
        logger.info("Sent challengeIdString \"" + challengeIdString + "\" to queue " + queue.getName());
    }
}
