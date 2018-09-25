package com.secondthorn.solitaire.pyramid.service.controller;

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
    public List<Solution> board(@RequestParam(value = "deck") String deckString) {
        Deck.validateDeckString(deckString);
        BoardChallenge boardChallenge = challengeRepository.findByDeckString(deckString);
        if (boardChallenge == null) {
            BoardChallengeSolver solver = new BoardChallengeSolver();
            Deck deck = new Deck(deckString);
            List<Solution> solutions = solver.solve(deck);
            boardChallenge = new BoardChallenge(deckString, solutions);
            challengeRepository.save(boardChallenge);
        }
        return boardChallenge.getSolutions();
    }

    @GetMapping("/pyramid-solitaire/solver/score")
    public List<Solution> score(@RequestParam(value = "deck") String deckString,
                                @RequestParam(value = "goalScore", required = false) Integer goalScore,
                                @RequestParam(value = "currentScore", required = false) Integer currentScore) {
        Deck.validateDeckString(deckString);
        if (currentScore == null) {
            currentScore = 0;
        }
        if (goalScore == null) {
            goalScore = 1290;
        }
        int goal = goalScore - currentScore;
        if (goal <= 0) {
            throw new InvalidParameterException(String.format(
                    "goalScore (%d) must be greater than currentScore (%d)",
                    goalScore, currentScore));
        }
        ScoreChallenge scoreChallenge = challengeRepository.findByDeckStringAndGoalScore(deckString, goal);
        if (scoreChallenge == null) {
            ScoreChallengeSolver solver = new ScoreChallengeSolver(goalScore, currentScore);
            Deck deck = new Deck(deckString);
            List<Solution> solutions = solver.solve(deck);
            scoreChallenge = new ScoreChallenge(deckString, goal, solutions);
            challengeRepository.save(scoreChallenge);
        }
        return scoreChallenge.getSolutions();
    }

    @GetMapping("/pyramid-solitaire/solver/card")
    public List<Solution> card(@RequestParam(value = "deck") String deckString,
                               @RequestParam(value = "rankToRemove") Character goalRank,
                               @RequestParam(value = "goalNumberToRemove") int goalNum,
                               @RequestParam(value = "currentNumberRemoved") int currentNum) {
        Deck.validateDeckString(deckString);
        if ("A23456789TJQK".indexOf(goalRank) == -1) {
            throw new InvalidParameterException("rankToRemove (" + goalRank +
                    ") is an invalid card rank. " +
                    "It must be one of A 2 3 4 5 6 7 8 9 T J Q K.");
        }
        int goal = goalNum - currentNum;
        if (goal <= 0) {
            throw new InvalidParameterException(String.format(
                    "goalNumberToRemove (%d) must be greater than currentNumberRemoved (%d)",
                    goalNum, currentNum));
        }
        CardChallenge cardChallenge = challengeRepository.findByDeckStringAndGoalRankAndGoalNum(deckString, goalRank, goal);
        if (cardChallenge == null) {
            CardChallengeSolver solver = new CardChallengeSolver(goalNum, goalRank, currentNum);
            Deck deck = new Deck(deckString);
            List<Solution> solutions = solver.solve(deck);
            cardChallenge = new CardChallenge(deckString, goalRank, goal, solutions);
            challengeRepository.save(cardChallenge);
        }
        return cardChallenge.getSolutions();
    }
}
