package com.secondthorn.solitaire.pyramid.service.solver;

import com.secondthorn.solitaire.pyramid.service.exception.InvalidParameterException;
import gnu.trove.list.TIntList;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class DeckTest {
    private static final String sortedDeckString = String.join("",
            "Ac 2c 3c 4c 5c 6c 7c 8c 9c Tc Jc Qc Kc ",
            "Ad 2d 3d 4d 5d 6d 7d 8d 9d Td Jd Qd Kd ",
            "Ah 2h 3h 4h 5h 6h 7h 8h 9h Th Jh Qh Kh ",
            "As 2s 3s 4s 5s 6s 7s 8s 9s Ts Js Qs Ks"
    );
    private static final Deck sortedDeck = new Deck(sortedDeckString);

    @Test(expected = InvalidParameterException.class)
    public void missingCardsThrowsInvalidParameterException() {
        new Deck(String.join("",
                "Ac 2c 3c 4c 5c 6c 7c 8c 9c Tc Jc Qc Kc ",
                "Ad 2d 3d 4d 5d 6d 7d 8d 9d Td Jd Qd Kd ",
                "Ah 2h 3h 4h    6h 7h 8h 9h Th Jh Qh Kh ",
                "As 2s 3s 4s 5s 6s 7s 8s 9s Ts Js Qs Ks"
        ));
    }

    @Test(expected = InvalidParameterException.class)
    public void duplicateCardsThrowsInvalidParameterException() {
        new Deck(String.join("",
                "Ac 2c 3c 4c 5c 6c 7c 8c 9c Tc Jc Qc Kc ",
                "Ad 2d 3d 4d 5d 6d 7d 8d 9d Td Jd Qd Kd ",
                "Ah 2h 6d 4h 5h 6d 7h 8h 9h Th Jh Kh Kh ",
                "As 2s 3s 4s 5s 6d 7s 8s 9s Ts Js Qs Ks"
        ));
    }

    @Test
    public void cardAtOnSortedDeck() {
        IntStream.range(0, 52).forEach(i -> {
            int start = i * 3;
            Card expected = Card.from(sortedDeckString.substring(start, start + 2));
            assertThat(sortedDeck.cardAt(i), is(equalTo(expected)));
        });
    }

    @Test
    public void cardValuesOnSortedDeck() {
        IntStream.range(0, 52).forEach(i -> {
            int expected = (i % 13) + 1;
            assertThat(sortedDeck.cardValue(i), is(equalTo(expected)));
        });
    }

    @Test
    public void isKingOnSortedDeck() {
        IntStream.range(0, 52).forEach(i -> {
            boolean expected = (i % 13) == 12;
            assertThat(sortedDeck.isKing(i), is(equalTo(expected)));
        });
    }

    @Test
    public void formattedDeckStringOnSortedDeck() {
        String expected = "            Ac\n" +
                "          2c  3c\n" +
                "        4c  5c  6c\n" +
                "      7c  8c  9c  Tc\n" +
                "    Jc  Qc  Kc  Ad  2d\n" +
                "  3d  4d  5d  6d  7d  8d\n" +
                "9d  Td  Jd  Qd  Kd  Ah  2h\n" +
                "3h 4h 5h 6h 7h 8h 9h Th Jh Qh Kh As 2s 3s 4s 5s 6s 7s 8s 9s Ts Js Qs Ks";
        assertThat(sortedDeck.formattedDeckString(), is(equalTo(expected)));
    }

    // Make a state with a gap of missing cards in the deckFlags so that the
    // returned state's wasteIndex will be derived correctly to match the given
    // wasteIndex.
    private long makeState(long pyramidFlags, int stockIndex, int wasteIndex, int recycleCount) {
        long stockFlags = 0xFFFFFFL << 28;
        int numBitsToClear = stockIndex - wasteIndex - 1;
        long mask = ((1L << numBitsToClear) - 1) << (wasteIndex + 1);
        stockFlags ^= mask;
        return ((long) recycleCount << 58) | ((long) stockIndex << 52) | stockFlags | pyramidFlags;
    }

    // return a mask to remove cards from a state using XOR
    private long removalMask(int... indexes) {
        long mask = 0L;
        for (int index : indexes) {
            mask |= 1L << index;
        }
        return mask;
    }

    // An alternate implementation of the unclearableMasks function, which
    // generates all the masks to determine if there exists a pyramid card that
    // can't be removed.
    private long[] unclearableMasks(int[] pyramidIndexes, Deck deck) {
        TLongSet unclearableMasks = new TLongHashSet();
        for (int pyramidIndex : pyramidIndexes) {
            int matchingCardValue = 13 - deck.cardValue(pyramidIndex);
            if (matchingCardValue != 0) {
                long mask = 0L;
                for (int i = 0; i < 52; i++) {
                    if (deck.cardValue(i) == matchingCardValue) {
                        mask |= 1L << i;
                    }
                }
                unclearableMasks.add(mask & Pyramid.UNRELATED_CARD_MASKS[pyramidIndex]);
            }
        }
        return unclearableMasks.toArray();
    }

    private void assertArraysContainSameValues(long[] actual, long[] expected) {
        List<Long> actualList = Arrays.stream(actual).boxed().sorted().collect(Collectors.toList());
        List<Long> expectedList = Arrays.stream(expected).boxed().sorted().collect(Collectors.toList());
        assertThat(actualList, is(equalTo(expectedList)));
    }

    @Test
    public void unclearableMasksAreValid() {
        for (int i = 0; i < Pyramid.allPyramidFlags.length; i++) {
            long pyramidFlags = Pyramid.allPyramidFlags[i];
            int[] allIndexes = Pyramid.allExistingIndexes.get(i);
            long[] expected = unclearableMasks(allIndexes, sortedDeck);
            long[] actual = sortedDeck.getUnclearableMasks(pyramidFlags);
            assertArraysContainSameValues(actual, expected);
        }
    }

    // An alternate implementation of creating successor masks for testing
    private long[] masks(Deck deck, int[] uncoveredIndexes, int stockIndex, int wasteIndex, int recycleCount) {
        TLongList masks = new TLongArrayList();
        TIntList indexes = new TIntArrayList(uncoveredIndexes);
        if (stockIndex != 52) {
            indexes.add(stockIndex);
        }
        if (wasteIndex != 27) {
            indexes.add(wasteIndex);
        }
        for (int i = 0; i < indexes.size(); i++) {
            int cardIndex1 = indexes.get(i);
            int cardValue = deck.cardValue(cardIndex1);
            if (cardValue == 13) {
                masks.add(removalMask(cardIndex1));
            } else {
                for (int j = i + 1; j < indexes.size(); j++) {
                    int cardIndex2 = indexes.get(j);
                    if (cardValue + deck.cardValue(cardIndex2) == 13) {
                        masks.add(removalMask(cardIndex1, cardIndex2));
                    }
                }
            }
        }
        if (stockIndex < 52) {
            long drawACard = (long) (stockIndex ^ (stockIndex + 1)) << 52;
            masks.add(drawACard);
        }
        if ((stockIndex == 52) && (recycleCount < 2)) {
            long resetStockIndex = (long) (stockIndex ^ 28) << 52;
            long incrementRecycleCount = (long) (recycleCount ^ (recycleCount + 1)) << 58;
            masks.add(resetStockIndex | incrementRecycleCount);
        }
        return masks.toArray();
    }

    @Test
    public void successorMasks() {
        for (int i = 0; i < Pyramid.allPyramidFlags.length; i++) {
            long pyramidFlags = Pyramid.allPyramidFlags[i];
            int[] uncoveredIndexes = Pyramid.allUncoveredIndexes.get(i);
            for (int stockIndex = 28; stockIndex < 53; stockIndex++) {
                for (int wasteIndex = 27; wasteIndex < stockIndex; wasteIndex++) {
                    for (int recycleCount = 0; recycleCount < 3; recycleCount++) {
                        long state = makeState(pyramidFlags, stockIndex, wasteIndex, recycleCount);
                        long[] actual = sortedDeck.getSuccessorMasks(state);
                        long[] expected = masks(sortedDeck, uncoveredIndexes, stockIndex, wasteIndex, recycleCount);
                        assertArraysContainSameValues(actual, expected);
                    }
                }
            }
        }
    }
}
