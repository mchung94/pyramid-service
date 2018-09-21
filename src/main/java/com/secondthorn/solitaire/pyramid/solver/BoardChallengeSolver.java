package com.secondthorn.solitaire.pyramid.solver;

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
 * When there's no solution, the player can either "lose quickly" by just
 * drawing or recycling until there are no more moves, or try a
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
                List<String> actions = actions(seenStates, state, deck);
                int score = score(state, deck);
                solutions.add(new Solution("", score, true, actions));
                break;
            }
            long[] masks = deck.getSuccessorMasks(state);
            addSuccessorStates(fringe, seenStates, state, masks);
        }
        return solutions;
    }

    private boolean isUnclearable(long state, Deck deck) {
        return Arrays.stream(deck.getUnclearableMasks(state))
                .anyMatch(mask -> (state & mask) == 0);
    }
}
