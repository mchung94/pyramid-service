package com.secondthorn.solitaire.pyramid.solver;

import gnu.trove.map.TLongLongMap;
import gnu.trove.map.hash.TLongLongHashMap;

import java.util.ArrayList;
import java.util.List;

/**
 * A Pyramid Solitaire Card Challenge solver.
 * <p>
 * Card challenges take the form: "Clear N [Rank] in M deals", e.g. "Clear 12
 * Aces in 2 deals". This solver is the most complicated because sometimes
 * there isn't one right solution.
 * <p>
 * The unit tests have a game where the solver finds a way to remove two cards
 * of rank 4 while clearing the board, or three cards of rank 4 without clearing
 * the board.  The player must decide the best solution because it depends on
 * the next game afterwards.
 * <p>
 * The solver will return both solutions when it can't be certain there's one
 * right answer.
 */

public class CardChallengeSolver extends BFSSolver {
    private int numCardsToClear;
    private char cardRankToClear;
    private int cardRankValueToClear;

    /**
     * Create a pyramid solitaire solver that tries to remove the cards of the
     * goal rank.  Throws an IllegalArgumentException if the goal has already
     * been reached or if the rank isn't valid.
     */
    public CardChallengeSolver(int goalNumCardsToClear, char cardRankToClear, int currentNumCardsCleared) {
        if (currentNumCardsCleared > goalNumCardsToClear) {
            throw new IllegalArgumentException("The current number of cards cleared must be smaller than the goal");
        }
        this.numCardsToClear = goalNumCardsToClear - currentNumCardsCleared;
        this.cardRankToClear = cardRankToClear;
        this.cardRankValueToClear = "A23456789TJQK".indexOf(cardRankToClear) + 1;
        if (this.cardRankValueToClear == 0) {
            throw new IllegalArgumentException("The card rank " + cardRankToClear + " is invalid.  " +
                    "It must be one of A 2 3 4 5 6 7 8 9 T J Q K.");
        }
    }

    /**
     * Solve the pyramid solitaire game using breadth-first search.  The solver
     * needs to determine the fastest ways to:
     * <p>
     * 1. Reach the goal before running out of possible moves.
     * 2. Clear the board while maximizing how many goal cards are removed.
     * 3. Maximize the goal cards removed without clearing the board.
     * <p>
     * Out of these three solutions, there's always one best one except for the
     * case where #1 is impossible, and #2 clears fewer goal cards than #3.
     * For example, removing two cards of rank 4 while clearing the board vs.
     * removing three cards of rank 4 without clearing the board.  Depending
     * on the next game, either answer could be the best choice.
     */
    public List<Solution> solve(Deck deck) {
        List<Solution> solutions = new ArrayList<>();
        LongFIFOQueue fringe = new LongFIFOQueue();
        TLongLongMap seenStates = new TLongLongHashMap();

        long goalReachedState = -1;
        int goalReachedScore = 0;
        long bestClearState = -1;
        int bestClearScore = 0;
        long bestNonClearState = -1;
        int bestNonClearScore = 0;

        fringe.enqueue(State.INITIAL_STATE);
        while (!fringe.isEmpty()) {
            long state = fringe.dequeue();
            int score = numCardsOfRankRemoved(state, cardRankValueToClear, deck);
            if (score == numCardsToClear) {
                goalReachedState = state;
                goalReachedScore = score;
                break;
            }
            if (State.isPyramidClear(state)) {
                if (score > bestClearScore) {
                    bestClearState = state;
                    bestClearScore = score;
                    if (bestSolutionsFound(bestClearScore, bestNonClearScore)) {
                        break;
                    }
                }
            } else {
                long[] masks = deck.getSuccessorMasks(state);
                if (masks.length == 0) {
                    if (score > bestNonClearScore) {
                        bestNonClearState = state;
                        bestNonClearScore = score;
                        if (bestSolutionsFound(bestClearScore, bestNonClearScore)) {
                            break;
                        }
                    }
                } else {
                    addSuccessorStates(fringe, seenStates, state, masks);
                }
            }
        }

        if (goalReachedState != -1) {
            // add the state where the goal is reached
            solutions.add(goalReached(deck, seenStates, goalReachedState, goalReachedScore));
        } else if ((bestClearState != -1) && (bestNonClearState == -1)) {
            // add the best state that cleared the board
            solutions.add(clearedState(deck, seenStates, bestClearState, bestClearScore));
        } else if ((bestClearState == -1) && (bestNonClearState != -1)) {
            // add the best state that didn't clear the board
            solutions.add(nonClearedState(deck, seenStates, bestNonClearState, bestNonClearScore));
        } else if ((bestClearState != -1)) {
            if (bestClearScore >= bestNonClearScore) {
                // add the best clearing score since it's better
                solutions.add(clearedState(deck, seenStates, bestClearState, bestClearScore));
            } else {
                // add both
                solutions.add(clearedState(deck, seenStates, bestClearState, bestClearScore));
                solutions.add(nonClearedState(deck, seenStates, bestNonClearState, bestNonClearScore));
            }
        }

        return solutions;
    }

    private Solution goalReached(Deck deck, TLongLongMap seenStates, long state, int numRemoved) {
        return createSolution(deck, seenStates, state, numRemoved, State.isPyramidClear(state));
    }

    private Solution clearedState(Deck deck, TLongLongMap seenStates, long state, int numRemoved) {
        return createSolution(deck, seenStates, state, numRemoved, true);
    }

    private Solution nonClearedState(Deck deck, TLongLongMap seenStates, long state, int numRemoved) {
        return createSolution(deck, seenStates, state, numRemoved, false);
    }


    private Solution createSolution(Deck deck, TLongLongMap seenStates,
                                    long endState, int numCardsCleared,
                                    boolean boardCleared) {
        List<String> actions = actions(seenStates, endState, deck);
        int score = score(endState, deck);
        String description = description(numCardsCleared);
        return new Solution(description, score, boardCleared, actions);
    }

    // Make a human-readable solution description
    private String description(int numCardsCleared) {
        String cards = (numCardsCleared == 1) ? "card" : "cards";
        return "Remove " + numCardsCleared + " " + cards + " of rank " + cardRankToClear + ".";
    }

    private int numCardsOfRankRemoved(long state, int rankValue, Deck deck) {
        // using Kernighan's method in The C Programming Language 2nd Ed.
        // Exercise 2-9 to count set bits
        int numCardsRemoved = 4;
        long remainingCardFlags = state & deck.cardRankMask(rankValue);
        while (remainingCardFlags != 0) {
            remainingCardFlags &= remainingCardFlags - 1;
            numCardsRemoved--;
        }
        return numCardsRemoved;
    }

    // For convenience, check if you've already found the best possible
    // solutions for both clearing the board and not clearing the board (all
    // four cards removed in the best solutions).  If true, we don't need to
    // keep looking for solutions.
    private boolean bestSolutionsFound(int bestClearScore, int bestNonClearScore) {
        return (bestClearScore == 4) && (bestNonClearScore == 4);
    }


}
