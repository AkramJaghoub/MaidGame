package Game;

import Cards.Deck;
import Player.Player;
import Player.PlayerQueue;
import Utility.GameUtil;

public abstract class Game {
    protected PlayerQueue playerQueue;
    protected Deck deck;


    protected void initializeGame(){
        GameUtil.dealCardsToPlayers();
        startWithPlayer();
    }

    protected void startWithPlayer() {
        Player startingPlayer = playerQueue.getPlayerByNumber(1);
        playerQueue.setCurrentPlayer(startingPlayer);
    }

    public abstract void play();
}