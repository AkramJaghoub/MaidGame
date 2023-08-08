import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Deck {
    private final ArrayList<Card> cards;
    private static Deck instance;

    public static Deck getInstance() {
        if (instance == null)
            instance = new Deck();
        return instance;
    }

    private Deck() {
        cards = new ArrayList<>();
        initializeCards();
    }

    private void initializeCards(){
        String[] suits = {"♠", "♣", "♦", "♥"};
        String[] values = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        for (String suit : suits) {
            for (String value : values) {
                Card card = CardFactory.createCard(suit, value);
                cards.add(card);
            }
        }
        Card card = CardFactory.createCard("\uD83C\uDCCF", "Joker");
        cards.add(card);
        Collections.shuffle(cards);
    }

    public ArrayList<Card> getCards(){
        return cards;
    }

    public ArrayList<Card> dealCard(int cardsPerPlayer) {
        ArrayList<Card> hand = new ArrayList<>(cards.subList(0, cardsPerPlayer));
        cards.subList(0, cardsPerPlayer).clear();
        return hand;
    }
}