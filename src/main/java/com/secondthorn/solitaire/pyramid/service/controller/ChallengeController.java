package com.secondthorn.solitaire.pyramid.service.controller;

import com.secondthorn.solitaire.pyramid.service.exception.ChallengeNotFoundException;
import com.secondthorn.solitaire.pyramid.service.exception.InvalidParameterException;
import com.secondthorn.solitaire.pyramid.service.model.BoardChallenge;
import com.secondthorn.solitaire.pyramid.service.model.CardChallenge;
import com.secondthorn.solitaire.pyramid.service.model.ScoreChallenge;
import com.secondthorn.solitaire.pyramid.service.model.Solution;
import com.secondthorn.solitaire.pyramid.service.repository.ChallengeRepository;
import com.secondthorn.solitaire.pyramid.service.solver.BoardChallengeSolver;
import com.secondthorn.solitaire.pyramid.service.solver.CardChallengeSolver;
import com.secondthorn.solitaire.pyramid.service.solver.Deck;
import com.secondthorn.solitaire.pyramid.service.solver.ScoreChallengeSolver;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * This is the main service controller for solving Pyramid Solitaire games.
 * It is designed to give solutions to Board/Score/Card challenges like in
 * Microsoft Solitaire Collection.  Otherwise, the players can just choose if
 * they want to maximize the score (score challenge with goal score of 1290) or
 * clear the board in as few steps as possible (board challenge).
 */
@RestController
public class ChallengeController {
    private ChallengeRepository challengeRepository;

    public ChallengeController(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    @GetMapping("/pyramid-solitaire/solver/board")
    public List<Solution> getBoardChallenge(@RequestParam(value = "deck") String deckString) {
        validateBoardChallengeParams(deckString);
        BoardChallenge challenge = challengeRepository.findByDeckString(deckString);
        if (challenge == null) {
            throw new ChallengeNotFoundException("Board Challenge for deck " + deckString + " was not found.");
        }
        return challenge.getSolutions();
    }

    @PostMapping("/pyramid-solitaire/solver/board")
    public List<Solution> postBoardChallenge(@RequestParam(value = "deck") String deckString) {
        validateBoardChallengeParams(deckString);
        BoardChallenge challenge = challengeRepository.findByDeckString(deckString);
        if (challenge == null) {
            BoardChallengeSolver solver = new BoardChallengeSolver();
            Deck deck = new Deck(deckString);
            List<Solution> solutions = solver.solve(deck);
            challenge = new BoardChallenge(deckString, solutions);
            challengeRepository.save(challenge);
        }
        return challenge.getSolutions();
    }

    @GetMapping("/pyramid-solitaire/solver/score")
    public List<Solution> getScoreChallenge(@RequestParam(value = "deck") String deckString,
                                            @RequestParam(value = "goalScore", required = false) Integer goalScore,
                                            @RequestParam(value = "currentScore", required = false) Integer currentScore) {
        validateScoreChallengeParams(deckString, goalScore, currentScore);
        int goalPoints = getGoalPoints(goalScore, currentScore);
        ScoreChallenge challenge = challengeRepository.findByDeckStringAndGoalScore(deckString, goalPoints);
        if (challenge == null) {
            throw new ChallengeNotFoundException("Score Challenge for deck (" + deckString + "), " +
                    "goalScore (" + goalScore + "), currentScore (" + currentScore + ") was not found.");
        }
        return challenge.getSolutions();
    }

    @PostMapping("/pyramid-solitaire/solver/score")
    public List<Solution> postScoreChallenge(@RequestParam(value = "deck") String deckString,
                                             @RequestParam(value = "goalScore", required = false) Integer goalScore,
                                             @RequestParam(value = "currentScore", required = false) Integer currentScore) {
        validateScoreChallengeParams(deckString, goalScore, currentScore);
        int goalPoints = getGoalPoints(goalScore, currentScore);
        ScoreChallenge challenge = challengeRepository.findByDeckStringAndGoalScore(deckString, goalPoints);
        if (challenge == null) {
            ScoreChallengeSolver solver = new ScoreChallengeSolver(goalScore, currentScore);
            Deck deck = new Deck(deckString);
            List<Solution> solutions = solver.solve(deck);
            challenge = new ScoreChallenge(deckString, goalPoints, solutions);
            challengeRepository.save(challenge);
        }
        return challenge.getSolutions();
    }

    @GetMapping("/pyramid-solitaire/solver/card")
    public List<Solution> getCardChallenge(@RequestParam(value = "deck") String deckString,
                                           @RequestParam(value = "rankToRemove") char goalRank,
                                           @RequestParam(value = "goalNumberToRemove") int goalNum,
                                           @RequestParam(value = "currentNumberRemoved") int currentNum) {
        validateCardChallengeParams(deckString, goalRank, goalNum, currentNum);
        int goal = goalNum - currentNum;
        CardChallenge challenge = challengeRepository.findByDeckStringAndGoalRankAndGoalNum(deckString, goalRank, goal);
        if (challenge == null) {
            throw new ChallengeNotFoundException("Score Challenge for deck (" + deckString + "), " +
                    "goalRank (" + goalRank + "), goalNum (" + goalNum + "), " +
                    "currentNum (" + currentNum + ") was not found");
        }
        return challenge.getSolutions();
    }

    @PostMapping("/pyramid-solitaire/solver/card")
    public List<Solution> postCardChallenge(@RequestParam(value = "deck") String deckString,
                                            @RequestParam(value = "rankToRemove") char goalRank,
                                            @RequestParam(value = "goalNumberToRemove") int goalNum,
                                            @RequestParam(value = "currentNumberRemoved") int currentNum) {
        validateCardChallengeParams(deckString, goalRank, goalNum, currentNum);
        int goal = goalNum - currentNum;
        CardChallenge challenge = challengeRepository.findByDeckStringAndGoalRankAndGoalNum(deckString, goalRank, goal);
        if (challenge == null) {
            CardChallengeSolver solver = new CardChallengeSolver(goalNum, goalRank, currentNum);
            Deck deck = new Deck(deckString);
            List<Solution> solutions = solver.solve(deck);
            challenge = new CardChallenge(deckString, goalRank, goal, solutions);
            challengeRepository.save(challenge);
        }
        return challenge.getSolutions();
    }

    private void validateBoardChallengeParams(String deckString) {
        Deck.validateDeckString(deckString);
    }

    private int getGoalPoints(Integer goalScore, Integer currentScore) {
        if (currentScore == null) {
            currentScore = 0;
        }
        if (goalScore == null) {
            goalScore = 1290;
        }
        return goalScore - currentScore;
    }

    private void validateScoreChallengeParams(String deckString, Integer goalScore, Integer currentScore) {
        Deck.validateDeckString(deckString);
        int goalPoints = getGoalPoints(goalScore, currentScore);
        if (goalPoints <= 0) {
            throw new InvalidParameterException(String.format(
                    "goalScore (%d) must be greater than currentScore (%d)",
                    goalScore, currentScore));
        }
    }

    private void validateCardChallengeParams(String deckString, char goalRank, int goalNum, int currentNum) {
        Deck.validateDeckString(deckString);
        if ("A23456789TJQK".indexOf(goalRank) == -1) {
            throw new InvalidParameterException("rankToRemove (" + goalRank +
                    ") is an invalid card rank. " +
                    "It must be one of A 2 3 4 5 6 7 8 9 T J Q K.");
        }
        if (goalNum - currentNum <= 0) {
            throw new InvalidParameterException(String.format(
                    "goalNumberToRemove (%d) must be greater than currentNumberRemoved (%d)",
                    goalNum, currentNum));
        }
    }
}
