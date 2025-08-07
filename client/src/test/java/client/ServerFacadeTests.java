package client;

import chess.ChessGame;
import dataaccess.DatabaseManager;
import dataaccess.exceptions.DataAccessException;
import model.GameData;
import org.junit.jupiter.api.*;
import server.ResponseException;
import server.Server;
import server.ServerFacade;
import server.clear.ClearService;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static String serverURL;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        serverURL = "http://localhost:" + port;
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterEach
    void tearDownReset() {
        try {
            DatabaseManager.dropTables();
            DatabaseManager.createTables();
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
        ClearService clear = new ClearService();
        clear.clear();
    }


    @Test
    public void successfullyRegister() {
        ServerFacade facade = new ServerFacade(serverURL);
        String authToken = null;
        try {
            authToken = facade.register("bradle", "goron", "bg@gmail.com");
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
        }
        assertNotNull(authToken);
    }

    @Test
    public void failToRegisterIncompleteRequest() {
        ServerFacade facade = new ServerFacade(serverURL);
        String authToken = null;
        try {
            authToken = facade.register("bradle", null, "bg@gmail.com");
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
        }
        assertNull(authToken);
    }

    @Test
    public void successfullyLoginGivenCorrectPassword() {
        ServerFacade facade = new ServerFacade(serverURL);
        String authToken = null;
        try {
            facade.register("bradle", "goron", "bg@gmail.com");
            authToken = facade.login("bradle", "goron");
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
        }
        assertNotNull(authToken);
    }

    @Test
    public void failToLoginGivenWrongPassword() {
        ServerFacade facade = new ServerFacade(serverURL);
        String authToken = null;
        try {
            facade.register("bradle", "goron", "bg@gmail.com");
            authToken = facade.login("bradle", "boron");
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
        }
        assertNull(authToken);
    }

    @Test
    public void successfullyLogoutGivenValidAuthToken() {
        ServerFacade facade = new ServerFacade(serverURL);
        String authToken;
        try {
            authToken = facade.register("bradle", "goron", "bg@gmail.com");
            facade.logout(authToken);
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
            assert(false);
        }
    }

    @Test
    public void failToLogoutGivenInvalidAuthToken() {
        ServerFacade facade = new ServerFacade(serverURL);
        String authToken = null;
        int finalStatusCode = -1;
        try {
            authToken = facade.register("bradle", "goron", "bg@gmail.com");
            facade.logout("wrong");
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
            finalStatusCode = e.getStatusCode();
        }
        assertNotNull(authToken);
        assertEquals(401, finalStatusCode);
    }

    @Test
    public void successfullyListGames() {
        ServerFacade facade = new ServerFacade(serverURL);
        String authToken;
        ArrayList<GameData> gamesArray = new ArrayList<>();
        ArrayList<GameData> expectedGamesArray = new ArrayList<>();
        expectedGamesArray.add(
                new GameData(1, null, null, "game 1", new ChessGame())
        );
        expectedGamesArray.add(
                new GameData(2, null, null, "game 2", new ChessGame())
        );
        expectedGamesArray.add(
                new GameData(3, null, null, "game 3", new ChessGame())
        );

        try {
            authToken = facade.register("bradle", "goron", "bg@gmail.com");
            facade.createGame("game 1", authToken);
            facade.createGame("game 2", authToken);
            facade.createGame("game 3", authToken);
            gamesArray = facade.listGames(authToken);
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
            assert(false);
        }

        assertEquals(expectedGamesArray.size(), gamesArray.size());
        assertEquals(expectedGamesArray.get(0), gamesArray.get(0));
        assertEquals(expectedGamesArray.get(1), gamesArray.get(1));
        assertEquals(expectedGamesArray.get(2), gamesArray.get(2));
    }

    @Test
    public void listGamesShowsEmptyListWhenNoGames() {
        ServerFacade facade = new ServerFacade(serverURL);
        String authToken;
        ArrayList<GameData> gamesArray = new ArrayList<>();
        ArrayList<GameData> expectedGamesArray = new ArrayList<>();
        try {
            authToken = facade.register("bradle", "goron", "bg@gmail.com");
            gamesArray = facade.listGames(authToken);
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
            assert(false);
        }

        assertEquals(expectedGamesArray, gamesArray);
    }

    @Test
    public void successfullyCreateGame() {
        ServerFacade facade = new ServerFacade(serverURL);
        String authToken = null;
        int gameId = -1;
        try {
            authToken = facade.register("bradle", "goron", "bg@gmail.com");
            gameId = facade.createGame("game time", authToken);
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
            assert(false);
        }

        assertNotNull(authToken);
        assertNotEquals(-1, gameId);
    }

    @Test
    public void failToCreateGameIncompleteRequest() {
        ServerFacade facade = new ServerFacade(serverURL);
        String authToken = null;
        int finalStatusCode = -1;
        int gameID = -1;
        try {
            authToken = facade.register("bradle", "goron", "bg@gmail.com");
            facade.createGame(null, authToken);
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
            finalStatusCode = e.getStatusCode();
        }
        assertNotNull(authToken);
        assertEquals(-1, gameID);
        assertEquals(400, finalStatusCode);
    }

    @Test
    public void successfullyJoinGame() {
        ServerFacade facade = new ServerFacade(serverURL);
        ArrayList<GameData> gamesArray;
        String authToken;
        GameData targetedGame = new GameData(-1, "white", "black", "cake", new ChessGame());
        try {
            authToken = facade.register("bradle", "goron", "test@test.com");
            facade.createGame("game1", authToken);
            facade.createGame("game2", authToken);
            facade.joinGame(2, "WHITE", authToken);
            gamesArray = facade.listGames(authToken);
            targetedGame = gamesArray.get(1);
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
        }
        assertEquals(2, targetedGame.gameID());
        assertEquals("game2", targetedGame.gameName());
        assertEquals("bradle", targetedGame.whiteUsername());
        assertNull(targetedGame.blackUsername());
    }

    @Test
    public void failToJoinGameColorAlreadyTaken() {
        ServerFacade facade = new ServerFacade(serverURL);
        String authToken;
        int finalStatusCode = -1;
        try {
            authToken = facade.register("bradle", "goron", "test@test.com");
            facade.createGame("game1", authToken);
            facade.createGame("game2", authToken);
            facade.joinGame(2, "WHITE", authToken);
            facade.joinGame(2, "WHITE", authToken);
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
            finalStatusCode = e.getStatusCode();
        }
        assertEquals(403, finalStatusCode);
    }
}
