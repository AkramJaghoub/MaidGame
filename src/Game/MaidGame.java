package Game;

import Cards.Deck;
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
    public void play() {
        System.out.println("Beginning play...");
        for (Player player : playerQueue.getQueue()) {
            Thread thread = new Thread(player);
            thread.start();
        }
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
