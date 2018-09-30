package com.secondthorn.solitaire.pyramid.service.controller;

import com.secondthorn.solitaire.pyramid.service.exception.InvalidParameterException;
import com.secondthorn.solitaire.pyramid.service.model.Challenge;
import com.secondthorn.solitaire.pyramid.service.model.ScoreChallenge;
import com.secondthorn.solitaire.pyramid.service.model.Solution;
import com.secondthorn.solitaire.pyramid.service.repository.ScoreChallengeRepository;
import com.secondthorn.solitaire.pyramid.service.solver.Deck;
import com.secondthorn.solitaire.pyramid.service.solver.ScoreChallengeSolver;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * A Controller for handling Pyramid Solitaire Score Challenge requests.
 */
@RestController
public class ScoreChallengeController extends ChallengeController {
    private ScoreChallengeRepository repository;

    public ScoreChallengeController(ScoreChallengeRepository repository) {
        this.repository = repository;
    }

    /**
     * Each Score Challenge requires a valid deck of cards, the current score
     * before the start of the game, and the goal score to win the challenge.
     * For example, the goal could be to reach 1500 points and you've got 300
     * points already.  Then the actual goal for the game is to get 1200 more
     * points.
     * <p>
     * If the goal and current scores are blank they default to trying to just
     * maximize the score overall (current score 0 and goal score 1290 which is
     * the maximum possible).
     */
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

    /**
     * Retrieve a Score Challenge's solution if it exists.
     */
    @GetMapping("/pyramid-solitaire/solver/score")
    public ResponseEntity<List<Solution>> getScoreChallenge(
            @RequestParam(value = "deck") String deckString,
            @RequestParam(value = "goalScore", required = false) Integer goalScore,
            @RequestParam(value = "currentScore", required = false) Integer currentScore) {
        ScoreChallengeParameters params = new ScoreChallengeParameters(deckString, goalScore, currentScore);
        return getChallengeSolutions(params);
    }

    /**
     * Post a new Score Challenge to be solved.
     * It'll just return the answer if it's already been solved, otherwise it
     * will solve the challenge and return the solution.
     */
    @PostMapping("/pyramid-solitaire/solver/score")
    public ResponseEntity<List<Solution>> postScoreChallenge(
            @RequestParam(value = "deck") String deckString,
            @RequestParam(value = "goalScore", required = false) Integer goalScore,
            @RequestParam(value = "currentScore", required = false) Integer currentScore,
            UriComponentsBuilder ucb) {
        ScoreChallengeParameters params = new ScoreChallengeParameters(deckString, goalScore, currentScore);
        return postChallenge(params, ucb);
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
        return "Score Challenge with deck (" + deckString + "), " +
                "goalScore (" + goalScore + "), " +
                "currentScore (" + currentScore + ")";
    }

    protected Challenge saveNewChallenge(ChallengeParameters params) {
        String deckString = ((ScoreChallengeParameters) params).getDeckString();
        int pointsUntilGoal = ((ScoreChallengeParameters) params).getPointsUntilGoal();
        ScoreChallenge challenge = new ScoreChallenge(deckString, pointsUntilGoal);
        return repository.save(challenge);
    }
}
