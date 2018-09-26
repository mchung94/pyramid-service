package com.secondthorn.solitaire.pyramid.service.solver;

import com.secondthorn.solitaire.pyramid.service.model.Solution;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class CardChallengeSolverTest {
    @Test(expected = IllegalArgumentException.class)
    public void constructorAlreadyPastGoal() {
        int goalNumToClear = 3;
        int currentNumCleared = 4;
        new CardChallengeSolver(goalNumToClear - currentNumCleared, 'A');
    }

    @Test
    public void twoSolutionTest() {
        CardChallengeSolver solver = new CardChallengeSolver(4, '4');
        String cards = "Tc Ac Js 5d 2h 3h As Th Qd 7h 3c Td 8s Kh 6d Ks 5c 6h 9h 3d 5h Jh Kc 8d Jd 8c " +
                "7d 7c 2d Qs 9s 2c 3s 7s Ah Ad 4h 6s 6c 4c 2s 4d Qh 9d Jc 4s Qc Ts Kd 5s 9c 8h";
        Deck deck = new Deck(cards);
        List<Solution> solutions = solver.solve(deck);
        assertThat(solutions.size(), is(equalTo(2)));
        assertThat(
                solutions.stream().map(Solution::getDescription).collect(Collectors.toList()),
                containsInAnyOrder(
                        "Remove 2 cards of rank 4. Get 1275 points in 54 steps while clearing the board.",
                        "Remove 3 cards of rank 4. Get 190 points in 57 steps without clearing the board."
                )
        );
    }

    @Test
    public void goalReachedEarly() {
        CardChallengeSolver solver = new CardChallengeSolver(1, 'A');
        String cards = "Kd Kc Qh Ah 7d 6d 8d 5d 9d 4d Td 3d Jd 2d Qd Ad 7c 6c 8c 5c 9c 4c Tc 3c Jc 2c " +
                "Qc Ac 6h 7h 5h 8h 4h 9h 3h Th 2h Jh Kh As 2s 3s 4s 5s 6s 7s 8s 9s Ts Js Qs Ks";
        Deck deck = new Deck(cards);
        List<Solution> solutions = solver.solve(deck);
        assertThat(solutions.size(), is(equalTo(1)));
        Solution solution = solutions.get(0);
        assertThat(solution.getSteps().size(), is(equalTo(1)));
        assertThat(solution.getScore(), is(equalTo(5)));
        assertThat(solution.isBoardCleared(), is(equalTo(false)));
        String expected = "Remove 1 card of rank A. Get 5 points in 1 step without clearing the board.";
        assertThat(solution.getDescription(), is(equalTo(expected)));
    }

    @Test
    public void deadEndWithoutClearingBoard() {
        CardChallengeSolver solver = new CardChallengeSolver(4, 'J');
        String cards = "As 5d 4s 7h 7s Kh 7d Tc 5c Qh 2d Kc 9c 6h Th 6s 5h Ks Jc 6c 2c 4h 8h 8s 7c Ac " +
                "Jh Js Kd Td 2s 9d 8c 6d Qc 9h 8d 3d 9s 2h 4d 3s 3c 3h Qd Ad Jd 5s 4c Ts Ah Qs";
        Deck deck = new Deck(cards);
        List<Solution> solutions = solver.solve(deck);
        assertThat(solutions.size(), is(equalTo(1)));
        Solution solution = solutions.get(0);
        assertThat(solution.getSteps().size(), is(equalTo(46)));
        assertThat(solution.getScore(), is(equalTo(55)));
        assertThat(solution.isBoardCleared(), is(equalTo(false)));
        assertThat(solution.getDescription(), is(equalTo("Remove 3 cards of rank J. Get 55 points in 46 steps without clearing the board.")));
    }
}
