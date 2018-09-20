package com.secondthorn.solitaire.pyramid.service;

import com.secondthorn.solitaire.pyramid.solver.BoardChallengeSolver;
import com.secondthorn.solitaire.pyramid.solver.CardChallengeSolver;
import com.secondthorn.solitaire.pyramid.solver.Deck;
import com.secondthorn.solitaire.pyramid.solver.ScoreChallengeSolver;
import com.secondthorn.solitaire.pyramid.solver.Solution;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ServiceController {
    @RequestMapping("/pyramid-solitaire/solver/Board")
    public List<Solution> board(@RequestParam(value = "deck") String deckString) {
        BoardChallengeSolver solver = new BoardChallengeSolver();
        Deck deck = new Deck(deckString);
        return solver.solve(deck);
    }

    @RequestMapping("/pyramid-solitaire/solver/Score")
    public List<Solution> score(@RequestParam(value = "deck") String deckString,
                                @RequestParam(value = "goal", required = false) Integer goal,
                                @RequestParam(value = "current", required = false) Integer current) {
        ScoreChallengeSolver solver;
        if (goal == null && current == null) {
            solver = new ScoreChallengeSolver();
        } else {
            solver = new ScoreChallengeSolver(goal, current);
        }
        Deck deck = new Deck(deckString);
        return solver.solve(deck);
    }

    @RequestMapping("/pyramid-solitaire/solver/Card")
    public List<Solution> card(@RequestParam(value = "deck") String deckString,
                               @RequestParam(value = "rank") Character rank,
                               @RequestParam(value = "goal") int goal,
                               @RequestParam(value = "current") int current) {
        CardChallengeSolver solver = new CardChallengeSolver(goal, rank, current);
        Deck deck = new Deck(deckString);
        return solver.solve(deck);
    }
}
