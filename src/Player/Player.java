package Player;

import Cards.Card;
import Game.MaidGame;

import java.util.ArrayList;
import java.util.Random;
import Game.*;

public class Player implements Runnable {
    private final int playerNumber;
    private final ArrayList<Card> hand;
    private final Object lock;
    private boolean isGameFinished = false;
    private final GameStatus gameStatus = GameStatus.getInstance();

    PlayerQueue playerQueue;


    public Player(int playerNumber, Object lock) {
        this.hand = new ArrayList<>();
        this.playerNumber = playerNumber;
        playerQueue = PlayerQueue.getInstance();
        this.lock = lock;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public void setGameFinisher(boolean finish){
        this.isGameFinished = finish;
    }


    public synchronized Card takeRandomCard() throws InterruptedException {
        Random random = new Random();
        return hand.remove(random.nextInt(hand.size()));
    }

    public synchronized void addCardsToHand(ArrayList<Card> cards) {
        hand.addAll(cards);
    }

    public synchronized ArrayList<Card> getCardsInHand() {
        return hand;
    }


    public void run() {
        synchronized (lock) {
            while (playerQueue.size() > 1) {
                while (!isGameFinished && !playerQueue.isNextPlayer(this)) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (!hand.isEmpty()) {
                    Player currentPlayer = playerQueue.getCurrentPlayer();
                    gameStatus.playerTurn(currentPlayer);
                    lock.notifyAll();
                }
                if (gameStatus.checkGameOver()) {
                    System.exit(0);
                }
            }
        }
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
                    break;
                }
            }
            if (!foundMatch)
                i++;
        }
    }
}