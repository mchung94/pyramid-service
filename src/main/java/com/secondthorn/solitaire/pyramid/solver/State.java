package com.secondthorn.solitaire.pyramid.solver;

/**
 * States represent the "state of the world" for each step while playing
 * Pyramid Solitaire.  For speed and memory usage, states are just unboxed
 * longs and this class implements static methods to operate on them.
 * <p>
 * The secret to cramming the entire state of the game into a long value is to
 * have the state hold information that refers to the deck of cards while
 * keeping the actual deck and cards outside of the state.
 * <p>
 * The state is represented using 60 bits.
 * <p>
 * Bits 0-51 indicate if each of the 52 cards in the deck have been removed
 * or remain in the game.  Bits 0-27 are the 28 pyramid cards, and bits 28-51
 * are the 24 stock/waste cards.  In the code the 52 bits are called Deck Flags
 * and the first 28 are called Pyramid Flags.
 * <p>
 * Bits 52-57 hold a 6-bit number from 28-52 to indicate the top card of the
 * stock pile, the stock index.  Cards above the stock index are the rest of
 * the stock pile, and cards below the stock index are the waste pile.  When
 * the player recycles the waste pile, we just move the stock index back to
 * 28 and adjust it to the next remaining card.
 * <p>
 * Bits 58-59 hold a 2-bit number from 0 to 2 indicating how many times the
 * player has recycled the waste pile.
 */
public class State {
    /**
     * The initial state of the game: no cards removed, the stock index at
     * 28 (meaning empty waste pile and all remaining cards in the stock), and
     * no waste pile recycling done yet.
     */
    public static final long INITIAL_STATE = create(0xFFFFFFFFFFFFFL, 28, 0);

    /**
     * Create a state using the given deck flags, stock index, and recycle count
     */
    public static long create(long deckFlags, int stockIndex, int recycleCount) {
        long state = ((long) recycleCount << 58) | ((long) stockIndex << 52) | deckFlags;
        return adjustStockIndex(state);
    }

    /**
     * Return true if the state's 28 pyramid cards have all been removed.
     */
    public static boolean isPyramidClear(long state) {
        return (state & 0xFFFFFFFL) == 0L;
    }

    /**
     * Return the deck flags for a given state.  The deck flags are the first
     * 52 bits of the state.  If the Nth bit is 1 then the Nth card in the deck
     * remains in the game, otherwise it's been removed.
     */
    public static long getDeckFlags(long state) {
        return 0xFFFFFFFFFFFFFL & state;
    }

    /**
     * Return the pyramid flags for a given state.  The pyramid flags are the
     * first 28 bits of the state.  If the Nth bit is 1 then the Nth card in
     * the pyramid remains in the game, otherwise it's been removed.
     */
    public static long getPyramidFlags(long state) {
        return 0xFFFFFFFL & state;
    }

    /**
     * Return the stock index for a given state.  This points to the top card
     * in the stock pile.  The cards with index above this are the rest of the
     * stock pile.  When the value is 52 then the stock pile is empty.
     */
    public static int getStockIndex(long state) {
        return 0b111111 & ((int) (state >>> 52));
    }

    /**
     * Return the waste index for a given state.  This points to the top card
     * in the waste pile.  The cards with index below this are the rest of the
     * stock pile down to index 28.  When the value is 27 then the waste pile
     * is empty.
     */
    public static int getWasteIndex(long state, int stockIndex) {
        // derive this from the stock index by finding the first card remaining
        // with index below stock index but above 27 (which represents empty).
        int wasteIndex = stockIndex - 1;
        long mask = 1L << wasteIndex;
        while (!isWasteEmpty(wasteIndex) && (mask & state) == 0) {
            wasteIndex--;
            mask >>>= 1;
        }
        return wasteIndex;
    }

    /**
     * If the stock index is referring to a card in the deck that remains in
     * the game, return the given state.  Otherwise, return a new state value
     * where the stock index points to the next available card (or 52 if empty).
     */
    public static long adjustStockIndex(long state) {
        int stockIndex = getStockIndex(state);
        long mask = 1L << stockIndex;
        while (!isStockEmpty(stockIndex) && (mask & state) == 0) {
            stockIndex++;
            mask <<= 1;
        }
        return (state & 0xC0FFFFFFFFFFFFFL) | ((long) stockIndex << 52);
    }

    /**
     * Return the number of times the player has recycled the waste pile.
     * This can only be between 0 and 2 times (inclusive).
     */
    public static int getRecycleCount(long state) {
        return 0b11 & ((int) (state >> 58));
    }

    /**
     * Return true if the stock pile is empty.
     */
    public static boolean isStockEmpty(int stockIndex) {
        return stockIndex == 52;
    }

    /**
     * Return true if the waste pile is empty.
     *
     * @param wasteIndex
     * @return
     */
    public static boolean isWasteEmpty(int wasteIndex) {
        return wasteIndex == 27;
    }
}
