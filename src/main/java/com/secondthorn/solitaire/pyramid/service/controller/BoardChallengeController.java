package com.secondthorn.solitaire.pyramid.service.controller;

import com.secondthorn.solitaire.pyramid.service.exception.InvalidParameterException;
import com.secondthorn.solitaire.pyramid.service.model.BoardChallenge;
import com.secondthorn.solitaire.pyramid.service.model.Challenge;
import com.secondthorn.solitaire.pyramid.service.model.Solution;
import com.secondthorn.solitaire.pyramid.service.queue.ChallengeSender;
import com.secondthorn.solitaire.pyramid.service.repository.BoardChallengeRepository;
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
 * A Controller for handling Pyramid Solitaire Board Challenge requests.
 */
@Profile("challenge_sender")
@RestController
public class BoardChallengeController extends ChallengeController {
    private BoardChallengeRepository repository;
    private ChallengeSender sender;

    public BoardChallengeController(BoardChallengeRepository repository, ChallengeSender sender) {
        this.repository = repository;
        this.sender = sender;
    }

    /**
     * Each Board Challenge requires a valid deck of cards.
     */
    class BoardChallengeParameters implements ChallengeParameters {
        private String deckString;

        BoardChallengeParameters(String deckString) {
            this.deckString = deckString.trim();
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
    public ResponseEntity<List<Solution>> getBoardChallenge(@RequestParam(value = "deck") String deckString) {
        BoardChallengeParameters params = new BoardChallengeParameters(deckString);
        return getChallengeSolutions(params);
    }

    /**
     * Post a new Board Challenge to be solved.
     * It'll just return the answer if it's already been solved, otherwise it
     * will solve the challenge and return the solution.
     */
    @PostMapping("/pyramid-solitaire/solver/board")
    public ResponseEntity<List<Solution>> postBoardChallenge(
            @RequestParam(value = "deck") String deckString,
            UriComponentsBuilder ucb) {
        BoardChallengeParameters params = new BoardChallengeParameters(deckString);
        return postChallenge(params, ucb);
    }

    protected Challenge queryChallenge(ChallengeParameters params) {
        String deckString = ((BoardChallengeParameters) params).getDeckString();
        return repository.findByDeckString(deckString);
    }

    protected String challengeDescription(ChallengeParameters params) {
        String deckString = ((BoardChallengeParameters) params).getDeckString();
        return "Board Challenge with deck (" + deckString + ")";
    }

    protected Challenge saveNewChallenge(ChallengeParameters params) {
        String deckString = ((BoardChallengeParameters) params).getDeckString();
        BoardChallenge challenge = new BoardChallenge(deckString);
        challenge = repository.save(challenge);
        sender.send(challenge.getId().toString());
        return challenge;
    }
}
