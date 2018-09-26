package com.secondthorn.solitaire.pyramid.service.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

/**
 * The goal of a Score Challenge is to reach a certain score.  The maximum
 * score possible is 1290.
 */
@Entity
@Table(name = "score_challenges")
public class ScoreChallenge extends Challenge {
    @Column(name = "deck_string", nullable = false, updatable = false, columnDefinition = "varchar(104)")
    private String deckString;

    @Column(name = "goal_score", nullable = false, updatable = false, columnDefinition = "int")
    private Integer goalScore;

    protected ScoreChallenge() {
    }

    public ScoreChallenge(String deckString, Integer goalScore, List<Solution> solutions) {
        super(solutions);
        this.deckString = deckString;
        this.goalScore = goalScore;
    }

    public Integer getGoalScore() {
        return goalScore;
    }

    @Override
    public String getDeckString() {
        return deckString;
    }
}
