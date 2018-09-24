package com.secondthorn.solitaire.pyramid.service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "solutions")
public class Solution {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "solution_id", nullable = false, updatable = false)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @Column(name = "description", nullable = false, columnDefinition = "varchar(82)")
    private String description;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "board_cleared", nullable = false)
    private Boolean boardCleared;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "solution")
    @OrderBy("step_number")
    private List<Step> steps;

    protected Solution() {
    }

    public Solution(String description, Integer score, Boolean boardCleared, List<Step> steps) {
        this.description = fullDescription(description, score, boardCleared, steps.size());
        this.score = score;
        this.boardCleared = boardCleared;
        this.steps = steps;
        for (Step step : steps) {
            step.setSolution(this);
        }
    }

    public Long getId() {
        return id;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public String getDescription() {
        return description;
    }

    public Integer getScore() {
        return score;
    }

    public Boolean isBoardCleared() {
        return boardCleared;
    }

    public List<Step> getSteps() {
        return steps;
    }

    private static String fullDescription(String description, int score, boolean boardCleared, int numActions) {
        String extra = "Get " + score + " " + pluralIfNeeded(score, "point") +
                " in " + numActions + " " + pluralIfNeeded(numActions, "step");
        if (boardCleared) {
            extra += " while clearing the board.";
        } else {
            extra += " without clearing the board.";
        }
        if (description.length() == 0) {
            return extra;
        } else {
            return description + " " + extra;
        }
    }

    private static String pluralIfNeeded(int num, String word) {
        return num == 1 ? word : word + "s";
    }

}
