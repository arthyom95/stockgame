package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

    public Stock stock;
    public List<Card> cards;

    //create a randomly shuffled effect deck for the specified stock
    public Deck(Stock stock) {
        this.stock = stock;
        cards = new ArrayList<>();
        for (int effect : Card.EFFECTS) {
            cards.add(new Card(effect));
            Collections.shuffle(cards);
        }
    }

    //create a deck for a stock with a specific order for the effect cards
    public Deck(Stock stock, int... effects) {
        this.stock = stock;
        cards = new ArrayList<>();
        for (int effect : effects) {
            cards.add(new Card(effect));
        }
    }

    @Override
    public String toString() {
        return stock + " " + cards;
    }

    public static void main(String[] args) {
        Deck[] decks = new Deck[Stock.values().length];
        for (Stock s : Stock.values()) {
            decks[s.ordinal()] = new Deck(s);
        }
        for (Deck d : decks) {
            System.out.println(d);
        }
    }

}
