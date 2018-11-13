package game;


import java.util.*;

public class Game {

    private static int[] prices;
    private static Deck[] decks;
    static Stock stock;
    private static int [][] shares;
    private static int [] playerCash;
    private static int [] deckVotes;
    private static int [] noTradesPerPlayerPerRound;
    private static int [] noVotesPerPlayerPerRound;
    private static boolean [][] playerVotesPerStockPerRound;
    public static int roundNumber;
    private static int playerNo;
    private static boolean [] playersSkippedRound;
    private static boolean gameOver;

    // create a random game
    public Game()
    {
        playerNo = 0;

        //initialize stock prices too 100 for each stock
        prices = new int[]{100, 100, 100, 100, 100};

        //generate the decks for each stock
        decks = new Deck[Stock.values().length];
        for (Stock s : Stock.values())
        {
            decks[s.ordinal()] = new Deck(s);
        }

        //initialize the deckVotes array
        deckVotes = new int[5];
        for(int vote : deckVotes)
        {
            vote = 0;
        }
    }

    public static String addPlayer()
    {
        if(playerNo < 4) {
            playerNo++;

            noTradesPerPlayerPerRound = new int[playerNo];
            noVotesPerPlayerPerRound = new int [playerNo];
            playerVotesPerStockPerRound = new boolean[playerNo][5];
            playerCash = new int[playerNo];
            playersSkippedRound = new boolean[playerNo];
            shares = new int[playerNo][5];
            return "Player " + playerNo + "successfully added to the game.";
        }
        else
        {
            return "Maximum number of players reached.";
        }
    }

    public static boolean roundCanEnd()
    {
        boolean roundCanEnd = true;

        for(int i = 0; i < playersSkippedRound.length; i++)
        {
            if(!playersSkippedRound[i])
            {
                roundCanEnd = false;
                break;
            }
        }

        if(roundCanEnd)
        {
            return true;
        }
        else
        {
            roundCanEnd = true;
            for (int i = 0; i < noTradesPerPlayerPerRound.length; i++)
            {
                if (noTradesPerPlayerPerRound[i] != 2 || noVotesPerPlayerPerRound[i] != 2)
                {
                    roundCanEnd = false;
                }
            }
            return roundCanEnd;
        }
    }

    public String advanceRound()
    {
        roundNumber++;
        executeVotes();
        resetRoundArrays();
        return "Advanced to round " + roundNumber + "\r\n\r\n" + gameInformation();
    }

    public static String finalizeGame()
    {
        gameOver = true;
        return computeAndDisplayScores();
    }

    private void resetRoundArrays()
    {
        noTradesPerPlayerPerRound = new int[playerNo];
        noVotesPerPlayerPerRound = new int[playerNo];
        for(int i = 0 ; i <  noTradesPerPlayerPerRound.length; i++)
        {
            noTradesPerPlayerPerRound[i] = 0;
            noVotesPerPlayerPerRound[i] = 0;
        }

        playerVotesPerStockPerRound = new boolean[playerNo][5];
        for(int i = 0 ; i <  playerVotesPerStockPerRound.length; i++)
        {
            for(int j = 0; j < playerVotesPerStockPerRound[i].length; j++)
            {
                playerVotesPerStockPerRound[i][j] = false;
            }
        }

        for(int i = 0; i < playersSkippedRound.length; i++)
        {
            playersSkippedRound[i] = false;
        }
    }

    //called after all players have connected
    public static String setupGame()
    {
        roundNumber = 1;
        gameOver = false;
        for(int i = 0; i< noTradesPerPlayerPerRound.length; i++)
        {
            noTradesPerPlayerPerRound[i] = 0;
            noVotesPerPlayerPerRound[i] = 0;

        }


        for(int i = 0; i < playerVotesPerStockPerRound.length; i++)
        {
            for(int j = 0; j < playerVotesPerStockPerRound[i].length; j++)
            {
                playerVotesPerStockPerRound[i][j] = false;
            }
        }

        for(int i = 0; i < playerCash.length; i++)
        {
            playerCash[i] = 500;
            playersSkippedRound[i] = false;
        }

        Random random = new Random();
        for(int i = 0; i < playerNo; i++)
        {
            int currentStockSum = 0;
            for (int j = 0; j < 5; j++)
            {
                shares[i][j] = random.nextInt(10);
                currentStockSum += shares[i][j];
            }

            double scale = 1d * 10 / currentStockSum;
            currentStockSum = 0;
            for (int j = 0; j < 5; j++)
            {
                shares[i][j] = (int) (shares[i][j] * scale);
                currentStockSum += shares[i][j];
            }

            //take rounding issues into account
            while(currentStockSum++ < 10)
            {
                int j = random.nextInt(5);
                shares[i][j] = shares[i][j] + 1;
            }
        }
        return "Round " + roundNumber + " started\r\n\r\n" + gameInformation();
    }

    private static String getStockNameById(int stockId)
    {
        Stock [] stockValues = Stock.values();
        if(stockId < 5 && stockId >= 0)
        {
            return stockValues[stockId].name();
        }
        else
        {
            return "Invalid stock id.";
        }
    }

    public static int getCash(int playerId)
    {
        return playerCash[playerId];
    }

    public static String getCashString(int playerId)
    {
        int playerCash = getCash(playerId);
        int playerNo = playerId + 1;
        return "Current cash for player " + playerNo + " : " + playerCash;
    }

    public static int[] getShares(int playerId)
    {
        return shares[playerId];
    }

    public static String getSharesString(int playerId)
    {
        int[] shares = getShares(playerId);
        String sharesString = "Shares for player " + playerId + " : \r\n";
        for (int i = 0; i < shares.length; i++)
        {
            sharesString = sharesString + getStockNameById(i) + " " + shares[i] + "\r\n";
        }

        return sharesString;
    }

    public static int[] getPrices()
    {
        return prices;
    }

    public static String getPricesString()
    {
        int[] prices = getPrices();
        String pricesString = "";
        for (int i = 0; i < prices.length; i++)
        {
            pricesString = pricesString + getStockNameById(i) + " : " + prices[i] + "\r\n";
        }

        return pricesString;
    }

    public static Card[] getCards() {
        Card[] currentCards = new Card[5];
        int i = 0;
        for(Deck deck : decks)
        {
            currentCards[i] = deck.cards.get(0);
            i++;
        }

        return currentCards;
    }

    public static String getCardsString()
    {
        Card[] currentCards = getCards();

        String cardString = "";
        for (int i = 0; i < currentCards.length; i++)
        {
            cardString = cardString + getStockNameById(i) + " " + currentCards[i] + "\r\n";
        }

        return cardString;
    }

    public String executeVotes() {
        Card[] currentCards = getCards();
        HashMap<Integer, Card> executedCards = new HashMap<>();
        HashMap<Integer, Card> removedCards = new HashMap<>();
        for(int i = 0; i< deckVotes.length; i++)
        {
            if(deckVotes[i] > 0)
            {
                int cardEffect = currentCards[i].effect;
                prices[i]  = prices[i] + cardEffect;

                Deck currentDeck = decks[i];
                executedCards.put(i, currentDeck.cards.get(0));
                currentDeck.cards.remove(0);
            }
            else if(deckVotes[i] < 0)
            {
                Deck currentDeck = decks[i];
                removedCards.put(i, currentDeck.cards.get(0));
                currentDeck.cards.remove(0);
            }
        }

        String removedAndExecutedCardsMessage = "The following effects have been applied:\r\n";
        for(Map.Entry<Integer, Card> executedCardEntry: executedCards.entrySet())
        {
            int stockOrder = executedCardEntry.getKey();
            Card effectCard = executedCardEntry.getValue();
            removedAndExecutedCardsMessage = removedAndExecutedCardsMessage + effectCard.effect + " for " + getStockNameById(stockOrder) + "\r\n";
        }
        removedAndExecutedCardsMessage = removedAndExecutedCardsMessage + "The following effects have been removed from play:\r\n";
        for(Map.Entry<Integer, Card> removedCardEntry: removedCards.entrySet())
        {
            int stockOrder = removedCardEntry.getKey();
            Card effectCard = removedCardEntry.getValue();
            removedAndExecutedCardsMessage = removedAndExecutedCardsMessage + effectCard.effect + " for " + getStockNameById(stockOrder) + "\r\n";
        }

        return removedAndExecutedCardsMessage;
    }

    public String buy(int playerId, Stock s, int amount)
    {
        if(!gameOver)
        {
            if(!playersSkippedRound[playerId])
            {
                if (noTradesPerPlayerPerRound[playerId] < 2)
                {
                    //calculate the amount to pay, including the 3£ transaction fee per share
                    int amountToPay = amount * prices[s.ordinal()] + 3 * amount;
                    if (playerCash[playerId] >= amountToPay)
                    {
                        playerCash[playerId] -= amountToPay;
                        shares[playerId][s.ordinal()] += amount;

                        noTradesPerPlayerPerRound[playerId]++;

                        return "Transaction successful. You have bought " + amount + " " + s.name() + " shares.";
                    }
                    else
                    {
                        return "Insufficient funds";
                    }
                }
                else
                {
                    return "Maximum number of trades reached per round";
                }
            }
            else
            {
                return "You have skipped this round and are not allowed to perform further actions.";
            }
        }
        else
        {
            return "Game has ended.";
        }
    }

    public String sell(int playerId, Stock s, int amount)
    {
        if(!gameOver)
        {
            if(!playersSkippedRound[playerId])
            {
                if(noTradesPerPlayerPerRound[playerId] < 2)
                {
                    int amountToReceive = amount * prices[s.ordinal()];
                    if (amount <= shares[playerId][s.ordinal()])
                    {
                        playerCash[playerId] += amountToReceive;
                        shares[playerId][s.ordinal()] -= amount;

                        noTradesPerPlayerPerRound[playerId]++;

                        return "Transaction successful. You have sold " + amount + " " + s.name() + " shares.";
                    }
                    else
                    {
                        return "Insufficient stocks, please select a lower amount.";
                    }
                }
                else
                {
                    return "Maximum number of trades reached per round";
                }
            }
            else
            {
                return "You have skipped this round and are not allowed to perform further actions.";
            }
        }
        else
        {
            return "Game has ended.";
        }
    }

    public String vote(int playerId, Stock s, boolean yes)
    {
        if(!gameOver)
        {
            if(!playersSkippedRound[playerId])
            {
                if(noVotesPerPlayerPerRound[playerId] < 2)
                {
                    if(!playerVotesPerStockPerRound[playerId][s.ordinal()])
                    {
                        if (yes)
                        {
                            deckVotes[s.ordinal()]++;
                        }
                        else
                        {
                            deckVotes[s.ordinal()]--;
                        }

                        noVotesPerPlayerPerRound[playerId]++;
                        playerVotesPerStockPerRound[playerId][s.ordinal()] = true;

                        return "You have successfully voted " + yes + " for the "+ decks[s.ordinal()].cards.get(0) +" effect on stock " + s.name();
                    }
                    else
                    {
                        return "You can only vote once for a card per round";
                    }
                }
                else
                {
                    return "Maximum number of votes per round reached";
                }
            }
            else
            {
                return "You have skipped this round and are not allowed to perform further actions.";
            }
        }
        else
        {
            return "Game has ended.";
        }
    }

    public String endRound(int playerId)
    {
        if(!gameOver)
        {
            int playerDisplayName = playerId + 1;
            if(!playersSkippedRound[playerId])
            {
                playersSkippedRound[playerId] = true;

                return "You have ended this round.";
            }
            return "You have already ended this round.";
        }
        else
        {
            return "Game has ended.";
        }
    }

    public String toString() {
        String gameString = "";
        gameString = gameString + "Prices:\r\n";
        gameString = gameString + Arrays.toString(getPrices()) + "\r\n";
        gameString = gameString + "Player Cash:\r\n";
        gameString = gameString + Arrays.toString(playerCash) + "\r\n";
        gameString = gameString + "Shares:\r\n";
        for (int i = 0; i < shares.length; i++)
        {
            for (int j = 0; j < shares[i].length; j++)
            {
                gameString = gameString + shares[i][j] + " ";
            }
            gameString = gameString + "\r\n";
        }
        gameString = gameString + "Decks:\r\n";
        for(Deck d : decks)
        {
            gameString = gameString + d.toString() + "\r\n";
        }

        return gameString;
    }

    private static String gameInformation()
    {
        String gameString = "";
        gameString = gameString + "Prices\r\n";
        gameString = gameString + getPricesString();
        gameString = gameString + "\r\nPlayer Cash\r\n";
        for(int i = 0; i < playerCash.length; i++)
        {
            int playerNo = i + 1;
            gameString = gameString + "Player " + playerNo + " : " + playerCash[i] + "\r\n";
        }
        gameString = gameString + "\r\nShares\r\n";

        for (int i = 0; i < shares.length; i++) {
            int playerNo = i+1;
            gameString = gameString + "Player " + playerNo + ": ";
            for (int j = 0; j < shares[i].length; j++) {
                gameString = gameString + getStockNameById(j) + " " + shares[i][j] + " ";
            }
            gameString = gameString + "\r\n";
        }

        gameString = gameString + "\r\nCards\r\n";
        gameString = gameString + getCardsString();

        return gameString;
    }

    private static String computeAndDisplayScores()
    {
        int[] playerScore = new int[playerNo];

        System.arraycopy(playerCash, 0, playerScore, 0, playerCash.length);

        for(int i = 0; i < shares.length; i++)
        {
            for(int j = 0; j < shares[i].length; j++)
            {
                playerScore[i] += shares[i][j] * prices[i];
            }
        }

        String playerScores = "Game has ended. Players have earned the following scores:\r\n";

        int winnerNumber = 1;
        int winnerScore = playerScore[0];
        for(int i = 0; i < playerScore.length; i++)
        {
            int playerNumber = i+1;
            playerScores = playerScores + "Player " + playerNumber + " score: " +  playerScore[i] + "\r\n";
            if(playerScore[i] > winnerScore)
            {
                winnerScore = playerScore[i];
                winnerNumber = playerNumber;
            }
        }

        playerScores = playerScores + "\r\nThe winner is player "+ winnerNumber + " with "+ winnerScore;

        return playerScores;
    }

    public static void main(String[] args) {
    }

}
