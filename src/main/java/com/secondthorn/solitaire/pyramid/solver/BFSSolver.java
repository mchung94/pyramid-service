package com.secondthorn.solitaire.pyramid.solver;

import gnu.trove.map.TLongLongMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * BFSSolver represents Breadth-First Search solvers.  Subclasses must
 * implement the solve method but the methods defined here support finding
 * the actions to get from the initial state to the current state, calculating
 * the score if needed, and adding successor states to the search queue.
 */
public abstract class BFSSolver {
    /**
     * Solve Pyramid Solitaire with the given deck of cards, return a list of
     * Solutions.  There may be 0 solutions or more than one if the solver
     * wants the user to be able to select one of multiple solutions.
     */
    public abstract List<Solution> solve(Deck deck);

    // Return a list of actions taken to get from the initial state to the
    // current state.
    protected List<String> actions(TLongLongMap seenStates, long state, Deck deck) {
        List<String> actions = new ArrayList<>();
        long nextState = state;
        while (seenStates.containsKey(nextState)) {
            state = seenStates.get(nextState);
            actions.add(action(state, nextState, deck));
            nextState = state;
        }
        Collections.reverse(actions);
        return actions;
    }

    // Calculate the Pyramid Solitaire current score, given a state and the
    // deck of cards.
    protected int score(long state, Deck deck) {
        int score = 0;

        // masks to check if a row of the pyramid has been cleared in a state
        final long[] rowClearedMasks = {
                0b1111111000000000000000000000L,
                0b0000000111111000000000000000L,
                0b0000000000000111110000000000L,
                0b0000000000000000001111000000L,
                0b0000000000000000000000111000L,
                0b0000000000000000000000000110L,
                0b0000000000000000000000000001L
        };

        // the score for clearing each row from bottom to top row
        final int[] rowClearedScores = {
                25,
                50,
                75,
                100,
                150,
                250,
                500
        };

        for (int i = 0; i < rowClearedMasks.length; i++) {
            if ((state & rowClearedMasks[i]) == 0) {
                score += rowClearedScores[i];
            }
        }

        int numKingsRemoved = 0;
        int numNonKingsRemoved = 0;
        for (int i = 0; i < 52; i++) {
            long mask = 1L << i;
            if ((state & mask) == 0) {
                if (deck.isKing(i)) {
                    numKingsRemoved++;
                } else {
                    numNonKingsRemoved++;
                }
            }
        }
        score += (5 * numKingsRemoved) + (5 * (numNonKingsRemoved / 2));

        return score;
    }

    // Given a list of successor masks, generate the successors for a state
    // and insert them into the fringe (queue of search states to examine).
    protected void addSuccessorStates(LongFIFOQueue fringe, TLongLongMap seenStates, long state, long[] successorMasks) {
        for (long mask : successorMasks) {
            long nextState = State.adjustStockIndex(state ^ mask);
            if (!seenStates.containsKey(nextState)) {
                seenStates.put(nextState, state);
                fringe.enqueue(nextState);
            }
        }
    }

    // Return a String describing what action to take in the game to get from
    // previousState to state.
    private String action(long previousState, long state, Deck deck) {
        long diff = previousState ^ state;
        long existFlagsDiff = State.getDeckFlags(diff);
        long recycleCountDiff = State.getRecycleCount(diff);
        if (recycleCountDiff != 0) {
            return "Recycle";
        } else if (existFlagsDiff != 0) {
            List<Card> cardsToRemove = new ArrayList<>();
            long flag = 1L;
            for (int i = 0; i < 52; i++) {
                if ((flag & existFlagsDiff) != 0) {
                    cardsToRemove.add(deck.cardAt(i));
                }
                flag <<= 1;
            }
            if (cardsToRemove.size() == 1) {
                return "Remove " + cardsToRemove.get(0);
            } else if (cardsToRemove.get(0).getRank().compareTo(cardsToRemove.get(1).getRank()) < 0) {
                return "Remove " + cardsToRemove.get(0) + " and " + cardsToRemove.get(1);
            } else {
                return "Remove " + cardsToRemove.get(1) + " and " + cardsToRemove.get(0);
            }
        } else {
            return "Draw";
        }
    }
}
