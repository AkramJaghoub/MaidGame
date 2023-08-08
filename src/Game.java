import java.util.ArrayList;
import java.util.Queue;

public abstract class Game implements Runnable {
    protected PlayerQueue playerQueue;
    protected Deck deck;

    public abstract void play();
}