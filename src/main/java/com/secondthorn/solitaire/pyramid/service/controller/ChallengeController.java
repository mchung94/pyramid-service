package com.secondthorn.solitaire.pyramid.service.controller;

import com.secondthorn.solitaire.pyramid.service.model.BoardChallenge;
import com.secondthorn.solitaire.pyramid.service.model.CardChallenge;
import com.secondthorn.solitaire.pyramid.service.model.Challenge;
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

@RestController
public class ChallengeController {
    private ChallengeRepository challengeRepository;

    public ChallengeController(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    @GetMapping("/pyramid-solitaire/solver/board")
    public List<Solution> board(@RequestParam(value = "deck") String deckString) {
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
        int goal = goalScore - currentScore;
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
        int goal = goalNum - currentNum;
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
