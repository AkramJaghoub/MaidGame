package Player;

import Exceptions.InvalidInputException;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class PlayerQueue {
    private static PlayerQueue instance;
    private final Queue<Player> players;
    private Player currentPlayer; // Variable to track the current player
    private final Object lock; // Synchronization lock for player operations

    private PlayerQueue(){
        players = new LinkedList<>();
        lock = new Object(); // Initialize the lock
    }

    public static PlayerQueue getInstance(){
        if(instance == null)
            instance = new PlayerQueue();
        return instance;
    }

    public void initializeQueue(){
        System.out.println("Welcome to the MAID GAME!");
        try {
            Scanner input = new Scanner(System.in);
            System.out.println("Enter the number of players: ");
            int numberOfPlayers = input.nextInt();
            if(numberOfPlayers < 2){
                throw new InvalidInputException("You need at least 2 players to play. Try again.");
            }
            if(numberOfPlayers > 8){
                throw new InvalidInputException("Maximum number of players is 8. Try again.");
            }
            for (int i = 1; i <= numberOfPlayers; i++) {
                Player player = new Player(i, lock); // Pass the lock to each player
                players.add(player);
            }
        } catch(InvalidInputException e){
            System.out.println(e.getMessage());
            initializeQueue();
        }
    }

    public boolean isNextPlayer(Player player) {
        return player.equals(currentPlayer);
    }


    public void notifyAllPlayers() {
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    public Queue<Player> getQueue(){
        return  players;
    }

    public synchronized Player removeCurrentPlayer() {
        Player removedPlayer = null;
        if (!players.isEmpty()) {
            removedPlayer = players.remove();
        }
        return removedPlayer;
    }

    public synchronized void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }

    public synchronized Player getPlayerByNumber(int playerNumber) {
        for (Player player : players) {
            if (player.getPlayerNumber() == playerNumber) {
                return player;
            }
        }
        return null; // Return null if no matching player is found
    }

    public synchronized Player getCurrentPlayer() {
        return this.currentPlayer;
    }


    public synchronized Player getNextPlayer() {
        if (players.isEmpty()) {
            return null; // Return null if the players queue is empty
        }
        Player nextPlayer = players.remove();
        players.add(nextPlayer);
        setCurrentPlayer(nextPlayer); // Update the current player
        return nextPlayer;
    }

    public int size() {
        return  players.size();
    }
}