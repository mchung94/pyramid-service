package com.secondthorn.solitaire.pyramid.service.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

/**
 * The goal of a Board Challenge is to clear the 28 pyramid cards from the
 * "board" (or table, or tableau).  The only required parameter is the deck of
 * cards to play with.
 */
@Entity
@Table(name = "board_challenges")
public class BoardChallenge extends Challenge {
    @Column(name = "deck_string", unique = true, nullable = false, updatable = false, columnDefinition = "varchar(104)")
    private String deckString;

    protected BoardChallenge() {
    }

    public BoardChallenge(String deckString, List<Solution> solutions) {
        this.deckString = deckString;
        this.solutions = solutions;
        for (Solution solution : solutions) {
            solution.setChallenge(this);
        }
    }

    @Override
    public String getDeckString() {
        return this.deckString;
    }
}
