package dataaccess;

import chess.ChessGame;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.GameNotFoundException;
import model.GameData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.Server;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLGameDAOTests {
    static Server myServer = new Server();

    @BeforeAll
    static void setup() {
        myServer.run(8080);
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
    static void closeConnection() {
        myServer.stop();
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

        int initalGameCount = -1;
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

        int finalGameCount = -1;
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
        MySQLGameDAO gameDAO = new MySQLGameDAO();
        GameData pulledGame = new GameData(-1, "wow", "bow", null, null);
        int gameID = 0;

        try {
            gameID = gameDAO.addGame("brud time");
            pulledGame = gameDAO.getGame(gameID);
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }

        assertEquals(gameID, pulledGame.gameID());
        assertNull(pulledGame.whiteUsername());
        assertNull(pulledGame.blackUsername());
        assertEquals("brud time", pulledGame.gameName());
        assertNotNull(pulledGame.game());
    }

    @Test
    void getGameFailure() {
        MySQLGameDAO gameDAO = new MySQLGameDAO();
        GameData pulledGame = new GameData(-1, "wow", "bow", null, null);
        int gameID = 0;

        try {
            gameID = gameDAO.addGame("brud time");
            pulledGame = gameDAO.getGame(-1);
        } catch (DataAccessException | GameNotFoundException e) {
            System.out.println(e.getMessage());
        }

        assertNotEquals(0, gameID);
        assertNotNull(pulledGame.whiteUsername());
        assertNotNull(pulledGame.blackUsername());
        assertNull(pulledGame.gameName());
        assertNull(pulledGame.game());
    }

    @Test
    void updateGameSuccess() {
        MySQLGameDAO gameDAO = new MySQLGameDAO();
        int gameID = 0;
        GameData generatedGame = new GameData(null, null, null, null, null);
        GameData updatedGame = new GameData(null, null, null, null, null);

        try {
            gameID = gameDAO.addGame("brud time");
            generatedGame = gameDAO.getGame(gameID);
            updatedGame = new GameData(gameID, "wow", "bow", generatedGame.gameName(), generatedGame.game());
            gameDAO.updateGame(gameID, updatedGame);
            updatedGame = gameDAO.getGame(gameID);
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }

        assertEquals(gameID, updatedGame.gameID());
        assertEquals("wow", updatedGame.whiteUsername());
        assertEquals("bow", updatedGame.blackUsername());
        assertEquals("brud time", updatedGame.gameName());
        assertEquals(generatedGame.game(), updatedGame.game());
    }

    @Test
    void updateGameFailure() {
        MySQLGameDAO gameDAO = new MySQLGameDAO();
        int gameID = 0;
        GameData generatedGame = new GameData(null, null, null, null, null);
        GameData updatedGame = new GameData(null, null, null, null, null);
        GameData pulledGame = new GameData(null, null, null, null, null);

        try {
            gameID = gameDAO.addGame("brud time");
            generatedGame = gameDAO.getGame(gameID);
            updatedGame = new GameData(gameID, "wow", "bow", generatedGame.gameName(), generatedGame.game());
            gameDAO.updateGame(-1, updatedGame);
        } catch (DataAccessException | GameNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            pulledGame = gameDAO.getGame(gameID);
        } catch (DataAccessException | GameNotFoundException ex) {
            System.out.println(ex.getMessage());
        }

        assertEquals(generatedGame.gameID(), pulledGame.gameID());
        assertEquals(generatedGame.whiteUsername(), pulledGame.whiteUsername());
        assertEquals(generatedGame.blackUsername(), pulledGame.blackUsername());
        assertEquals(generatedGame.gameName(), pulledGame.gameName());
        assertEquals(generatedGame.game(), pulledGame.game());
    }

    @Test
    void getAllGamesSuccess() {
        MySQLGameDAO gameDAO = new MySQLGameDAO();
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
            gameDAO.addGame("game 1");
            gameDAO.addGame("game 2");
            gameDAO.addGame("game 3");
            gamesArray = gameDAO.getAllGames();
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }

        assertEquals(expectedGamesArray.size(), gamesArray.size());
        assertEquals(expectedGamesArray.get(0), gamesArray.get(0));
        assertEquals(expectedGamesArray.get(1), gamesArray.get(1));
        //assertEquals(expectedGamesArray.get(2), gamesArray.get(2));
    }

    @Test
    void getAllGamesFailure() {
        MySQLGameDAO gameDAO = new MySQLGameDAO();
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
            gameDAO.addGame("game 1");
            gameDAO.addGame("game 2");
            gameDAO.addGame("game 3");
            gamesArray = gameDAO.getAllGames();
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }

        assertEquals(expectedGamesArray.size(), gamesArray.size());
        assertEquals(expectedGamesArray.get(0), gamesArray.get(0));
        assertEquals(expectedGamesArray.get(1), gamesArray.get(1));
        assertEquals(expectedGamesArray.get(2), gamesArray.get(2));
    }

    @Test
    void clearGamesSuccess() {
        MySQLGameDAO gameDAO = new MySQLGameDAO();
        int initialGameCount = getInitialGameCount(gameDAO);
        int newGameCount = getGameCountAfterAddingGames(gameDAO);

        int finalGameCount = -1;
        try {
            gameDAO.clearGames();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        try {
            finalGameCount = gameDAO.size();
        } catch (DataAccessException e) {
            System.out.println("size error");
        }

        assertEquals(0, initialGameCount);
        assertEquals(2, newGameCount);
        assertEquals(0, finalGameCount);
    }

    @Test
    void getSizeSuccess() {
        MySQLGameDAO gameDAO = new MySQLGameDAO();
        int initialGameCount = getInitialGameCount(gameDAO);
        int newGameCount = getGameCountAfterAddingGames(gameDAO);

        assertEquals(0, initialGameCount);
        assertEquals(2,  newGameCount);
    }

    private int getInitialGameCount(MySQLGameDAO gameDAO) {
        int initialGameCount = -1;
        try {
            initialGameCount = gameDAO.size();
        } catch (DataAccessException ex) {
            System.out.println("size error");
        }
        return initialGameCount;
    }

    private int getGameCountAfterAddingGames(MySQLGameDAO gameDAO) {
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
        return newGameCount;
    }
}
