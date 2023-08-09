package Game;

import Cards.Card;
import Player.Player;
import Player.PlayerQueue;
import Utility.GameUtil;
import java.util.List;

public class GameController {

    private static GameController instance;
    boolean gameFinished;
    int counter;
    PlayerQueue playerQueue;
    private final Object playerQueueLock;
    private final Object gameStatusLock;

    private GameController() {
        playerQueue = PlayerQueue.getInstance();
        playerQueueLock = new Object();
        gameStatusLock = new Object();
    }

    public static GameController getInstance() {
        if (instance == null) {
            instance = new GameController();
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
                        System.out.println("Player " + nextPlayer.getPlayerNumber() + " has discarded all their cards and is removed from the game!");
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
                if(currentPlayer.getCardsInHand().isEmpty()){
                    Player player = playerQueue.removePlayerByNumber(currentPlayer.getPlayerNumber());
                    System.out.println("Player " + player.getPlayerNumber() + " has discarded all their cards and is removed from the game!");
                    playerQueue.getNextPlayer();
                }
            }
        }
    }

    public synchronized void playRound() {
        synchronized (playerQueueLock) {
            System.out.println("***************************** ROUND " + ++counter + " SUMMARY *****************************");
            int playerSize = playerQueue.size();
            for (int i = 0; i < playerSize; i++) {
                Player player = playerQueue.removeCurrentPlayer();
                if (player.getCardsInHand().isEmpty()) {
                    continue; //skip empty player card
                }
                playerQueue.getQueue().add(player); //only add to the queue when a player can play again
                if (checkGameOver()) {
                    return;
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
                if (lastPlayer.getCardsInHand().size() == 1 && lastPlayer.getCardsInHand().get(0).isJoker()) { // if 1 card left and it is the JOKER
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