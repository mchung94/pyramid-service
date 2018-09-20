package com.secondthorn.solitaire.pyramid.solver;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class CardChallengeSolverTest {
    @Test(expected = IllegalArgumentException.class)
    public void constructorAlreadyPastGoal() {
        new CardChallengeSolver(3, 'A', 4);
    }

    @Test
    public void twoSolutionTest() {
        CardChallengeSolver solver = new CardChallengeSolver(4, '4', 0);
        String cards = "Tc Ac Js 5d 2h 3h As Th Qd 7h 3c Td 8s Kh 6d Ks 5c 6h 9h 3d 5h Jh Kc 8d Jd 8c " +
                "7d 7c 2d Qs 9s 2c 3s 7s Ah Ad 4h 6s 6c 4c 2s 4d Qh 9d Jc 4s Qc Ts Kd 5s 9c 8h";
        Deck deck = new Deck(cards);
        List<Solution> solutions = solver.solve(deck);
        assertThat(solutions.size(), is(equalTo(2)));
    }

    @Test
    public void goalReachedEarly() {
        CardChallengeSolver solver = new CardChallengeSolver(3, 'A', 2);
        String cards = "Kd Kc Qh Ah 7d 6d 8d 5d 9d 4d Td 3d Jd 2d Qd Ad 7c 6c 8c 5c 9c 4c Tc 3c Jc 2c " +
                "Qc Ac 6h 7h 5h 8h 4h 9h 3h Th 2h Jh Kh As 2s 3s 4s 5s 6s 7s 8s 9s Ts Js Qs Ks";
        Deck deck = new Deck(cards);
        List<Solution> solutions = solver.solve(deck);
        assertThat(solutions.size(), is(equalTo(1)));
        Solution solution = solutions.get(0);
        assertThat(solution.getActions().size(), is(equalTo(1)));
    }

    @Test
    public void deadEndWithoutClearingBoard() {
        CardChallengeSolver solver = new CardChallengeSolver(4, 'J', 0);
        String cards = "As 5d 4s 7h 7s Kh 7d Tc 5c Qh 2d Kc 9c 6h Th 6s 5h Ks Jc 6c 2c 4h 8h 8s 7c Ac " +
                "Jh Js Kd Td 2s 9d 8c 6d Qc 9h 8d 3d 9s 2h 4d 3s 3c 3h Qd Ad Jd 5s 4c Ts Ah Qs";
        Deck deck = new Deck(cards);
        List<Solution> solutions = solver.solve(deck);
        assertThat(solutions.size(), is(equalTo(1)));
        Solution solution = solutions.get(0);
        assertThat(solution.getActions().size(), is(equalTo(46)));
    }
}
