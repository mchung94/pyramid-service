package com.secondthorn.solitaire.pyramid.solver;

import gnu.trove.map.TLongLongMap;
import gnu.trove.map.hash.TLongLongHashMap;

import java.util.ArrayList;
import java.util.List;

/**
 * A Pyramid Solitaire Score Challenge solver.  Score challenges take the
 * form: "Earn a score of N", e.g. "Earn a score of 2,400".  The solver
 * searches for a solution that maximizes the score in the minimum number of
 * steps.  Note that this may be more steps than the minimum required to clear
 * the board.  Because clearing the board maximizes the score, if it possible
 * to clear the board the solution will do it.
 */
public class ScoreChallengeSolver extends BFSSolver {
    private static final int MAX_POSSIBLE_SCORE = 1290;
    private int goalPoints;

    public ScoreChallengeSolver() {
        // without a goal score, make it unobtainable to find the max possible
        goalPoints = MAX_POSSIBLE_SCORE + 1;
    }

    /**
     * Create a pyramid solitaire solver that tries to reach the goal score
     * in as few steps as possible.  Throws an IllegalArgumentException if the
     * goal score has already been reached.
     */
    public ScoreChallengeSolver(int goalScore, int currentScore) {
        if (currentScore >= goalScore) {
            throw new IllegalArgumentException("The current score must be smaller than the goal score");
        }
        goalPoints = goalScore - currentScore;
    }

    /**
     * Solve the pyramid solitaire game using breadth-first search.  The
     * solver wants to reach the goal score with the fewest moves possible,
     * but if it can't reach the goal score it will just maximize the score.
     */
    public List<Solution> solve(Deck deck) {
        List<Solution> solutions = new ArrayList<>();
        LongFIFOQueue fringe = new LongFIFOQueue();
        TLongLongMap seenStates = new TLongLongHashMap();
        long bestState = -1;
        int bestScore = 0;
        boolean goalReached = false;
        fringe.enqueue(State.INITIAL_STATE);
        while (!fringe.isEmpty()) {
            long state = fringe.dequeue();
            long[] successorMasks = deck.getSuccessorMasks(state);
            int score = score(state, deck);
            if (score >= goalPoints) {
                // stop searching, we reached the goal score
                bestState = state;
                bestScore = score;
                goalReached = true;
                break;
            }
            if  (score == MAX_POSSIBLE_SCORE) {
                // stop searching, we can't do any better
                bestState = state;
                bestScore = score;
                break;
            }
            if (State.isPyramidClear(state) || (successorMasks.length == 0)) {
                // if we clear the board we can't continue any further in
                // the game, like removing stock + waste cards... but we should
                // continue the search in case there's a better scoring path
                if (score > bestScore) {
                    bestState = state;
                    bestScore = score;
                }
            } else {
                addSuccessorStates(fringe, seenStates, state, successorMasks);
            }
        }

        if (bestState != -1) {
            List<String> actions = actions(seenStates, bestState, deck);
            boolean boardCleared = State.isPyramidClear(bestState);
            if (goalReached) {
                solutions.add(new Solution("Goal reached.", bestScore, boardCleared, actions));
            } else {
                solutions.add(new Solution("", bestScore, boardCleared, actions));
            }
        }

        return solutions;
    }
}
