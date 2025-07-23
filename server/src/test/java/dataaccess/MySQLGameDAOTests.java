package dataaccess;

import chess.ChessGame;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DataAccessException;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.Server;
import server.clear.ClearService;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLGameDAOTests {
    @BeforeAll
    static void setup() {
        Server myServer = new Server();
        myServer.run(8080);
    }

    @AfterEach
    void tearDownReset() {
        ClearService clearService = new ClearService();
        clearService.clear();
        try {
            DatabaseManager.dropTables();
            DatabaseManager.createTables();
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void addGameSuccess() {
        MySQLGameDAO gameDAO = new MySQLGameDAO();
        GameData generatedGame = new GameData(-1, "wow", "bow", null, null);
        int gameID = 0;

        int initalGameCount = 0;
        try {
            initalGameCount = gameDAO.size();
        } catch (DataAccessException ex) {
            System.out.println("size error");
        }

        try {
            gameID = gameDAO.addGame("brud time");
            generatedGame = gameDAO.getGame(gameID);
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }

        int finalGameCount = 0;
        try {
            finalGameCount = gameDAO.size();
        } catch (DataAccessException ex) {
            System.out.println("size error");
        }

        assertEquals(0, initalGameCount);
        assertEquals(1, finalGameCount);
        assertEquals(gameID, generatedGame.gameID());
        assertNull(generatedGame.whiteUsername());
        assertNull(generatedGame.blackUsername());
        assertEquals("brud time", generatedGame.gameName());
        assertNotNull(generatedGame.game());
    }

    @Test
    void addGameFailure() {
        MySQLGameDAO gameDAO = new MySQLGameDAO();
        int gameID = 0;

        int initalGameCount = 0;
        try {
            initalGameCount = gameDAO.size();
        } catch (DataAccessException ex) {
            System.out.println("size error");
        }

        try {
            gameID = gameDAO.addGame(null);
        } catch (BadRequestException | DataAccessException e) {
            System.out.println(e.getMessage());
        }

        int finalGameCount = 0;
        try {
            finalGameCount = gameDAO.size();
        } catch (DataAccessException ex) {
            System.out.println("size error");
        }

        assertEquals(0, initalGameCount);
        assertEquals(0, finalGameCount);
        assertEquals(0, gameID);
    }

    @Test
    void getGameSuccess() {

    }

    @Test
    void getGameFailure() {

    }

    @Test
    void updateGameSuccess() {

    }

    @Test
    void updateGameFailure() {

    }

    @Test
    void getAllGamesSuccess() {

    }

    @Test
    void getAllGamesFailure() {

    }

    @Test
    void clearGamesSuccess() {

    }

    @Test
    void getSizeSuccess() {
        GameData testGame1 = new GameData(1, null, null, "cool", new ChessGame());
        GameData testGame2 = new GameData(2, null, null, "cool2", new ChessGame());

        MySQLGameDAO gameDAO = new MySQLGameDAO();
        int initialGameCount = -1;
        try {
            initialGameCount = gameDAO.size();
        } catch (DataAccessException ex) {
            System.out.println("size error");
        }

        int newGameCount = -1;
        try {
            gameDAO.addGame("cool");
            gameDAO.addGame("cool2");
            try {
                newGameCount = gameDAO.size();
            } catch (DataAccessException ex) {
                System.out.println("size error");
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        assertEquals(0, initialGameCount);
        assertEquals(2,  newGameCount);
    }
}
