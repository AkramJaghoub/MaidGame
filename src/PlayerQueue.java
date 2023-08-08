import Exceptions.InvalidInputException;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class PlayerQueue {
    private static PlayerQueue instance;
    private final Queue<Player> players;
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
            for (int i = 0; i < numberOfPlayers; i++) {
                Player player = new Player(i, lock); // Pass the lock to each player
                players.add(player);
            }
        } catch(InvalidInputException e){
            System.out.println(e.getMessage());
            initializeQueue();
        }
    }

    public Queue<Player> getQueue(){
        return  players;
    }

    public synchronized Player getCurrentPlayer() {
        return players.peek();
    }

    public synchronized Player getNextPlayer() {
        Player currentPlayer =  players.remove();
        players.add(currentPlayer);
        return players.peek();
    }

    public synchronized void removeCurrentPlayer() {
        players.remove();
    }

    public int size() {
        return  players.size();
    }
}