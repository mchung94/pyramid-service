package com.secondthorn.solitaire.pyramid.service.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name="card_challenges")
public class CardChallenge extends Challenge {
    @NotNull
    @Size(min=104, max=104)
    @Column(name="deck_string", nullable=false, updatable=false, columnDefinition="varchar(104)")
    private String deckString;

    @Column(name="goal_rank", nullable=false, updatable=false, columnDefinition="char")
    private Character goalRank;

    @Column(name="goal_num", nullable=false, updatable=false, columnDefinition="int")
    private Integer goalNum;

    protected CardChallenge() {}

    public CardChallenge(String deckString, Character goalRank, Integer goalNum, List<Solution> solutions) {
        this.deckString = deckString;
        this.goalRank = goalRank;
        this.goalNum = goalNum;
        this.solutions = solutions;
        for (Solution solution : solutions) {
            solution.setChallenge(this);
        }
    }

    public Character getGoalRank() {
        return goalRank;
    }

    public Integer getGoalNum() {
        return goalNum;
    }

    @Override
    public String getDeckString() {
        return deckString;
    }
}
