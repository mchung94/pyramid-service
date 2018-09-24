package com.secondthorn.solitaire.pyramid.service.solver;

import org.junit.Test;

import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class PyramidTest {
    private boolean cardExists(int index, long flags) {
        return ((1L << index) & flags) != 0L;
    }

    private boolean allDescendantsAreRemoved(int pyramidIndex, long pyramidFlags) {
        PyramidLocation location = new PyramidLocation(pyramidIndex);
        for (int i = 0; i < 28; i++) {
            PyramidLocation otherLocation = new PyramidLocation(i);
            if (otherLocation.isCovering(location) && cardExists(i, pyramidFlags)) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidPyramidFlags(long pyramidFlags) {
        return IntStream.range(0, 28)
                .noneMatch(i -> !cardExists(i, pyramidFlags) && !allDescendantsAreRemoved(i, pyramidFlags));
    }

    @Test
    public void allPyramidFlagsAreValid() {
        assertThat(Pyramid.allPyramidFlags.length, is(equalTo(1430)));
        for (long pyramidFlags : Pyramid.allPyramidFlags) {
            assertThat(isValidPyramidFlags(pyramidFlags), is(equalTo(true)));
        }
    }

    @Test
    public void existingIndexesAreValid() {
        assertThat(Pyramid.allExistingIndexes.size(), is(equalTo(Pyramid.allPyramidFlags.length)));
        for (int index = 0; index < Pyramid.allPyramidFlags.length; index++) {
            long pyramidFlags = Pyramid.allPyramidFlags[index];
            int[] expected = IntStream.range(0, 28)
                    .filter(i -> cardExists(i, pyramidFlags))
                    .toArray();
            int[] actual = Pyramid.allExistingIndexes.get(index);
            assertThat(actual, is(equalTo(expected)));
        }
    }

    @Test
    public void uncoveredIndexesAreValid() {
        assertThat(Pyramid.allUncoveredIndexes.size(), is(equalTo(Pyramid.allPyramidFlags.length)));
        for (int index = 0; index < Pyramid.allPyramidFlags.length; index++) {
            long pyramidFlags = Pyramid.allPyramidFlags[index];
            int[] expected = IntStream.range(0, 28)
                    .filter(i -> cardExists(i, pyramidFlags) && allDescendantsAreRemoved(i, pyramidFlags))
                    .toArray();
            int[] actual = Pyramid.allUncoveredIndexes.get(index);
            assertThat(actual, is(equalTo(expected)));
        }
    }

    @Test
    public void unrelatedCardMasksAreValid() {
        for (int pyramidIndex = 0; pyramidIndex < 28; pyramidIndex++) {
            PyramidLocation location = new PyramidLocation(pyramidIndex);
            long mask = Pyramid.UNRELATED_CARD_MASKS[pyramidIndex];
            for (int i = 0; i < 27; i++) {
                PyramidLocation testLocation = new PyramidLocation(i);
                boolean isUnrelated = !(testLocation.isCovering(location) ||
                        location.isCovering(testLocation) ||
                        testLocation.pyramidIndex == location.pyramidIndex);
                assertThat(cardExists(i, mask), is(equalTo(isUnrelated)));
            }
            for (int stockIndex = 28; stockIndex < 52; stockIndex++) {
                assertThat(cardExists(stockIndex, mask), is(equalTo(true)));
            }
        }
    }

    static class PyramidLocation {
        // Pyramid indexes arranged in the order they are in pyramid flags.
        //             00
        //           01  02
        //         03  04  05
        //       06  07  08  09
        //     10  11  12  13  14
        //   15  16  17  18  19  20
        // 21  22  23  24  25  26  27
        private static final int[][] PYRAMID_INDEXES = {
                {0, 2, 5, 9, 14, 20, 27},
                {1, 4, 8, 13, 19, 26},
                {3, 7, 12, 18, 25},
                {6, 11, 17, 24},
                {10, 16, 23},
                {15, 22},
                {21}
        };

        int pyramidIndex;
        int row;
        int column;

        // Get the location inside PYRAMID_INDEX that pyramidIndex is at.
        PyramidLocation(int pyramidIndex) {
            this.pyramidIndex = pyramidIndex;
            for (int row = 0; row < 7; row++) {
                for (int column = 0; column < 7 - row; column++) {
                    if (PYRAMID_INDEXES[row][column] == pyramidIndex) {
                        this.row = row;
                        this.column = column;
                        return;
                    }
                }
            }
        }

        // Return true if the given location is covered by this.  Another way
        // to put it is that the location passed in is an ancestor of this
        // location.
        boolean isCovering(PyramidLocation location) {
            return ((location.row <= this.row) &&
                    (location.column <= this.column) &&
                    (location.pyramidIndex != this.pyramidIndex));
        }
    }

}
