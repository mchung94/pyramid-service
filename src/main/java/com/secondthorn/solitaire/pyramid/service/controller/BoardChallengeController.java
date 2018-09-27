package com.secondthorn.solitaire.pyramid.service.controller;

import com.secondthorn.solitaire.pyramid.service.exception.InvalidParameterException;
import com.secondthorn.solitaire.pyramid.service.model.BoardChallenge;
import com.secondthorn.solitaire.pyramid.service.model.Challenge;
import com.secondthorn.solitaire.pyramid.service.model.Solution;
import com.secondthorn.solitaire.pyramid.service.repository.BoardChallengeRepository;
import com.secondthorn.solitaire.pyramid.service.solver.BoardChallengeSolver;
import com.secondthorn.solitaire.pyramid.service.solver.Deck;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * A Controller for handling Pyramid Solitaire Board Challenge requests.
 */
@RestController
public class BoardChallengeController extends ChallengeController {
    private BoardChallengeRepository repository;

    public BoardChallengeController(BoardChallengeRepository repository) {
        this.repository = repository;
    }

    /**
     * Each Board Challenge requires a valid deck of cards.
     */
    class BoardChallengeParameters implements ChallengeParameters {
        private String deckString;

        BoardChallengeParameters(String deckString) {
            this.deckString = deckString;
        }

        String getDeckString() {
            return deckString;
        }

        public void validate() throws InvalidParameterException {
            Deck.validateDeckString(deckString);
        }
    }

    /**
     * Retrieve a Board Challenge's solution if it exists.
     */
    @GetMapping("/pyramid-solitaire/solver/board")
    public List<Solution> getBoardChallenge(@RequestParam(value = "deck") String deckString) {
        BoardChallengeParameters params = new BoardChallengeParameters(deckString);
        return getChallenge(params);
    }

    /**
     * Post a new Board Challenge to be solved.
     * It'll just return the answer if it's already been solved, otherwise it
     * will solve the challenge and return the solution.
     */
    @PostMapping("/pyramid-solitaire/solver/board")
    public List<Solution> postBoardChallenge(@RequestParam(value = "deck") String deckString) {
        BoardChallengeParameters params = new BoardChallengeParameters(deckString);
        return postChallenge(params);
    }

    protected Challenge queryChallenge(ChallengeParameters params) {
        String deckString = ((BoardChallengeParameters) params).getDeckString();
        return repository.findByDeckString(deckString);
    }

    protected String challengeDescription(ChallengeParameters params) {
        String deckString = ((BoardChallengeParameters) params).getDeckString();
        return "Board Challenge for deck (" + deckString + ")";
    }

    protected void saveNewChallenge(ChallengeParameters params) {
        String deckString = ((BoardChallengeParameters) params).getDeckString();
        Deck deck = new Deck(deckString);
        BoardChallengeSolver solver = new BoardChallengeSolver();
        List<Solution> solutions = solver.solve(deck);
        BoardChallenge challenge = new BoardChallenge(deckString, solutions);
        repository.save(challenge);
    }
}
