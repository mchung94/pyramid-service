package com.secondthorn.solitaire.pyramid.service.solver;

import com.secondthorn.solitaire.pyramid.service.model.Solution;
import com.secondthorn.solitaire.pyramid.service.model.Step;
import gnu.trove.map.TLongLongMap;
import gnu.trove.map.hash.TLongLongHashMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A Pyramid Solitaire Board Challenge solver.  This solver just wants to
 * remove the 28 pyramid cards in as few moves as possible.  If it is not
 * possible, it won't return any solution at all.
 * <p>
 * When there's no solution, the player can either ask for a new deal, or try a
 * ScoreChallengeSolver if they want to maximize the score.
 */
public class BoardChallengeSolver extends BFSSolver {

    /**
     * Solve the Pyramid Solitaire game using breadth-first search.  The
     * solver wants to remove all the 28 pyramid cards, or return no solutions.
     */
    public List<Solution> solve(Deck deck) {
        List<Solution> solutions = new ArrayList<>();
        LongFIFOQueue fringe = new LongFIFOQueue();
        TLongLongMap seenStates = new TLongLongHashMap();
        long state = State.INITIAL_STATE;
        if (!isUnclearable(state, deck)) {
            fringe.enqueue(State.INITIAL_STATE);
        }
        while (!fringe.isEmpty()) {
            state = fringe.dequeue();
            if (State.isPyramidClear(state)) {
                List<Step> steps = getSteps(seenStates, state, deck);
                int score = score(state, deck);
                solutions.add(new Solution("", score, true, steps));
                return solutions;
            }
            long[] masks = deck.getSuccessorMasks(state);
            addSuccessorStates(fringe, seenStates, state, masks);
        }
        // if there's no way to clear the board, ask for a new deal
        List<Step> steps = new ArrayList<>();
        steps.add(new Step(1, "Ask for a New Deal"));
        solutions.add(new Solution("There is no way to clear the board.", 0, false, steps));
        return solutions;
    }

    private boolean isUnclearable(long state, Deck deck) {
        return Arrays.stream(deck.getUnclearableMasks(state))
                .anyMatch(mask -> (state & mask) == 0);
    }
}
