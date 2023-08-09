package Game;

import Cards.Deck;
import Player.Player;
import Player.PlayerQueue;

public abstract class Game {
    protected PlayerQueue playerQueue;
    protected Deck deck;

    public void startWithPlayer(int playerNumber) {
        Player startingPlayer = playerQueue.getPlayerByNumber(playerNumber);
        playerQueue.setCurrentPlayer(startingPlayer);
    }

    public abstract void play();
}