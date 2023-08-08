package Game;

import Cards.Deck;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import Player.PlayerQueue;
import Player.Player;
import Util.GameUtil;

public class MaidGame extends Game {
    private final Object lock = new Object();

    public MaidGame() {
        playerQueue = PlayerQueue.getInstance();
        deck = Deck.getInstance();
        GameUtil.dealCardsToPlayers();
    }

    @Override
    public void run() {
        System.out.println("Starting the game...");
        ExecutorService threadPool = Executors.newFixedThreadPool(playerQueue.size());
        for (Player player : playerQueue.getQueue()) {
            player.setGameInstance(this);
            threadPool.execute(player);
        }
        play();
        threadPool.shutdown(); // Don't play the game here; the players will handle their turns
        System.out.println("Game.Game ended.");
    }

    @Override
    public void play() {
        startGame();
    }

    private void startGame() {
        System.out.println("Beginning play...");
        while (playerQueue.size() > 1) {
            synchronized (lock) {
                try {
                    Thread.sleep(50);
                    lock.notifyAll(); // Notify all waiting threads
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        System.out.println("Play ended.");
    }
}