package com.secondthorn.solitaire.pyramid.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.secondthorn.solitaire.pyramid.service.exception.InvalidParameterException;
import com.secondthorn.solitaire.pyramid.service.exception.SolutionNotFoundException;
import com.secondthorn.solitaire.pyramid.service.model.Challenge;
import com.secondthorn.solitaire.pyramid.service.model.Solution;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the main service controller for solving Pyramid Solitaire games.
 * It is designed to give solutions to Board/Score/Card challenges like in
 * Microsoft Solitaire Collection.  Otherwise, the players can just choose if
 * they want to maximize the score (score challenge with goal score of 1290) or
 * clear the board in as few steps as possible (board challenge).
 */
public abstract class ChallengeController {
    /**
     * An interface to wrap input parameters so that functionality common to
     * all subclasses can be declared/defined here.
     */
    protected interface ChallengeParameters {
        /**
         * Check all input parameters and throw an InvalidParameterException
         * if something's wrong.
         */
        void validate() throws InvalidParameterException;
    }

    /**
     * Query and return a Challenge from the database, or null if not found.
     */
    protected abstract Challenge queryChallenge(ChallengeParameters params);

    /**
     * Return a string description of the challenge based on its parameters.
     * It should mention which type of challenge it is (Board/Score/Card)
     * as well as what parameters it has.
     */
    protected abstract String challengeDescription(ChallengeParameters params);

    /**
     * Create an appropriate Challenge instance given the parameters, generate
     * the solution, and save it all into the database.
     */
    protected abstract Challenge saveNewChallenge(ChallengeParameters params);

    // The process to GET challenge solutions, customized per challenge type.
    protected ResponseEntity<List<Solution>> getChallengeSolutions(ChallengeParameters params) {
        params.validate();
        Challenge challenge = queryChallenge(params);
        if (!hasSolutions(challenge)) {
            String message = "Solutions for " + challengeDescription(params) + " were not found.";
            throw new SolutionNotFoundException(message);
        }
        return ResponseEntity.ok(challenge.getSolutions());
    }

    // The process to POST a challenge, customized per challenge type.
    protected ResponseEntity<List<Solution>> postChallenge(ChallengeParameters params, UriComponentsBuilder ucb) {
        params.validate();
        Challenge challenge = queryChallenge(params);
        if (hasSolutions(challenge)) {
            return ResponseEntity.ok(challenge.getSolutions());
        }
        if (challenge == null) {
            challenge = saveNewChallenge(params);
        }
        URI uri = ucb.path("/pyramid-solitaire/solver/tasks/" + challenge.getId()).build().toUri();
        Map<String, String> postResult = new HashMap<>();
        postResult.put("description", "Task created for " + challengeDescription(params));
        postResult.put("task_location", uri.toString());
        return ResponseEntity.accepted()
                .location(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ObjectMapper().valueToTree(postResult));
    }

    protected boolean hasSolutions(Challenge challenge) {
        return (challenge != null) && (challenge.getSolutions() != null) && (challenge.getSolutions().size() > 0);
    }
}
