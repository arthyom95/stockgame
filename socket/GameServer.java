package socket;

/**
 * Created by viones on 22/11/2017.
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import game.Game;

public class GameServer {

    private static final int PORT = 8888;
    private static final Game game = new Game();
    private static List<GameService> gameServices = new ArrayList<>();

    @SuppressWarnings("resource")
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        System.out.println("Started Stock Game server at port " + PORT);
        System.out.println("Waiting for clients to connect...");

        Socket socket = server.accept();
        GameService service = new GameService(game, socket);
        gameServices.add(service);
        Thread t = new Thread(service);
        t.start();
        while(service.getMaxPlayers() == 0)
        {
            try {
                t.join(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int players = service.getMaxPlayers();
        System.out.println("Primary client connected.");

        for (int i = 1; i < players; i++)
        {
            socket = server.accept();
            System.out.println("Client connected.");
            service = new GameService(game, socket);
            new Thread(service).start();
            gameServices.add(service);
        }

        String gameSetupMessage = Game.setupGame();
        for (GameService gameService : gameServices)
        {
            gameService.notifyMaxPlayersConnected();
            gameService.sendCustomMessage(gameSetupMessage);
        }

        while(Game.roundNumber < 6)
        {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String endMessage = Game.finalizeGame();
        for (GameService gameService : gameServices) {
            gameService.sendCustomMessage(endMessage);
        }
    }
}
