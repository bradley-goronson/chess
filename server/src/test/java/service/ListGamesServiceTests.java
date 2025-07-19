package service;

import dataaccess.GameDataAccess;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.clear.ClearService;
import server.creategame.CreateGameRequest;
import server.creategame.CreateGameService;
import server.listgames.ListGamesRequest;
import server.listgames.ListGamesResult;
import server.listgames.ListGamesService;
import server.register.RegisterRequest;
import server.register.RegisterResult;
import server.register.RegisterService;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ListGamesServiceTests {
    @BeforeEach
    void clear() {
        ClearService clearService = new ClearService();
        clearService.clear();
    }

    @Test
    void successfullyListGamesWhenGivenValidAuthToken() {
        RegisterService registerService = new RegisterService();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("bradle1");
        registerRequest.setPassword("goron");
        registerRequest.setEmail("bg@gmail.com");

        RegisterResult registerResult = registerService.register(registerRequest);

        assertEquals(200, registerResult.getStatusCode());
        assertEquals("bradle1", registerResult.getUsername());
        assertNotEquals(null, registerResult.getAuthToken());

        CreateGameService createGameService = new CreateGameService();
        CreateGameRequest createGameRequest = new CreateGameRequest();
        createGameRequest.setGameName("this is fun");
        createGameRequest.setAuthorization(registerResult.getAuthToken());

        createGameService.addGame(createGameRequest);

        CreateGameRequest createGameRequest2 = new CreateGameRequest();
        createGameRequest2.setGameName("this is fun");
        createGameRequest2.setAuthorization(registerResult.getAuthToken());

        createGameService.addGame(createGameRequest2);

        ListGamesService listGamesService = new ListGamesService();
        ListGamesRequest listGamesRequest = new ListGamesRequest();
        listGamesRequest.setAuthorization(registerResult.getAuthToken());

        ListGamesResult listGamesResult = listGamesService.listGames(listGamesRequest);
        assertEquals(200, listGamesResult.getStatusCode());

        GameDataAccess gameDAO = listGamesService.getGameDAO();
        assertEquals(2, gameDAO.size());

        ArrayList<GameData> gamesArray = gameDAO.getAllGames();
        for (GameData game : gamesArray) {
            System.out.println(game);
        }
    }

    @Test
    void failToListGamesWhenGivenInvalidAuthToken() {
        RegisterService registerService = new RegisterService();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("bradle1");
        registerRequest.setPassword("goron");
        registerRequest.setEmail("bg@gmail.com");

        RegisterResult registerResult = registerService.register(registerRequest);

        assertEquals(200, registerResult.getStatusCode());
        assertEquals("bradle1", registerResult.getUsername());
        assertNotEquals(null, registerResult.getAuthToken());

        CreateGameService createGameService = new CreateGameService();
        CreateGameRequest createGameRequest = new CreateGameRequest();
        createGameRequest.setGameName("this is fun");
        createGameRequest.setAuthorization(registerResult.getAuthToken());
        createGameService.addGame(createGameRequest);

        ListGamesService listGamesService = new ListGamesService();
        ListGamesRequest listGamesRequest = new ListGamesRequest();
        listGamesRequest.setAuthorization("24");

        ListGamesResult listGamesResult = listGamesService.listGames(listGamesRequest);
        assertEquals(401, listGamesResult.getStatusCode());
    }
}
