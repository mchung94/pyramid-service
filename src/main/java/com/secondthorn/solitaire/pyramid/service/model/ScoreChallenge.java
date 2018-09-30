package com.secondthorn.solitaire.pyramid.service.model;

import com.secondthorn.solitaire.pyramid.service.solver.Deck;
import com.secondthorn.solitaire.pyramid.service.solver.ScoreChallengeSolver;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * The goal of a Score Challenge is to reach a certain score.  The maximum
 * score possible is 1290.
 */
@Entity
@Table(name = "score_challenges")
public class ScoreChallenge extends Challenge {
    @Column(name = "deck_string", nullable = false, updatable = false, columnDefinition = "varchar(104)")
    private String deckString;

    @Column(name = "num_points", nullable = false, updatable = false, columnDefinition = "int")
    private Integer numPoints;

    protected ScoreChallenge() {
    }

    public ScoreChallenge(String deckString, Integer numPoints) {
        this.deckString = deckString;
        this.numPoints = numPoints;
    }

    @Override
    public String getDeckString() {
        return deckString;
    }

    public Integer getNumPoints() {
        return numPoints;
    }

    @Override
    public void solve() {
        Deck deck = new Deck(deckString);
        ScoreChallengeSolver solver = new ScoreChallengeSolver(numPoints);
        setSolutions(solver.solve(deck));
    }

    @Override
    public String getUriPath() {
        return "/pyramid-solitaire/solver/score";
    }

    @Override
    public String getUriQuery() {
        return "goalScore=" + numPoints + "&" + "currentScore=0&" + "deck=" + deckString;
    }
}
