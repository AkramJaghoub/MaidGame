package Cards;

public class CardFactory {
    public static Card createCard(String suit, String value) {
        Color color = switch (suit) {
            case "♠", "♣" -> Color.BLACK;
            case "♦", "♥" -> Color.RED;
            default -> Color.JOKER; // Special case for Joker
        };
        return new Card(suit, value, color);
    }
}