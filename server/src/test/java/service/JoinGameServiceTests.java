package service;

import dataaccess.GameDataAccess;
import dataaccess.exceptions.GameNotFoundException;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.clear.ClearService;
import server.creategame.CreateGameRequest;
import server.creategame.CreateGameResult;
import server.creategame.CreateGameService;
import server.joingame.UpdateGameRequest;
import server.joingame.JoinGameResult;
import server.joingame.JoinGameService;
import server.register.RegisterRequest;
import server.register.RegisterResult;
import server.register.RegisterService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class JoinGameServiceTests {
    @BeforeEach
    void clear() {
        ClearService clearService = new ClearService();
        clearService.clear();
    }
    @Test
    void joinGameGivenGameIDAndPlayerColor() {
        RegisterService registerService = new RegisterService();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("bradle1");
        registerRequest.setPassword("goron");
        registerRequest.setEmail("bg@gmail.com");

        RegisterResult registerResult = registerService.register(registerRequest);

        CreateGameService createGameService = new CreateGameService();
        CreateGameRequest createGameRequest = new CreateGameRequest();
        createGameRequest.setGameName("this is fun");
        createGameRequest.setAuthorization(registerResult.getAuthToken());

        CreateGameResult createGameResult = createGameService.addGame(createGameRequest);

        JoinGameService joinGameService = new JoinGameService();
        UpdateGameRequest joinGameRequest = new UpdateGameRequest();
        joinGameRequest.setGameID(createGameResult.getGameID());
        joinGameRequest.setPlayColor("WHITE");
        joinGameRequest.setAuthorization(registerResult.getAuthToken());

        JoinGameResult joinGameResult = joinGameService.joinGame(joinGameRequest);

        assertEquals(200, joinGameResult.getStatusCode());

        GameDataAccess gameDAO = joinGameService.getGameDAO();
        GameData targetedGame;
        try {
            targetedGame = gameDAO.getGame(createGameResult.getGameID());
        } catch (dataaccess.exceptions.DataAccessException e) {
            throw new RuntimeException(e);
        }
        assertEquals("bradle1", targetedGame.whiteUsername());
    }

    @Test
    void failToJoinGameGivenInvalidGameID() {
        RegisterService registerService = new RegisterService();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("bradle1");
        registerRequest.setPassword("goron");
        registerRequest.setEmail("bg@gmail.com");

        RegisterResult registerResult = registerService.register(registerRequest);

        CreateGameService createGameService = new CreateGameService();
        CreateGameRequest createGameRequest = new CreateGameRequest();
        createGameRequest.setGameName("this is fun");
        createGameRequest.setAuthorization(registerResult.getAuthToken());

        createGameService.addGame(createGameRequest);

        JoinGameService joinGameService = new JoinGameService();
        UpdateGameRequest joinGameRequest = new UpdateGameRequest();
        joinGameRequest.setGameID(24);
        joinGameRequest.setPlayColor("WHITE");
        joinGameRequest.setAuthorization(registerResult.getAuthToken());

        JoinGameResult joinGameResult = joinGameService.joinGame(joinGameRequest);

        assertEquals(400, joinGameResult.getStatusCode());
        GameDataAccess gameDAO = joinGameService.getGameDAO();
        GameData targetedGame = new GameData(null, null, null, null, null);
        try {
            targetedGame = gameDAO.getGame(1);
        } catch (dataaccess.exceptions.DataAccessException | GameNotFoundException e) {
            System.out.println(e.getMessage());
        }
        assertNull(targetedGame.whiteUsername());
    }

    @Test
    void failToJoinGameIncompleteRequestNoPlayerColor() {
        RegisterService registerService = new RegisterService();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("bradle1");
        registerRequest.setPassword("goron");
        registerRequest.setEmail("bg@gmail.com");

        RegisterResult registerResult = registerService.register(registerRequest);

        CreateGameService createGameService = new CreateGameService();
        CreateGameRequest createGameRequest = new CreateGameRequest();
        createGameRequest.setGameName("this is fun");
        createGameRequest.setAuthorization(registerResult.getAuthToken());

        CreateGameResult createGameResult = createGameService.addGame(createGameRequest);

        JoinGameService joinGameService = new JoinGameService();
        UpdateGameRequest joinGameRequest = new UpdateGameRequest();
        joinGameRequest.setGameID(1);
        joinGameRequest.setPlayColor(null);
        joinGameRequest.setAuthorization(registerResult.getAuthToken());

        JoinGameResult joinGameResult = joinGameService.joinGame(joinGameRequest);
        GameDataAccess gameDAO = joinGameService.getGameDAO();

        GameData targetGame;
        try {
            targetGame = gameDAO.getGame(createGameResult.getGameID());
        } catch (dataaccess.exceptions.DataAccessException e) {
            throw new RuntimeException(e);
        }

        assertEquals(400, joinGameResult.getStatusCode());
        assertNull(targetGame.whiteUsername());
    }

    @Test
    void failToJoinGameIncompleteRequestNoAuthToken() {
        RegisterService registerService = new RegisterService();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("bradle1");
        registerRequest.setPassword("goron");
        registerRequest.setEmail("bg@gmail.com");

        RegisterResult registerResult = registerService.register(registerRequest);

        CreateGameService createGameService = new CreateGameService();
        CreateGameRequest createGameRequest = new CreateGameRequest();
        createGameRequest.setGameName("this is fun");
        createGameRequest.setAuthorization(registerResult.getAuthToken());

        CreateGameResult createGameResult = createGameService.addGame(createGameRequest);

        JoinGameService joinGameService = new JoinGameService();
        UpdateGameRequest joinGameRequest = new UpdateGameRequest();
        joinGameRequest.setGameID(1);
        joinGameRequest.setPlayColor("WHITE");
        joinGameRequest.setAuthorization(null);

        JoinGameResult joinGameResult = joinGameService.joinGame(joinGameRequest);
        GameDataAccess gameDAO = joinGameService.getGameDAO();

        GameData targetGame;
        try {
            targetGame = gameDAO.getGame(createGameResult.getGameID());
        } catch (dataaccess.exceptions.DataAccessException e) {
            throw new RuntimeException(e);
        }

        assertEquals(400, joinGameResult.getStatusCode());
        assertNull(targetGame.whiteUsername());
        assertNull(targetGame.blackUsername());
    }

    @Test
    void failToJoinGameIncompleteRequestNoGameID() {
        RegisterService registerService = new RegisterService();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("bradle1");
        registerRequest.setPassword("goron");
        registerRequest.setEmail("bg@gmail.com");

        RegisterResult registerResult = registerService.register(registerRequest);

        CreateGameService createGameService = new CreateGameService();
        CreateGameRequest createGameRequest = new CreateGameRequest();
        createGameRequest.setGameName("this is fun");
        createGameRequest.setAuthorization(registerResult.getAuthToken());

        CreateGameResult createGameResult = createGameService.addGame(createGameRequest);

        JoinGameService joinGameService = new JoinGameService();
        UpdateGameRequest joinGameRequest = new UpdateGameRequest();
        joinGameRequest.setGameID(null);
        joinGameRequest.setPlayColor("WHITE");
        joinGameRequest.setAuthorization(registerResult.getAuthToken());

        JoinGameResult joinGameResult = joinGameService.joinGame(joinGameRequest);
        GameDataAccess gameDAO = joinGameService.getGameDAO();
        GameData targetGame;
        try {
            targetGame = gameDAO.getGame(createGameResult.getGameID());
        } catch (dataaccess.exceptions.DataAccessException e) {
            throw new RuntimeException(e);
        }

        assertEquals(400, joinGameResult.getStatusCode());
        assertNull(targetGame.whiteUsername());
    }
}
