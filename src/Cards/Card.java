package Cards;

public class Card {
    private final String suit;
    private final String value;
    private final Color color;

    public Card(String suit, String value, Color color) {

        this.suit = suit;
        this.value = value;
        this.color = color;
    }


    public String getSuit() {
        return suit;
    }

    public String getValue() {
        return value;
    }

    public Color getColor() {
        return color;
    }


    public boolean matches(Card otherCard) {
        return this.value.equals(otherCard.value) && this.color == otherCard.color;
    }

    public boolean isJoker() {
        return getColor() == Color.JOKER;
    }


    @Override
    public String toString() {
        return "[" + suit + ' ' + value + ", " + color + ']';
    }
}