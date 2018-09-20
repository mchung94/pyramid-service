package com.secondthorn.solitaire.pyramid.solver;

import java.util.List;

/**
 * A Solution represents a solution to a Pyramid Solitaire game, containing
 * a text description of what happens, the game's final score, and Strings
 * explaining the actions to take (removing cards, drawing a card from the
 * stock pile to the waste pile, and recycling the waste pile).
 */
public class Solution {
    private String description;
    private int score;
    private List<String> actions;

    public Solution(String description, int score, List<String> actions) {
        this.description = description;
        this.score = score;
        this.actions = actions;
    }

    public String getDescription() {
        return this.description;
    }

    public List<String> getActions() {
        return this.actions;
    }

    public int getScore() {
        return this.score;
    }
}
