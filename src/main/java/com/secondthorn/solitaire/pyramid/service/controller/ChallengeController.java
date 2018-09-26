package com.secondthorn.solitaire.pyramid.service.controller;

import com.secondthorn.solitaire.pyramid.service.exception.ChallengeNotFoundException;
import com.secondthorn.solitaire.pyramid.service.exception.InvalidParameterException;
import com.secondthorn.solitaire.pyramid.service.model.Challenge;
import com.secondthorn.solitaire.pyramid.service.model.Solution;

import java.util.List;

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

    protected abstract Challenge queryChallenge(ChallengeParameters params);

    protected abstract String challengeDescription(ChallengeParameters params);

    protected abstract void saveNewChallenge(ChallengeParameters params);

    protected List<Solution> getChallenge(ChallengeParameters params) {
        params.validate();
        Challenge challenge = queryChallenge(params);
        if (challenge == null) {
            String message = challengeDescription(params) + " was not found.";
            throw new ChallengeNotFoundException(message);
        }
        return challenge.getSolutions();
    }

    protected List<Solution> postChallenge(ChallengeParameters params) {
        params.validate();
        Challenge challenge = queryChallenge(params);
        if (challenge == null) {
            saveNewChallenge(params);
        }
        return getChallenge(params);
    }
}
