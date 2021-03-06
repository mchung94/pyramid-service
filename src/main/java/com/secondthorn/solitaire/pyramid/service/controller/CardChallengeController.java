package com.secondthorn.solitaire.pyramid.service.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.secondthorn.solitaire.pyramid.service.exception.InvalidParameterException;
import com.secondthorn.solitaire.pyramid.service.model.CardChallenge;
import com.secondthorn.solitaire.pyramid.service.model.Challenge;
import com.secondthorn.solitaire.pyramid.service.model.Solution;
import com.secondthorn.solitaire.pyramid.service.queue.ChallengeSender;
import com.secondthorn.solitaire.pyramid.service.repository.CardChallengeRepository;
import com.secondthorn.solitaire.pyramid.service.solver.Deck;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * A Controller for handling Pyramid Solitaire Card Challenge requests.
 */
@Profile("challenge_sender")
@RestController
public class CardChallengeController extends ChallengeController {
    private CardChallengeRepository repository;
    private ChallengeSender sender;

    public CardChallengeController(CardChallengeRepository repository, ChallengeSender sender) {
        this.repository = repository;
        this.sender = sender;
    }

    /**
     * Each Card Challenge requires a valid deck of cards, a card rank to be
     * removed, and numbers indicating how many of that card has been removed
     * so far and how many need to be removed to win the challenge.  For
     * example, a goal could be "Remove 4 Aces" and you've removed two already
     * before the start of the game.  Then the actual goal is to remove two
     * more Aces.
     */
    class CardChallengeParameters implements ChallengeParameters {
        private String deckString;
        private char goalRank;
        private int goalNum;
        private int currentNum;

        CardChallengeParameters(String deckString, char goalRank, int goalNum, int currentNum) {
            this.deckString = deckString.trim();
            this.goalRank = goalRank;
            this.goalNum = goalNum;
            this.currentNum = currentNum;
        }

        String getDeckString() {
            return deckString;
        }

        char getGoalRank() {
            return goalRank;
        }

        int getGoalNum() {
            return goalNum;
        }

        int getCurrentNum() {
            return currentNum;
        }

        int getNumToRemove() {
            return goalNum - currentNum;
        }

        public void validate() throws InvalidParameterException {
            Deck.validateDeckString(deckString);
            if ("A23456789TJQK".indexOf(goalRank) == -1) {
                throw new InvalidParameterException("rankToRemove (" + goalRank +
                        ") is an invalid card rank. " +
                        "It must be one of A 2 3 4 5 6 7 8 9 T J Q K.");
            }
            if (getNumToRemove() <= 0) {
                throw new InvalidParameterException(String.format(
                        "goalNumberToRemove (%d) must be greater than currentNumberRemoved (%d)",
                        goalNum, currentNum));
            }
        }
    }

    /**
     * Retrieve a Card Challenge's solution if it exists.
     */
    @GetMapping("/pyramid-solitaire/solver/card")
    public ResponseEntity<List<Solution>> getCardChallenge(
            @RequestParam(value = "deck") String deckString,
            @RequestParam(value = "rankToRemove") char goalRank,
            @RequestParam(value = "goalNumberToRemove") int goalNum,
            @RequestParam(value = "currentNumberRemoved") int currentNum) {
        CardChallengeParameters params = new CardChallengeParameters(deckString, goalRank, goalNum, currentNum);
        return getChallengeSolutions(params);
    }

    /**
     * Post a new Card Challenge to be solved.
     * It'll just return the answer if it's already been solved, otherwise it
     * will solve the challenge and return the solution.
     */
    @PostMapping("/pyramid-solitaire/solver/card")
    public ResponseEntity<JsonNode> postCardChallenge(
            @RequestParam(value = "deck") String deckString,
            @RequestParam(value = "rankToRemove") char goalRank,
            @RequestParam(value = "goalNumberToRemove") int goalNum,
            @RequestParam(value = "currentNumberRemoved") int currentNum,
            UriComponentsBuilder ucb) {
        CardChallengeParameters params = new CardChallengeParameters(deckString, goalRank, goalNum, currentNum);
        return postChallenge(params, ucb);
    }

    protected Challenge queryChallenge(ChallengeParameters params) {
        String deckString = ((CardChallengeParameters) params).getDeckString();
        char goalRank = ((CardChallengeParameters) params).getGoalRank();
        int numUntilGoal = ((CardChallengeParameters) params).getNumToRemove();
        return repository.findByDeckStringAndGoalRankAndNumToRemove(deckString, goalRank, numUntilGoal);
    }

    protected String challengeDescription(ChallengeParameters params) {
        String deckString = ((CardChallengeParameters) params).getDeckString();
        char goalRank = ((CardChallengeParameters) params).getGoalRank();
        int goalNum = ((CardChallengeParameters) params).getGoalNum();
        int currentNum = ((CardChallengeParameters) params).getCurrentNum();
        return "Card Challenge with deck (" + deckString + "), " +
                "rankToRemove (" + goalRank + "), " +
                "goalNumberToRemove (" + goalNum + "), " +
                "currentNumberRemoved (" + currentNum + ")";
    }

    protected Challenge saveNewChallenge(ChallengeParameters params) {
        String deckString = ((CardChallengeParameters) params).getDeckString();
        char goalRank = ((CardChallengeParameters) params).getGoalRank();
        int numUntilGoal = ((CardChallengeParameters) params).getNumToRemove();
        CardChallenge challenge = new CardChallenge(deckString, goalRank, numUntilGoal);
        challenge = repository.save(challenge);
        sender.send(challenge.getId().toString());
        return challenge;
    }

}
