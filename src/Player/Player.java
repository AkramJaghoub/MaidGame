package Player;

import Cards.Card;
import Game.MaidGame;

import java.util.ArrayList;
import java.util.Random;
import Game.*;

public class Player implements Runnable {
    private final int playerNumber;
    private final ArrayList<Card> hand;
    private final Object lock; // Synchronization lock for player operations
    private boolean isGameFinished = false;
    private final GameStatus gameStatus = GameStatus.getInstance();

    PlayerQueue playerQueue;

    private MaidGame gameInstance;

    public void setGameInstance(MaidGame gameInstance) {
        this.gameInstance = gameInstance;
    }

    public MaidGame getGameInstance() {
        return gameInstance;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public Player(int playerNumber, Object lock) {
        this.hand = new ArrayList<>();
        this.playerNumber = playerNumber;
        playerQueue = PlayerQueue.getInstance();
        this.lock = lock;
    }

    public synchronized Card takeRandomCard() throws InterruptedException {
        Random random = new Random();
        return hand.remove(random.nextInt(hand.size()));
    }

    public synchronized void addCardsToHand(ArrayList<Card> cards) {
        hand.addAll(cards);
    }

    public void setGameFinisher(boolean finish){
        this.isGameFinished = finish;
    }

    @Override
    public void run() {
        synchronized (lock) {
            System.out.println("Player " + (playerNumber + 1) + " starting...");
            while (playerQueue.size() > 1) {
                while (playerQueue.getCurrentPlayer() != this && !isGameFinished) {
                    System.out.println("Player " + playerNumber + " waiting...");
                    try {
                        lock.wait(); // Wait if it's not this player's turn
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                if (isGameFinished) break; // Break out of loop if game is finished
                if (!hand.isEmpty()) {
                    Player currentPlayer = playerQueue.getCurrentPlayer();
                    gameStatus.playerTurn(currentPlayer); // Take turn
                    playerQueue.getNextPlayer(); // Move to next player; method to be implemented in PlayerQueue
                    lock.notifyAll(); // Notify other waiting players
                }
                if (gameStatus.checkGameOver()) {
                    break; // Exit the loop if the game is finished
                }
            }
            lock.notifyAll(); // Notify other players at the end
            System.out.println("Player " + playerNumber + " ended...");
        }
    }


    public synchronized ArrayList<Card> getCardsInHand() {
        return hand;
    }



    public synchronized void discardMatchingPairs() {
        int i = 0;
        while (i < hand.size()) {
            Card card1 = hand.get(i);
            boolean foundMatch = false;
            for (int j = i + 1; j < hand.size(); j++) {
                Card card2 = hand.get(j);
                if (card1.matches(card2)) {
                    System.out.println("Player " + playerNumber + " matched " + card1 + " with " + card2 + " and discarded both");
                    hand.remove(j);
                    hand.remove(i);
                    foundMatch = true;
                    break; // Exit inner loop, since we found a match for this card
                }
            }
            if (!foundMatch) i++; // Increment i only if no match found
        }
    }

}