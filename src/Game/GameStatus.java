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

    private GameStatus() {
        playerQueue = PlayerQueue.getInstance();
    }

    public static GameStatus getInstance() {
        if (instance == null) {
            instance = new GameStatus();
        }
        return instance;
    }

    public synchronized void playerTurn(Player currentPlayer) {
        List<Card> playerHand = currentPlayer.getCardsInHand();
        GameUtil.printHands();
        playRound();
        System.out.println("Player " + currentPlayer.getPlayerNumber() + " taking turn...");
        try {
            if (playerHand.isEmpty() ||
                    (playerHand.size() == 1 && playerHand.get(0).isJoker())) {
                return;
            }
            Player nextPlayer = playerQueue.getNextPlayer(); // Getting the next player
            Card takenCard = nextPlayer.takeRandomCard(); // Taking a card from the next player
            System.out.println("Player " + currentPlayer.getPlayerNumber() + " took " + takenCard + " from Player " + nextPlayer.getPlayerNumber());
            boolean matched = false;
            for (int i = 0; i < playerHand.size(); i++) {
                if (takenCard.matches(playerHand.get(i))) {
                    System.out.println("Player " + currentPlayer.getPlayerNumber() + " matched " + takenCard + " with " + playerHand.get(i) + " and discarded both");
                    playerHand.remove(i);
                    matched = true;
                    break; // Exiting the loop as match found
                }
            }
            if (!matched) {
                playerHand.add(takenCard); // Adding to hand if no match found
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Player " + currentPlayer.getPlayerNumber() + " turn ended.");
    }

    public boolean isStillInGame(Player player) {
        return playerQueue.getQueue().contains(player);
    }


    public synchronized void playRound() {
        if (gameFinished) return;
        System.out.println("***************************** ROUND " + ++counter + " SUMMARY *****************************");
        int playerSize = playerQueue.size();
        for (int i = 0; i < playerSize; i++) {
            Player player = playerQueue.removeCurrentPlayer();
            if (player.getCardsInHand().isEmpty()) {
                System.out.println("Player " + player.getPlayerNumber() + " has discarded all their cards and is removed from the game!");
                continue; // Skip players without cards
            }
            playerQueue.getQueue().add(player); // Add player back only if they still have cards
            if (checkGameOver()) {
                break; // Exit the loop if the game is over
            }
        }
    }

    public synchronized boolean checkGameOver() {
        System.out.println("Checking if game is over...");
            if (gameFinished) {
                System.out.println("Game already marked as finished.");
                return false;
            }
            if (playerQueue.size() == 1) {
                Player lastPlayer = playerQueue.getCurrentPlayer();
                if (lastPlayer.getCardsInHand().size() == 1 && lastPlayer.getCardsInHand().get(0).isJoker()) {
                    System.out.println("\n***************************** FINAL STATUS *****************************");
                    System.out.println("Player " + lastPlayer.getPlayerNumber() + " has lost! They got left with a Joker! Game.Game finished! :)");
                    System.out.println("------------------------------------------------------------------");
                    endGame(); // Call the endGame method here
                    System.out.println("Check game over ended.");
                    return true;
                }
            }
            System.out.println("Check game over ended.");
            return false;
    }


    private synchronized void endGame() {
        gameFinished = true;
        for (Player player : playerQueue.getQueue()) {
            player.setGameFinisher(true);
        }
        playerQueue.notifyAllPlayers(); // Notify all waiting players
        System.out.println("Game.Game marked as finished.");
    }
}
