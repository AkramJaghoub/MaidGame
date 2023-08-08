
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MaidGame extends Game {
    private static final String JOKER = "Joker";
    private boolean gameFinished = false;
    private final Object lock = new Object();
    private int counter;


    public MaidGame() {
        playerQueue = PlayerQueue.getInstance();
        deck = Deck.getInstance();
        System.out.println(playerQueue.size());
        dealCardsToPlayers();
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
        threadPool.shutdown(); // Move this line here
        System.out.println("Game ended.");
    }


    @Override
    public void play() {
        System.out.println("Beginning play...");
        while (!gameFinished) {
            printHands();
            playRound(); // Play the round
            synchronized (lock) {
                playerQueue.getNextPlayer(); // Move to the next player
                lock.notifyAll(); // Notify all waiting threads
            }
            checkGameOver(); // Check game over condition
        }
        System.out.println("Play ended.");
    }



    private void dealCardsToPlayers() {
        System.out.println("\nDiscarding cards before starting game...\n");
        int numberOfPlayers = playerQueue.size();
        Queue<Player> queue = playerQueue.getQueue();
        int cardsPerPlayer = deck.getCards().size() / numberOfPlayers;
        int extraCards = deck.getCards().size() % numberOfPlayers;
        for (int i = 0; i < numberOfPlayers; i++) {
            int numPlayerCards = cardsPerPlayer + (i < extraCards ? 1 : 0);
            Player player = queue.remove();
            player.addCardsToHand(deck.dealCard(numPlayerCards));
            player.discardMatchingPairs(); // Discard matching pairs after dealing cards
            queue.add(player);
        }
    }


    public void printHands() {
        System.out.println("***************************** Current Hands *****************************\n");
        for (Player player : playerQueue.getQueue()) {
            if (!player.getCardsInHand().isEmpty()) {
                System.out.println("Player " + (player.getPlayerNumber() + 1) + "'s Cards:");
                player.printPlayerCards();
            }
        }
    }

    public void playRound() {
        if (gameFinished) return; // Add this line to exit if the game is over
        System.out.println("***************************** ROUND " + ++counter + " SUMMARY *****************************");
        int playerSize = playerQueue.size();
        for (int i = 0; i < playerSize; i++) {
            Player player = playerQueue.getQueue().remove();
            if (player.getCardsInHand().isEmpty()) {
                System.out.println("Player " + (player.getPlayerNumber() + 1) + " has discarded all their cards and is removed from the game!");
                continue; // Skip players without cards
            }
            if (playTurn(player)) {
                System.out.println("Player " + (player.getPlayerNumber() + 1) + " has discarded all their cards and is removed from the game!");
            } else {
                playerQueue.getQueue().add(player); // Add player back only if they still have cards
            }
            if (checkGameOver()) {
                break; // Exit the loop if the game is over
            }
        }
    }

    public boolean playTurn(Player currentPlayer) {
        Player nextPlayer = playerQueue.getQueue().peek();
        if (nextPlayer != null) {
            currentPlayer.takeTurn(nextPlayer);
            return currentPlayer.getCardsInHand().isEmpty();
        }
        return false;
    }

    private synchronized void endGame() {
        gameFinished = true;
        for (Player player : playerQueue.getQueue()) {
            player.setGameFinisher(true);
        }
        // Use the same lock object for notifyAll() as the synchronized block
        synchronized (lock) {
            lock.notifyAll();
        }
        System.out.println("Game marked as finished.");
    }

    private boolean checkGameOver() {
        System.out.println("Checking if game is over...");
        synchronized (lock) {
            if (gameFinished) {
                System.out.println("Game already marked as finished.");
                return false;
            }
            if (playerQueue.size() == 1) {
                Player lastPlayer = playerQueue.getCurrentPlayer();
                if (lastPlayer.getCardsInHand().size() == 1 && lastPlayer.getCardsInHand().get(0).getValue().equals(JOKER)) {
                    System.out.println("\n***************************** FINAL STATUS *****************************");
                    System.out.println("Player " + (lastPlayer.getPlayerNumber() + 1) + " has lost! They got left with a Joker! Game finished! :)");
                    System.out.println("------------------------------------------------------------------");
                    endGame(); // Call the endGame method here
                    System.out.println("Check game over ended.");
                    return true;
                }
            }
            System.out.println("Check game over ended.");
            return false;
        }
    }
}