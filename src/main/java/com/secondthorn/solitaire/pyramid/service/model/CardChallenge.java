package com.secondthorn.solitaire.pyramid.service.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

/**
 * The goal of a Card Challenge is to remove cards of a certain rank.  The goal
 * may be to "Remove 12 Jacks in 2 deals".  This means to remove a total of
 * 12 Jacks as you play.  If you clear the board of the 28 pyramid cards,
 * another game starts automatically, but if you cannot clear the board, you
 * have to use a "deal" to restart the game with a new deck of cards.  These
 * re-deals are what is meant by "... in 2 deals."
 */
@Entity
@Table(name = "card_challenges")
public class CardChallenge extends Challenge {
    @Column(name = "deck_string", nullable = false, updatable = false, columnDefinition = "varchar(104)")
    private String deckString;

    @Column(name = "goal_rank", nullable = false, updatable = false, columnDefinition = "char")
    private Character goalRank;

    @Column(name = "num_to_remove", nullable = false, updatable = false, columnDefinition = "int")
    private Integer numToRemove;

    protected CardChallenge() {
    }

    public CardChallenge(String deckString, Character goalRank, Integer numToRemove, List<Solution> solutions) {
        super(solutions);
        this.deckString = deckString;
        this.goalRank = goalRank;
        this.numToRemove = numToRemove;
    }

    public Character getGoalRank() {
        return goalRank;
    }

    public Integer getNumToRemove() {
        return numToRemove;
    }

    @Override
    public String getDeckString() {
        return deckString;
    }
}
