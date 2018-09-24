package com.secondthorn.solitaire.pyramid.service.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

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
