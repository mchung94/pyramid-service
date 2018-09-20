package com.secondthorn.solitaire.pyramid.solver;

import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A Card Deck in Pyramid Solitaire contains one of each of the 52 standard
 * playing cards.  The first 28 cards will be the pyramid cards and the
 * remaining 24 will be the stock/waste cards.  When the game starts, the 29th
 * card will be the top of the stock pile and the 52nd card will be the bottom.
 * <p>
 * Decks contain precalculated information about the cards that will be useful
 * to the solver.  Use an index from 0 to 51, a deckIndex, to look up
 * information about a given card in the deck.
 */
public class Deck {
    private Card[] cards;
    private int[] values;
    private long[] cardRankMasks;
    private TLongObjectMap<long[]> unclearableMasks;
    private TLongObjectMap<long[][][][]> successorMasks;

    /**
     * Create a Deck out of a String containing Cards in their two-letter
     * String format.  Whitespace and other characters are ignored, only the
     * valid two-letter Card strings are collected.
     * <p>
     * This will throw an IllegalArgumentException if the Deck isn't a standard
     * 52 card deck (if there's any missing or duplicate cards).
     */
    public Deck(String deckString) {
        List<Card> cardList = readCards(deckString);
        Map<Card, Integer> counts = countCards(cardList);
        Map<Card, Integer> duplicates = duplicateCardCounts(counts);
        List<Card> missing = missingCards(counts);
        // if there's any problem, we want to know all three values to fix it
        if (cardList.size() != 52 || duplicates.size() > 0 || missing.size() > 0) {
            String message = String.format(
                    "The deck isn't a standard 52 card deck: " +
                            "Number of cards: %d, Duplicates: %s, Missing: %s",
                    cardList.size(), duplicates, missing);
            throw new IllegalArgumentException(message);
        }
        this.cards = cardList.toArray(new Card[52]);
        this.values = calcCardValues();
        this.cardRankMasks = calcCardRankMasks(values);
        this.unclearableMasks = calcUnclearableMasks();
        this.successorMasks = calcSuccessorMasks();
    }

    /**
     * Return the Card at the given deck index.  The first card is the top of
     * the Pyramid, and the last card will be the bottom of the stock pile.
     */
    public Card cardAt(int deckIndex) {
        return cards[deckIndex];
    }

    /**
     * Return the value of the Card at the given deck index.  Aces are always
     * one, and Kings are thirteen.
     */
    public int cardValue(int deckIndex) {
        return values[deckIndex];
    }

    /**
     * Return true if the Card at the given deck index is a King.
     */
    public boolean isKing(int deckIndex) {
        return cards[deckIndex].getRank().symbol == 'K';
    }

    /**
     * For a given card value, return a bit mask with the bits set only for the
     * four cards in the deck of that value.  This is used to find out if there
     * are any cards of a particular rank left in the game.
     */
    long cardRankMask(int value) {
        return cardRankMasks[value];
    }

    /**
     * Return an array of masks to check against a state to see if there are
     * any cards on the pyramid that can't be removed.
     */
    public long[] getUnclearableMasks(long state) {
        long pyramidFlags = State.getPyramidFlags(state);
        return unclearableMasks.get(pyramidFlags);
    }

    /**
     * Return an array of masks to XOR with the given state to get its
     * successor states - they still have to be adjusted after XOR by calling
     * State.adjustStockIndex() afterwards.
     */
    public long[] getSuccessorMasks(long state) {
        long pyramidFlags = State.getPyramidFlags(state);
        int stockIndex = State.getStockIndex(state);
        int wasteIndex = State.getWasteIndex(state, stockIndex);
        int recycleCount = State.getRecycleCount(state);
        return successorMasks.get(pyramidFlags)[stockIndex][wasteIndex][recycleCount];
    }

    /**
     * Return the deck as a formatted human-readable String.
     */
    public String formattedDeckString() {
        return String.format("            %s\n" +
                        "          %s  %s\n" +
                        "        %s  %s  %s\n" +
                        "      %s  %s  %s  %s\n" +
                        "    %s  %s  %s  %s  %s\n" +
                        "  %s  %s  %s  %s  %s  %s\n" +
                        "%s  %s  %s  %s  %s  %s  %s\n" +
                        "%s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s",
                (Object[]) cards);
    }

    // Look for every two-letter card in deckString and return a list of the
    // cards in it.  This is slow but doesn't cause problems with assuming
    // whitespace or separators, or accidentally creating cards that weren't
    // there before when stripping whitespace and merging letters together.
    private List<Card> readCards(String deckString) {
        List<Card> cardList = new ArrayList<>();
        for (int i = 0; i < deckString.length() - 1; i++) {
            try {
                cardList.add(Card.from(deckString.substring(i, i + 2)));
            } catch (IllegalArgumentException ex) {
                // do nothing, this is expected when there isn't a Card here
            }
        }
        return cardList;
    }

    private Map<Card, Integer> countCards(List<Card> cards) {
        Map<Card, Integer> counts = new HashMap<>();
        for (Card card : Card.ALL_CARDS) {
            counts.put(card, 0);
        }
        for (Card card : cards) {
            counts.put(card, counts.get(card) + 1);
        }
        return counts;
    }

    private Map<Card, Integer> duplicateCardCounts(Map<Card, Integer> counts) {
        return counts.entrySet().stream()
                .filter(e -> e.getValue() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private List<Card> missingCards(Map<Card, Integer> counts) {
        return counts.entrySet().stream()
                .filter(e -> e.getValue() == 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // In Pyramid Solitaire, Aces are always 1 and Kings are 13.
    private int[] calcCardValues() {
        return Arrays.stream(cards)
                .mapToInt(card -> "A23456789TJQK".indexOf(card.getRank().symbol) + 1)
                .toArray();
    }

    // For each card value, build a bit mask with the bits set for the
    // positions of the four cards in the deck with that value.  So index 1
    // would be a mask with the bits for each Ace's position set to 1.
    private long[] calcCardRankMasks(int[] values) {
        long[] cardBucketMasks = new long[14];
        for (int i = 0; i < values.length; i++) {
            cardBucketMasks[values[i]] |= (1L << i);
        }
        return cardBucketMasks;
    }

    private TLongObjectMap<long[]> calcUnclearableMasks() {
        TLongObjectMap<long[]> map = new TLongObjectHashMap<>();
        for (int i = 0; i < Pyramid.allPyramidFlags.length; i++) {
            long pyramidFlags = Pyramid.allPyramidFlags[i];
            int[] existingIndexes = Pyramid.allExistingIndexes.get(i);
            TLongList masks = new TLongArrayList();
            for (int pyramidIndex : existingIndexes) {
                int cardValue = cardValue(pyramidIndex);
                if (cardValue != 13) {
                    long mask = cardRankMask(13 - cardValue);
                    mask &= Pyramid.UNRELATED_CARD_MASKS[pyramidIndex];
                    if (!masks.contains(mask)) {
                        masks.add(mask);
                    }
                }
            }
            map.put(pyramidFlags, masks.toArray());
        }
        return map;
    }

    // Given the deck of cards, precalculate data to help calculate the
    // successor state for every possible state.  For every possible combo
    // of pyramid cards remaining + stock pile top card + waste pile top card,
    // have a list of masks to XOR with the state to determine its successor
    // states.  Each result after XOR has to have its stock index adjusted in
    // case it is no longer pointing to an existing card.
    //
    // The return value is a map where the key is a pyramid flags value.
    // The value is a long[][][][] which is indexed by stock index, waste index,
    // and recycle count, and the last part is the array of successor masks.
    private TLongObjectMap<long[][][][]> calcSuccessorMasks() {
        TLongObjectMap<long[][][][]> map = new TLongObjectHashMap<>();
        for (int i = 0; i < Pyramid.allPyramidFlags.length; i++) {
            long pyramidFlags = Pyramid.allPyramidFlags[i];
            int[] uncoveredIndexes = Pyramid.allUncoveredIndexes.get(i);
            long[][][][] successorMasks = successorMasks(uncoveredIndexes);
            map.put(pyramidFlags, successorMasks);
        }
        return map;
    }

    // Calculate the successor masks for a given pyramid flags (representing
    // the pyramid cards remaining in the game).  This is designed to run fast
    // so it's caching intermediate results.
    private long[][][][] successorMasks(int[] uncoveredIndexes) {
        long[][][][] successorMasks = new long[53][][][];
        TLongList pyramidMasks = calcPyramidMasks(uncoveredIndexes);
        TLongList[] stockMasks = calcStockMasks(uncoveredIndexes);
        for (int stockIndex = 28; stockIndex < 53; stockIndex++) {
            long[][][] wMasks = new long[52][][];
            successorMasks[stockIndex] = wMasks;
            TLongList dspMasks = new TLongArrayList(pyramidMasks);
            dspMasks.addAll(stockMasks[stockIndex]);
            long drawMask = calcDrawMask(stockIndex);
            if (drawMask != 0) {
                dspMasks.add(drawMask);
            }
            for (int wasteIndex = 27; wasteIndex < stockIndex; wasteIndex++) {
                long[][] rMasks = new long[3][];
                wMasks[wasteIndex] = rMasks;
                TLongList wdspMasks = new TLongArrayList(dspMasks);
                wdspMasks.addAll(stockMasks[wasteIndex]);
                long swMask = calcStockWasteMask(stockIndex, wasteIndex);
                if (swMask != 0) {
                    wdspMasks.add(swMask);
                }
                long[] wdspMaskArray = wdspMasks.toArray();
                for (int recycleCount = 0; recycleCount < 3; recycleCount++) {
                    long recycleMask = calcRecycleMask(stockIndex, recycleCount);
                    if (recycleMask != 0) {
                        TLongList masks = new TLongArrayList(wdspMaskArray);
                        masks.add(recycleMask);
                        rMasks[recycleCount] = masks.toArray();
                    } else {
                        rMasks[recycleCount] = wdspMaskArray;
                    }
                }
            }
        }
        return successorMasks;
    }

    // Return a mask that can remove one card from a state using XOR.
    // Be careful to only use this on a card that hasn't been removed yet -
    // XORing a bit with 1 toggles its value so if you try to remove a card
    // that isn't there, this would add it back to the game.
    private long mask1(int i) {
        return 1L << i;
    }

    // Return a mask that can remove two cards from a state using XOR
    private long mask2(int i1, int i2) {
        return mask1(i1) | mask1(i2);
    }

    // Return true if the two cards add up to 13 (able to be removed together)
    private boolean isMatch(int i1, int i2) {
        return values[i1] + values[i2] == 13;
    }

    // Calculate successor XOR masks involving the card at deckIndex.  Remove
    // it by itself if it's a king, or with one of the cards at otherIndexes
    // starting from otherIndexes[firstOtherIndex]
    private TLongList calcXORSuccessorMasks(int deckIndex, int[] otherIndexes, int firstOtherIndex) {
        TLongList masks = new TLongArrayList();
        if (isKing(deckIndex)) {
            masks.add(mask1(deckIndex));
        } else {
            for (int i = firstOtherIndex; i < otherIndexes.length; i++) {
                if (isMatch(deckIndex, otherIndexes[i])) {
                    masks.add(mask2(deckIndex, otherIndexes[i]));
                }
            }
        }
        return masks;
    }

    // Calculate successor XOR masks involving only the cards in the pyramid.
    private TLongList calcPyramidMasks(int[] uncoveredIndexes) {
        TLongList masks = new TLongArrayList();
        for (int i = 0; i < uncoveredIndexes.length; i++) {
            masks.addAll(calcXORSuccessorMasks(uncoveredIndexes[i], uncoveredIndexes, i + 1));
        }
        return masks;
    }

    // Calculate successor XOR masks involving the removal of one stock card,
    // possibly with a card in the pyramid.  The resulting array is meant to
    // be indexed by stock or waste index, and leads to a TLongList of masks.
    private TLongList[] calcStockMasks(int[] uncoveredIndexes) {
        TLongList[] masks = new TLongArrayList[53];
        masks[27] = new TLongArrayList();  // no results for empty waste index
        masks[52] = new TLongArrayList();  // no results for empty stock index
        for (int i = 28; i < 52; i++) {
            masks[i] = new TLongArrayList();
            if (isKing(i)) {
                masks[i].add(mask1(i));
            } else {
                for (int j : uncoveredIndexes) {
                    if (isMatch(i, j)) {
                        masks[i].add(mask2(i, j));
                    }
                }
            }
        }
        return masks;
    }

    // Return a successor XOR mask to remove the stock and waste cards together
    // if possible, or 0 otherwise.
    private long calcStockWasteMask(int stockIndex, int wasteIndex) {
        if (!State.isStockEmpty(stockIndex) && !State.isWasteEmpty(wasteIndex) && isMatch(stockIndex, wasteIndex)) {
            return mask2(stockIndex, wasteIndex);
        }
        return 0L;
    }

    // Return a successor XOR mask to draw a card from the stock pile if
    // possible, or 0 otherwise.
    private long calcDrawMask(int stockIndex) {
        if (!State.isStockEmpty(stockIndex)) {
            return (long) (stockIndex ^ stockIndex + 1) << 52;
        }
        return 0L;
    }

    // Return a successor XOR mask to recycle the waste pile if possible, or
    // 0 otherwise.
    private long calcRecycleMask(int stockIndex, int recycleCount) {
        if (State.isStockEmpty(stockIndex) && (recycleCount < 2)) {
            long recycleMask = (long) (recycleCount ^ recycleCount + 1) << 58;
            long stockMask = (stockIndex ^ 28L) << 52;
            return recycleMask | stockMask;
        }
        return 0L;
    }
}
