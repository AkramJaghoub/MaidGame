package Game;

import Player.PlayerQueue;

public class GameDriver {
    public static void main(String[] args) {
        PlayerQueue playerQueue = PlayerQueue.getInstance();
        playerQueue.initializeQueue();
        Game game = new MaidGame();
        game.startWithPlayer(1);
        game.play();
    }
}