import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Player implements Runnable {
    private final int playerNumber;
    private final ArrayList<Card> hand;
    private final Object lock; // Synchronization lock for player operations
    private volatile boolean isGameFinished = false; // Volatile flag for synchronization
    private final boolean isDebug = false; // Debug flag

    PlayerQueue playerQueue;

    private MaidGame gameInstance;

    public void setGameInstance(MaidGame gameInstance) {
        this.gameInstance = gameInstance;
    }

    public MaidGame getGameInstance() {
        return gameInstance;
    }
    public Player(int playerNumber, Object lock) {
        this.hand = new ArrayList<>();
        this.playerNumber = playerNumber;
        playerQueue = PlayerQueue.getInstance();
        this.lock = lock;
    }

    public Card takeRandomCard() throws InterruptedException {
        Random random = new Random();
        return hand.remove(random.nextInt(hand.size()));
    }

    public void addCardsToHand(ArrayList<Card> cards) {
        hand.addAll(cards);
    }

    public void setGameFinisher(boolean finish){
        this.isGameFinished = finish;
    }


    @Override
    public void run() {
        System.out.println("Player " + (playerNumber + 1) + " starting...");
        while (!isGameFinished) {
            synchronized (lock) {
                while ((this != playerQueue.getCurrentPlayer() || this.gameInstance != playerQueue.getCurrentPlayer().getGameInstance()) && !isGameFinished) {
                    try {
                        System.out.println("Player " + playerNumber + " waiting...");
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                if (isGameFinished) break; // Add this line to exit if the game is over
                if (!hand.isEmpty()) {
                    takeTurn(playerQueue.getQueue().peek()); // Take turn but do not fetch next player here
                }
                lock.notifyAll();
            }
        }
        System.out.println("Player " + (playerNumber + 1) + " ended...");
    }

    public ArrayList<Card> getCardsInHand() {
        return hand;
    }

    public boolean isJoker(Card card) {
        return card.getColor() == Color.JOKER;
    }

    public boolean hasJokerAndOneMoreCard(List<Card> hand) {
        return hand.size() == 2 && hand.stream().anyMatch(this::isJoker);
    }

    public void takeTurn(Player nextPlayer) {
        System.out.println("Player " + (playerNumber + 1) + " taking turn...");
        try {
            if (hand.isEmpty() || nextPlayer.getCardsInHand().isEmpty() ||
                    (hand.size() == 2 && hasJokerAndOneMoreCard(hand)) ||
                    (nextPlayer.getCardsInHand().size() == 1 && nextPlayer.getCardsInHand().get(0).getColor() == Color.JOKER)) {
                // Handle this special case based on the rules of your game
                return;
            }

            Card takenCard = nextPlayer.takeRandomCard();
            System.out.println("Player " + (playerNumber + 1) + " took " + takenCard + " from Player " + (nextPlayer.getPlayerNumber() + 1));
            boolean matched = false;
            for (int i = 0; i < hand.size(); i++) {
                if (takenCard.matches(hand.get(i))) {
                    System.out.println("Player " + (playerNumber + 1) + " matched " + takenCard + " with " + hand.get(i) + " and discarded both");
                    hand.remove(i);
                    matched = true;
                    break; // Exiting the loop as match found
                }
            }
            if (!matched) {
                hand.add(takenCard); // Adding to hand if no match found
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Player " + (playerNumber + 1) + " turn ended.");
    }

    public void discardMatchingPairs() {
        int i = 0;
        while (i < hand.size()) {
            Card card1 = hand.get(i);
            boolean foundMatch = false;
            for (int j = i + 1; j < hand.size(); j++) {
                Card card2 = hand.get(j);
                if (card1.matches(card2)) {
                    System.out.println("Player " + (playerNumber + 1) + " matched " + card1 + " with " + card2 + " and discarded both");
                    hand.remove(j);
                    hand.remove(i);
                    foundMatch = true;
                    break; // Exit inner loop, since we found a match for this card
                }
            }
            if (!foundMatch) i++; // Increment i only if no match found
        }
    }

    public void printPlayerCards() {
        for (int i = 0; i < hand.size(); i++) {
            if (i > 0 && i % 4 == 0) {
                System.out.println();
            }
            System.out.print(hand.get(i) + ", ");
        }
        System.out.println("\n");
    }

    public int getPlayerNumber() {
        return playerNumber;
    }
}