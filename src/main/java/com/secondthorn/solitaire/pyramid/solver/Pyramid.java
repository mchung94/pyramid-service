package com.secondthorn.solitaire.pyramid.solver;

import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Pyramid represents precalculated data around the 28 pyramid cards that aren't
 * based on the deck of cards being played.
 * <p>
 * First, it contains a list of all possible ways for cards to be laid out in
 * the pyramid - we can't remove a card from the pyramid unless there are no
 * cards blocking it from below.
 * <p>
 * This information is represented as a 28-bit value called pyramid flags -
 * the Nth bit indicates if the Nth card in the pyramid remains in the game or
 * if it's been removed.
 * <p>
 * Then for each arrangement of pyramid cards, there's a list of all the
 * positions of existing cards that are not blocked from below - these are the
 * cards that are available for removal without any other card being removed
 * first.
 */
public class Pyramid {

    /**
     * An array of all 1430 possible arrangements of cards in the pyramid.
     */
    public static final long[] allPyramidFlags;

    /**
     * For every possible value of pyramid flags, an array of indexes of the
     * 28 cards that are remaining in the pyramid.  This can be used to check
     * which cards on the pyramid can't be removed at all.
     */
    public static final List<int[]> allExistingIndexes;

    /**
     * For every possible value of pyramid flags, an array of indexes of the
     * cards that are uncovered and available for removal.  This helps us
     * figure out what cards can potentially be removed at each step.
     */
    public static final List<int[]> allUncoveredIndexes;

    /**
     * For each pyramid card index, a mask that excludes the cards that are
     * covering or covered by it.  The nth card in the pyramid can't be removed
     * by a card that is masked off by the nth mask.  Each value is the full 52
     * bits covering the entire deck.  The stock/waste cards (28th-51st) bits
     * in the deck are always included because they're never blocked.
     */
    static final long[] UNRELATED_CARD_MASKS = {
            0b1111111111111111111111110000000000000000000000000000L,
            0b1111111111111111111111111000000100000100001000100100L,
            0b1111111111111111111111110000001000001000010001001010L,
            0b1111111111111111111111111100000110000110001100110100L,
            0b1111111111111111111111111000001100001100011001101000L,
            0b1111111111111111111111110000011000011000110011011010L,
            0b1111111111111111111111111110000111000111001110110100L,
            0b1111111111111111111111111100001110001110011101100000L,
            0b1111111111111111111111111000011100011100111011001000L,
            0b1111111111111111111111110000111000111001110111011010L,
            0b1111111111111111111111111111000111100111101110110100L,
            0b1111111111111111111111111110001111001111011100100000L,
            0b1111111111111111111111111100011110011110111001000000L,
            0b1111111111111111111111111000111100111101110011001000L,
            0b1111111111111111111111110001111001111011110111011010L,
            0b1111111111111111111111111111100111110111101110110100L,
            0b1111111111111111111111111111001111101111001100100000L,
            0b1111111111111111111111111110011111011110011000000000L,
            0b1111111111111111111111111100111110111100110001000000L,
            0b1111111111111111111111111001111101111001110011001000L,
            0b1111111111111111111111110011111011111011110111011010L,
            0b1111111111111111111111111111110111110111101110110100L,
            0b1111111111111111111111111111101111100111001100100000L,
            0b1111111111111111111111111111011111001110001000000000L,
            0b1111111111111111111111111110111110011100010000000000L,
            0b1111111111111111111111111101111100111000110001000000L,
            0b1111111111111111111111111011111001111001110011001000L,
            0b1111111111111111111111110111111011111011110111011010L
    };

    static {
        // for each pyramid card index, bits set on the Nth card and the cards
        // covering it from below
        long[] coverMasks = new long[]{
                0b1111111111111111111111111111L,
                0b0111111011111011110111011010L,
                0b1111110111110111101110110100L,
                0b0011111001111001110011001000L,
                0b0111110011110011100110010000L,
                0b1111100111100111001100100000L,
                0b0001111000111000110001000000L,
                0b0011110001110001100010000000L,
                0b0111100011100011000100000000L,
                0b1111000111000110001000000000L,
                0b0000111000011000010000000000L,
                0b0001110000110000100000000000L,
                0b0011100001100001000000000000L,
                0b0111000011000010000000000000L,
                0b1110000110000100000000000000L,
                0b0000011000001000000000000000L,
                0b0000110000010000000000000000L,
                0b0001100000100000000000000000L,
                0b0011000001000000000000000000L,
                0b0110000010000000000000000000L,
                0b1100000100000000000000000000L,
                0b0000001000000000000000000000L,
                0b0000010000000000000000000000L,
                0b0000100000000000000000000000L,
                0b0001000000000000000000000000L,
                0b0010000000000000000000000000L,
                0b0100000000000000000000000000L,
                0b1000000000000000000000000000L
        };
        allPyramidFlags = everyPyramidFlags();
        allUncoveredIndexes = Arrays.stream(allPyramidFlags)
                .mapToObj(flags -> uncoveredIndexes(flags, coverMasks))
                .collect(Collectors.toList());
        allExistingIndexes = Arrays.stream(allPyramidFlags)
                .mapToObj(Pyramid::existingIndexes)
                .collect(Collectors.toList());
    }

    private static long[] everyPyramidFlags() {
        // calculate all possible values for pyramid flags by iterating through
        // every possible removed/remaining card combination on the bottom
        // row of cards, then figuring out all the possible combinations of
        // cards on each previous row
        TLongList allFlags = new TLongArrayList(1430);
        for (long row7 = 0; row7 < (1L << 7); row7++) {
            for (long row6 : previousRows(row7, 7)) {
                for (long row5 : previousRows(row6, 6)) {
                    for (long row4 : previousRows(row5, 5)) {
                        for (long row3 : previousRows(row4, 4)) {
                            for (long row2 : previousRows(row3, 3)) {
                                for (long row1 : previousRows(row2, 2)) {
                                    allFlags.add(row1 |
                                            row2 << 1 |
                                            row3 << 3 |
                                            row4 << 6 |
                                            row5 << 10 |
                                            row6 << 15 |
                                            row7 << 21);
                                }
                            }
                        }
                    }
                }
            }
        }
        return allFlags.toArray();
    }

    private static int[] uncoveredIndexes(long pyramidFlags, long[] coverMasks) {
        return IntStream.range(0, 28)
                .filter(i -> (1L << i) == (coverMasks[i] & pyramidFlags))
                .toArray();
    }

    private static int[] existingIndexes(long pyramidFlags) {
        return IntStream.range(0, 28)
                .filter(i -> ((1L << i) & pyramidFlags) != 0)
                .toArray();
    }

    private static int[] previousRowOptionalIndexes(long rowFlags, int numCardsInRow) {
        // given card existence flags for a row of the pyramid cards, return
        // the offsets of the previous row that don't to have a card in place
        return IntStream.range(0, numCardsInRow - 1)
                .filter(i -> (rowFlags & (0b11L << i)) == 0L)
                .toArray();
    }

    private static long previousRowOptionalCardMask(long existingOptionalCardFlags, int[] optionalIndexes) {
        // given optional card indexes and a flag where if the Nth bit is 1
        // then the Nth optionalIndex value should have a card, return a mask
        // containing 0 for each card in the row that should be removed.
        long mask = 0;
        for (int i = 0; i < optionalIndexes.length; i++) {
            if ((existingOptionalCardFlags & (1 << i)) == 0) {
                mask |= 1L << optionalIndexes[i];
            }
        }
        return ~mask;
    }

    private static long[] previousRows(long rowFlags, int numCardsInRow) {
        // get all valid card existence bit flags for the previous pyramid row
        TLongList rows = new TLongArrayList();
        long allCardsMask = (1L << (numCardsInRow - 1)) - 1;
        int[] optionalIndexes = previousRowOptionalIndexes(rowFlags, numCardsInRow);
        // iterate through all possible combinations of optional cards to remain
        for (int flags = 0; flags < (1L << optionalIndexes.length); flags++) {
            rows.add(allCardsMask & previousRowOptionalCardMask(flags, optionalIndexes));
        }
        return rows.toArray();
    }
}
