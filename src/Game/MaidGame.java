package Game;

import Cards.Deck;
import Player.PlayerQueue;
import Player.Player;
import Utility.GameUtil;

public class MaidGame extends Game {
    private final Object lock = new Object();

    public MaidGame() {
        playerQueue = PlayerQueue.getInstance();
        deck = Deck.getInstance();
        initializeGame();
    }

    @Override
    public void play() {
        System.out.println("\nBeginning play...");
        for (Player player : playerQueue.getQueue()) {
            Thread thread = new Thread(player);
            thread.start();
        }
        while (playerQueue.size() > 1) {
            synchronized (lock) {
                try {
                    Thread.sleep(50);
                    lock.notifyAll();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        System.out.println("Play ended.");
    }
}
