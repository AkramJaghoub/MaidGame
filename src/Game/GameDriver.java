package Game;

import Player.PlayerQueue;

public class GameDriver {
    public static void main(String[] args) {
        // Add players to the player queue
        PlayerQueue playerQueue = PlayerQueue.getInstance();
        playerQueue.initializeQueue(); // You can create a method to initialize players
        Game game = new MaidGame();
        game.startWithPlayer(1);
        Thread gameThread = new Thread(game);
        gameThread.start();
        // Wait for the game thread to finish
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}