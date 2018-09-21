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
    private boolean boardCleared;
    private List<String> actions;

    public Solution(String description, int score, boolean boardCleared, List<String> actions) {
        this.description = fullDescription(description, score, boardCleared, actions.size());
        this.score = score;
        this.boardCleared = boardCleared;
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

    public boolean isBoardCleared() {
        return this.boardCleared;
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
