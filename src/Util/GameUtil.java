package Util;

import Cards.Card;
import Cards.Deck;
import Player.Player;
import Player.PlayerQueue;

import java.util.List;

public class GameUtil {
    // Private constructor to prevent instantiation
    private GameUtil() {}

    public synchronized static void dealCardsToPlayers() {
        PlayerQueue playerQueue = PlayerQueue.getInstance();
        Deck deck = Deck.getInstance();

        System.out.println("\nDiscarding cards before starting the game...\n");
        int numberOfPlayers = playerQueue.size();
        int cardsPerPlayer = deck.getCards().size() / numberOfPlayers;
        int extraCards = deck.getCards().size() % numberOfPlayers;

        int playerIndex = 0;
        for (Player player : playerQueue.getQueue()) {
            int numPlayerCards = cardsPerPlayer + (playerIndex < extraCards ? 1 : 0);
            player.addCardsToHand(deck.dealCard(numPlayerCards));
            player.discardMatchingPairs();
            playerIndex++;
        }
    }

    public synchronized static void printHands() {
        PlayerQueue playerQueue = PlayerQueue.getInstance();
        System.out.println("***************************** Current Hands *****************************\n");
        for (Player player : playerQueue.getQueue()) {
            if (!player.getCardsInHand().isEmpty()) {
                System.out.println("Player " + player.getPlayerNumber() + "'s Cards:");
                printPlayerCards(player);
            }
        }
    }

    private synchronized static void printPlayerCards(Player player) { // Made private, as it seems to be only used within this class
        List<Card> playerHands = player.getCardsInHand();
        for (int i = 0; i < playerHands.size(); i++) {
            if (i > 0 && i % 4 == 0) {
                System.out.println();
            }
            System.out.print(playerHands.get(i));
            if (i < playerHands.size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("\n");
    }
}