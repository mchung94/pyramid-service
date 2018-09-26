package com.secondthorn.solitaire.pyramid.service.solver;

import com.secondthorn.solitaire.pyramid.service.model.Solution;
import com.secondthorn.solitaire.pyramid.service.model.Step;
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
    public static final int MAX_POSSIBLE_SCORE = 1290;
    private int numPoints;

    public ScoreChallengeSolver() {
        numPoints = MAX_POSSIBLE_SCORE;
    }

    /**
     * Create a pyramid solitaire solver that tries to reach the goal score
     * in as few steps as possible.  Throws an IllegalArgumentException if the
     * goal score has already been reached.
     */
    public ScoreChallengeSolver(int numPoints) {
        if (numPoints < 0) {
            throw new IllegalArgumentException("The current score must be smaller than the goal score");
        }
        this.numPoints = numPoints;
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
        fringe.enqueue(State.INITIAL_STATE);
        while (!fringe.isEmpty()) {
            long state = fringe.dequeue();
            long[] successorMasks = deck.getSuccessorMasks(state);
            int score = score(state, deck);
            if (score >= numPoints) {
                // stop searching, we reached the goal score
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
            List<Step> steps = getSteps(seenStates, bestState, deck);
            boolean boardCleared = State.isPyramidClear(bestState);
            solutions.add(new Solution("", bestScore, boardCleared, steps));
        }

        return solutions;
    }
}
