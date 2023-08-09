package Game;

import Cards.Card;
import Player.Player;
import Player.PlayerQueue;
import Util.GameUtil;

import java.util.List;

public class GameStatus {

    private static GameStatus instance;
    boolean gameFinished;
    int counter;
    PlayerQueue playerQueue;
    private final Object playerQueueLock;
    private final Object gameStatusLock;

    private GameStatus() {
        playerQueue = PlayerQueue.getInstance();
        playerQueueLock = new Object();
        gameStatusLock = new Object();
    }

    public static GameStatus getInstance() {
        if (instance == null) {
            instance = new GameStatus();
        }
        return instance;
    }

    public synchronized void playerTurn(Player currentPlayer) {
        synchronized (gameStatusLock) {
            List<Card> playerHand = currentPlayer.getCardsInHand();
            GameUtil.printHands();
            playRound();
            if (!playerHand.isEmpty()) {
                Card takenCard = null;
                Player nextPlayer;
                try {
                    nextPlayer = playerQueue.getNextPlayer();
                    if (nextPlayer == currentPlayer) {
                        return;
                    }
                    takenCard = nextPlayer.takeRandomCard();
                    System.out.println("Player " + currentPlayer.getPlayerNumber() + " took " + takenCard + " from Player " + nextPlayer.getPlayerNumber());
                    if (nextPlayer.getCardsInHand().isEmpty()) {
                        playerQueue.removeCurrentPlayer();
                         playerQueue.getNextPlayer();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (takenCard != null) {
                    boolean matched = false;
                    for (int i = 0; i < playerHand.size(); i++) {
                        Card playerCard = playerHand.get(i);
                        if (playerCard != null && takenCard.matches(playerCard)) {
                            System.out.println("Player " + currentPlayer.getPlayerNumber() + " matched " + takenCard + " with " + playerCard + " and discarded both");
                            playerHand.remove(i);
                            matched = true;
                            break;
                        }
                    }

                    if (checkGameOver()) {
                        gameFinished = true;
                        return;
                    }

                    if (!matched) {
                        playerHand.add(takenCard);
                    }
                }
            }
        }
    }

    public synchronized void playRound() {
        synchronized (playerQueueLock) {
            if (gameFinished) return;
            System.out.println("***************************** ROUND " + ++counter + " SUMMARY *****************************");
            int playerSize = playerQueue.size();
            for (int i = 0; i < playerSize; i++) {
                Player player = playerQueue.removeCurrentPlayer();
                if (player.getCardsInHand().isEmpty()) {
                    System.out.println("Player " + player.getPlayerNumber() + " has discarded all their cards and is removed from the game!");
                    continue;
                }
                playerQueue.getQueue().add(player);
                if (checkGameOver()) {
                    break;
                }
            }
        }
    }

    public synchronized boolean checkGameOver() {
        synchronized (gameStatusLock) {
            if (gameFinished) {
                return true;
            }
            if (playerQueue.size() == 1) {
                Player lastPlayer = playerQueue.getCurrentPlayer();
                if (lastPlayer.getCardsInHand().size() == 1 && lastPlayer.getCardsInHand().get(0).isJoker()) {
                    System.out.println("Player " + lastPlayer.getPlayerNumber() + " has lost! They got left with a Joker! Game finished!");
                    endGame();
                    return true;
                }
            }
            return false;
        }
    }

    private synchronized void endGame() {
        synchronized (playerQueueLock) {
            gameFinished = true;
            for (Player player : playerQueue.getQueue()) {
                player.setGameFinisher(true);
            }
            playerQueue.notifyAllPlayers();
            System.out.println("Game marked as finished.");
        }
    }
}