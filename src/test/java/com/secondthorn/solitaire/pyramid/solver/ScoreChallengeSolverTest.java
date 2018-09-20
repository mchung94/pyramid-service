package com.secondthorn.solitaire.pyramid.solver;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;


public class ScoreChallengeSolverTest {
    @Test(expected = IllegalArgumentException.class)
    public void currentScoreHigherThanGoalScore() {
        new ScoreChallengeSolver(1290, 2580);
    }

    @Test
    public void testForShortAnswerNonMaximalPoints() {
        ScoreChallengeSolver solver = new ScoreChallengeSolver(1225, 0);
        String cards = "Kd Kc Qh Ah 7d 6d 8d 5d 9d 4d Td 3d Jd 2d Qd Ad 7c 6c 8c 5c 9c 4c Tc 3c Jc 2c " +
                "Qc Ac 6h 7h 5h 8h 4h 9h 3h Th 2h Jh Kh As 2s 3s 4s 5s 6s 7s 8s 9s Ts Js Qs Ks";
        Deck deck = new Deck(cards);
        List<Solution> solutions = solver.solve(deck);
        assertThat(solutions.size(), is(equalTo(1)));
        Solution solution = solutions.get(0);
        assertThat(solution.getActions().size(), is(equalTo(15)));
        assertThat(solution.getScore(), is(equalTo(1225)));
    }

    @Test
    public void testForDeadEndAnswer() {
        ScoreChallengeSolver solver = new ScoreChallengeSolver();
        String cards = "4s 4d 4h Ah As 4c Qh Qd Qc Ad Th Ts 3s Ac Qs Jc Jd Jh Js Tc Td 2c 2d 2h 2s 3c " +
                "3d 3h Kc Kd Kh Ks 5c 6c 7c 8c 9c 5d 6d 7d 8d 9d 5h 6h 7h 8h 9h 5s 6s 7s 8s 9s";
        Deck deck = new Deck(cards);
        List<Solution> solutions = solver.solve(deck);
        assertThat(solutions.size(), is(equalTo(1)));
        Solution solution = solutions.get(0);
        assertThat(solution.getActions().size(), is(equalTo(34)));
        assertThat(solution.getScore(), is(equalTo(60)));
    }
}