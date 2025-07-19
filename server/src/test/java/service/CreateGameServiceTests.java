package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.clear.ClearService;
import server.createGame.CreateGameRequest;
import server.createGame.CreateGameResult;
import server.createGame.CreateGameService;
import server.register.RegisterRequest;
import server.register.RegisterResult;
import server.register.RegisterService;

import static org.junit.jupiter.api.Assertions.*;

public class CreateGameServiceTests {
    @BeforeEach
    void clear() {
        ClearService clearService = new ClearService();
        clearService.clear();
    }
    @Test
    void createGameGivenGameName() {
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

        assertEquals(200, createGameResult.getStatusCode());
        assertNotNull(createGameResult.getGameID());
    }

    @Test
    void failToCreateGameUnauthorized() {
        RegisterService registerService = new RegisterService();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("bradle1");
        registerRequest.setPassword("goron");
        registerRequest.setEmail("bg@gmail.com");

        registerService.register(registerRequest);

        CreateGameService createGameService = new CreateGameService();
        CreateGameRequest createGameRequest = new CreateGameRequest();
        createGameRequest.setGameName("this is fun");
        createGameRequest.setAuthorization("24");

        CreateGameResult createGameResult = createGameService.addGame(createGameRequest);

        assertEquals(401, createGameResult.getStatusCode());
        assertNull(createGameResult.getGameID());
    }

    @Test
    void failToCreateGameIncompleteRequestNoGameName() {
        CreateGameService createGameService = new CreateGameService();
        CreateGameRequest createGameRequest = new CreateGameRequest();
        createGameRequest.setAuthorization("24");

        CreateGameResult createGameResult = createGameService.addGame(createGameRequest);

        assertEquals(400, createGameResult.getStatusCode());
        assertNull(createGameResult.getGameID());
    }

    @Test
    void failToCreateGameIncompleteRequestNoAuthToken() {
        CreateGameService createGameService = new CreateGameService();
        CreateGameRequest createGameRequest = new CreateGameRequest();
        createGameRequest.setGameName("much fun");

        CreateGameResult createGameResult = createGameService.addGame(createGameRequest);

        assertEquals(400, createGameResult.getStatusCode());
        assertNull(createGameResult.getGameID());
    }
}
