package socket;

/**
 * Created by viones on 22/11/2017.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

import game.Game;
import game.Stock;

public class GameService implements Runnable  {

    private Scanner in;
    private PrintWriter out;
    private int playerId;
    private boolean login;
    private static boolean startGame = false;
    private Game game;
    private static int loginCount = 0;
    private static int maxPlayers = 0;
    private static boolean primary = true;
    private static List<GameService> gameServices = new ArrayList<GameService>();

    public GameService(Game game, Socket socket) {
        loginCount++;
        this.playerId = loginCount;
        this.game = game;
        Game.addPlayer();
        gameServices.add(this);
        try {
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void notifyGameStart()
    {
        out.println("All connected players have agreed to start the game.");
        out.println("Starting game...");
        out.println();
    }

    void notifyMaxPlayersConnected()
    {
        out.println("Maximum number of players connected.");
        out.println("Starting game...");
        out.println();
    }

    private void notifyPlayersConnected()
    {
        out.println("Player "+loginCount+" has connected.");
        out.println();
    }

    void sendCustomMessage(String message)
    {
        out.println(message);
    }


    @Override
    public void run() {
        try
        {
            loginAndWaitForPlayers();
        }
        catch(InterruptedException ignored)
        {
        }

        if(!startGame)
        {
            startGame = true;
            for (GameService serviceListItem : gameServices)
            {
                serviceListItem.notifyGameStart();
            }
        }
        while (startGame && Game.roundNumber < 6)
        {
            while(!Game.roundCanEnd())
            {
                try
                {
                    Request request = Request.parse(in.nextLine());
                    String response = execute(game, request);
                    // note use of \r\n for CRLF
                    out.println(response + "\r\n");
                }
                catch (NoSuchElementException e)
                {
                    login = false;
                }
            }
            String roundAdvanceMessage = game.advanceRound();
            if(Game.roundNumber < 6)
            {
                for (GameService gameService : gameServices) {
                    gameService.out.println(roundAdvanceMessage);
                }
            }
        }
    }

    public static int getMaxPlayers()
    {
        return maxPlayers;
    }

    private synchronized void loginAndWaitForPlayers() throws InterruptedException
    {
        gameServices.forEach(GameService::notifyPlayersConnected);

        if(primary)
        {
            out.println("Designated primary client.");
            out.println("Enter number of players (including yourself): ");
            maxPlayers = Integer.parseInt(in.nextLine());
            primary = false;
        }

        if(loginCount == maxPlayers)
        {
            startGame = true;
            return;
        }

        out.println("Welcome, player "+loginCount+". Wait for more players to connect.");
        while(loginCount != maxPlayers)
        {
            Thread.sleep(2000);
            if(loginCount == maxPlayers)
            {
                startGame = true;
                return;
            }
            Thread.sleep(2000);
        }
    }



    private synchronized String execute(Game game, Request request) {
        //since arrays start at 0, the position of playerId 1 will be 0, with the same being true for the rest of the
        // playerIds
        int playerIdArrayPos = playerId - 1;
        try {
            switch (request.type) {
                case SELL:
                    String stockChar = request.params[0];
                    Stock stockType = Stock.parse(stockChar);
                    int quantity = Integer.parseInt(request.params[1]);
                    return game.sell(playerIdArrayPos, stockType, quantity);
                case BUY:
                    stockChar = request.params[0];
                    stockType = Stock.parse(stockChar);
                    quantity = Integer.parseInt(request.params[1]);
                    return game.buy(playerIdArrayPos,stockType,quantity);
                case VOTE:
                    stockChar = request.params[0];
                    stockType = Stock.parse(stockChar);
                    String voteString = request.params[1];
                    Boolean voteType = Boolean.parseBoolean(voteString);
                    return game.vote(playerIdArrayPos, stockType, voteType);
                case ENDROUND:
                    return game.endRound(playerIdArrayPos);
                case GETCASH:
                    return game.getCashString(playerIdArrayPos);
                case GETSHARES:
                    return game.getSharesString(playerIdArrayPos);
                case GETPRICES:
                    return game.getPricesString();
                case GETCARDS:
                    return game.getCardsString();
                case INVALID:
                    return "Command invalid or failed!";
                case LOGOUT:
                    login = false;
                    return "Goodbye!";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
