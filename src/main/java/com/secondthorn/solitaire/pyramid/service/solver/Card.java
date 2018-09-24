package com.secondthorn.solitaire.pyramid.service.solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cards consisting of a Rank and Suit.  Cards are immutable, only one instance
 * of each possible Card is created in the static initialization block.  Use
 * the from() method to get Card instances, or see ALL_CARDS for a list of all
 * Card instances in suit (cdhs) followed by rank (A23456789TJQK) order.
 */
public final class Card {
    /**
     * ALL_CARDS contains all 52 possible Card values.
     */
    static final List<Card> ALL_CARDS = new ArrayList<>();

    private static final Map<String, Card> stringToCard;

    static {
        stringToCard = new HashMap<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                Card card = new Card(rank, suit);
                stringToCard.put(card.toString(), card);
                ALL_CARDS.add(card);
            }
        }
    }

    private final Rank rank;
    private final Suit suit;

    private Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    /**
     * Given a two-letter card string containing rank followed by suit symbols,
     * return the matching Card instance or else throw an
     * IllegalArgumentException.
     */
    static Card from(String cardString) {
        Card card = stringToCard.get(cardString);
        if (card == null) {
            throw new IllegalArgumentException(cardString + " is not a valid card value.");
        }
        return card;
    }

    Rank getRank() {
        return this.rank;
    }

    @Override
    public String toString() {
        return "" + rank.symbol + suit.symbol;
    }

    /**
     * Card Rank values - they can be represented by a single character.
     */
    public enum Rank {
        ACE('A'),
        TWO('2'),
        THREE('3'),
        FOUR('4'),
        FIVE('5'),
        SIX('6'),
        SEVEN('7'),
        EIGHT('8'),
        NINE('9'),
        TEN('T'),
        JACK('J'),
        QUEEN('Q'),
        KING('K');

        public final char symbol;

        Rank(char symbol) {
            this.symbol = symbol;
        }
    }

    /**
     * Card Suit values - they can be represented by a single lower-case character.
     */
    public enum Suit {
        CLUBS('c'),
        DIAMONDS('d'),
        HEARTS('h'),
        SPADES('s');

        public final char symbol;

        Suit(char symbol) {
            this.symbol = symbol;
        }
    }
}
