package com.secondthorn.solitaire.pyramid.service.solver;

import org.junit.Test;

import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class StateTest {
    private static long unadjustedState(long deckFlags, int stockIndex, int recycleCount) {
        return deckFlags | ((long) stockIndex << 52) | ((long) recycleCount << 58);
    }

    private static final long FULL_DECK_FLAGS = 0xFFFFFFFFFFFFFL;
    private static final long FULL_STATE = unadjustedState(FULL_DECK_FLAGS, 28, 0);
    private static final long END_STATE = unadjustedState(0x0L, 52, 2);

    @Test
    public void initialState() {
        assertThat(State.INITIAL_STATE, is(equalTo(unadjustedState(FULL_DECK_FLAGS, 28, 0))));
    }

    @Test
    public void create() {
        assertThat(State.create(FULL_DECK_FLAGS, 28, 0), is(equalTo(FULL_STATE)));
        assertThat(State.create(0x0L, 52, 2), is(equalTo(END_STATE)));
    }

    @Test
    public void isPyramidClear() {
        assertThat(State.isPyramidClear(FULL_STATE), is(equalTo(false)));
        assertThat(State.isPyramidClear(END_STATE), is(equalTo(true)));
    }

    @Test
    public void getDeckFlags() {
        assertThat(State.getDeckFlags(FULL_STATE), is(equalTo(FULL_DECK_FLAGS)));
        assertThat(State.getDeckFlags(END_STATE), is(equalTo(0L)));
    }

    @Test
    public void getPyramidFlags() {
        assertThat(State.getPyramidFlags(FULL_STATE), is(equalTo(FULL_DECK_FLAGS & 0xFFFFFFFL)));
        assertThat(State.getPyramidFlags(END_STATE), is(equalTo(0L)));
    }

    @Test
    public void getWasteIndex() {
        assertThat(State.getWasteIndex(State.getDeckFlags(FULL_STATE), State.getStockIndex(FULL_STATE)), is(equalTo(27)));
        assertThat(State.getWasteIndex(State.getDeckFlags(END_STATE), State.getStockIndex(END_STATE)), is(equalTo(27)));
        int emptyStockPileIndex = 52;
        long noCardsRemovedYet = 0xFFFFFFFFFFFFFL;
        long allStockAndWasteCardsRemovedExceptFirst = 0x000001FFFFFFFL;
        assertThat(State.getWasteIndex(noCardsRemovedYet, emptyStockPileIndex), is(equalTo(51)));
        assertThat(State.getWasteIndex(allStockAndWasteCardsRemovedExceptFirst, emptyStockPileIndex), is(equalTo(28)));
    }

    @Test
    public void adjustStockIndex() {
        assertThat(State.adjustStockIndex(unadjustedState(0x0L, 28, 2)), is(equalTo(END_STATE)));
    }

    @Test
    public void getRecycleCount() {
        assertThat(State.getRecycleCount(FULL_STATE), is(equalTo(0)));
        assertThat(State.getRecycleCount(END_STATE), is(equalTo(2)));
    }

    @Test
    public void isStockEmpty() {
        IntStream.rangeClosed(28, 52).forEach(i -> assertThat(State.isStockEmpty(i), is(equalTo(i == 52))));
    }

    @Test
    public void isWasteEmpty() {
        IntStream.rangeClosed(27, 51).forEach(i -> assertThat(State.isWasteEmpty(i), is(equalTo(i == 27))));
    }
}
