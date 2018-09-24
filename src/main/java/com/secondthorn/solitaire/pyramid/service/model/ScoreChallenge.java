package com.secondthorn.solitaire.pyramid.service.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name="score_challenges")
public class ScoreChallenge extends Challenge {
    @NotNull
    @Size(min=104, max=104)
    @Column(name="deck_string", nullable=false, updatable=false, columnDefinition="varchar(104)")
    private String deckString;

    @Column(name="goal_score", nullable=false, updatable=false, columnDefinition="int")
    private Integer goalScore;

    protected ScoreChallenge() {}

    public ScoreChallenge(String deckString, Integer goalScore, List<Solution> solutions) {
        this.deckString = deckString;
        this.goalScore = goalScore;
        this.solutions = solutions;
        for (Solution solution : solutions) {
            solution.setChallenge(this);
        }
    }

    public Integer getGoalScore() {
        return goalScore;
    }

    @Override
    public String getDeckString() {
        return deckString;
    }
}
