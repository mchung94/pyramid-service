package com.secondthorn.solitaire.pyramid.service.controller;

import com.secondthorn.solitaire.pyramid.service.exception.InvalidParameterException;
import com.secondthorn.solitaire.pyramid.service.model.Challenge;
import com.secondthorn.solitaire.pyramid.service.model.ScoreChallenge;
import com.secondthorn.solitaire.pyramid.service.model.Solution;
import com.secondthorn.solitaire.pyramid.service.repository.ScoreChallengeRepository;
import com.secondthorn.solitaire.pyramid.service.solver.Deck;
import com.secondthorn.solitaire.pyramid.service.solver.ScoreChallengeSolver;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ScoreChallengeController extends ChallengeController {
    private ScoreChallengeRepository repository;

    public ScoreChallengeController(ScoreChallengeRepository repository) {
        this.repository = repository;
    }

    class ScoreChallengeParameters implements ChallengeParameters {
        private String deckString;
        private Integer goalScore;
        private Integer currentScore;
        private int pointsUntilGoal;

        ScoreChallengeParameters(String deckString, Integer goalScore, Integer currentScore) {
            this.deckString = deckString;
            this.goalScore = goalScore == null ? ScoreChallengeSolver.MAX_POSSIBLE_SCORE : goalScore;
            this.currentScore = currentScore == null ? 0 : currentScore;
            this.pointsUntilGoal = ceilingMultipleOfFive(this.goalScore - this.currentScore);
        }

        String getDeckString() {
            return deckString;
        }

        Integer getGoalScore() {
            return goalScore;
        }

        Integer getCurrentScore() {
            return currentScore;
        }

        int getPointsUntilGoal() {
            return pointsUntilGoal;
        }

        // Return the nearest multiple of five that is equal to or greater than
        // num, because the score is always a multiple of five from 0 to 1290.
        private int ceilingMultipleOfFive(int num) {
            return ((num + 4) / 5) * 5;
        }

        public void validate() throws InvalidParameterException {
            Deck.validateDeckString(deckString);
            int pointsUntilGoal = getPointsUntilGoal();
            if (pointsUntilGoal <= 0) {
                throw new InvalidParameterException(String.format(
                        "goalScore (%d) must be greater than currentScore (%d)",
                        goalScore, currentScore));
            }
        }
    }

    @GetMapping("/pyramid-solitaire/solver/score")
    public List<Solution> getScoreChallenge(@RequestParam(value = "deck") String deckString,
                                            @RequestParam(value = "goalScore", required = false) Integer goalScore,
                                            @RequestParam(value = "currentScore", required = false) Integer currentScore) {
        ScoreChallengeParameters params = new ScoreChallengeParameters(deckString, goalScore, currentScore);
        return getChallenge(params);
    }

    @PostMapping("/pyramid-solitaire/solver/score")
    public List<Solution> postScoreChallenge(@RequestParam(value = "deck") String deckString,
                                             @RequestParam(value = "goalScore", required = false) Integer goalScore,
                                             @RequestParam(value = "currentScore", required = false) Integer currentScore) {
        ScoreChallengeParameters params = new ScoreChallengeParameters(deckString, goalScore, currentScore);
        return postChallenge(params);
    }

    protected Challenge queryChallenge(ChallengeParameters params) {
        String deckString = ((ScoreChallengeParameters) params).getDeckString();
        int pointsUntilGoal = ((ScoreChallengeParameters) params).getPointsUntilGoal();
        return repository.findByDeckStringAndNumPoints(deckString, pointsUntilGoal);
    }

    protected String challengeDescription(ChallengeParameters params) {
        String deckString = ((ScoreChallengeParameters) params).getDeckString();
        Integer goalScore = ((ScoreChallengeParameters) params).getGoalScore();
        Integer currentScore = ((ScoreChallengeParameters) params).getCurrentScore();
        return "Score Challenge for deck (" + deckString + "), " +
                "goalScore (" + goalScore + "), " +
                "currentScore (" + currentScore + ")";
    }

    protected void saveNewChallenge(ChallengeParameters params) {
        String deckString = ((ScoreChallengeParameters) params).getDeckString();
        int pointsUntilGoal = ((ScoreChallengeParameters) params).getPointsUntilGoal();
        Deck deck = new Deck(deckString);
        ScoreChallengeSolver solver = new ScoreChallengeSolver(pointsUntilGoal);
        List<Solution> solutions = solver.solve(deck);
        ScoreChallenge challenge = new ScoreChallenge(deckString, pointsUntilGoal, solutions);
        repository.save(challenge);
    }
}
