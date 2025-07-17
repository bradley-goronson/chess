package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.clear.ClearService;
import server.logout.LogoutRequest;
import server.logout.LogoutResult;
import server.logout.LogoutService;
import server.register.RegisterRequest;
import server.register.RegisterResult;
import server.register.RegisterService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class LogoutServiceTests {
    @BeforeEach
    void clear() {
        ClearService clearService = new ClearService();
        clearService.clear();
    }

    @Test
    void successfullyLogoutWhenGivenValidAuthToken() {
        RegisterService registerService = new RegisterService();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("bradle1");
        registerRequest.setPassword("goron");
        registerRequest.setEmail("bg@gmail.com");

        RegisterResult registerResult = registerService.register(registerRequest);

        assertEquals(200, registerResult.getStatusCode());
        assertEquals("bradle1", registerResult.getUsername());
        assertNotEquals(null, registerResult.getAuthToken());

        LogoutService logoutService = new LogoutService();
        LogoutRequest logoutRequest = new LogoutRequest();
        logoutRequest.setAuthorization(registerResult.getAuthToken());

        LogoutResult logoutResult = logoutService.logout(logoutRequest);
        assertEquals(200, logoutResult.getStatusCode());
    }

    @Test
    void failToLogoutWhenGivenInvalidAuthToken() {
        RegisterService registerService = new RegisterService();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("bradle1");
        registerRequest.setPassword("goron");
        registerRequest.setEmail("bg@gmail.com");

        RegisterResult registerResult = registerService.register(registerRequest);

        assertEquals(200, registerResult.getStatusCode());
        assertEquals("bradle1", registerResult.getUsername());
        assertNotEquals(null, registerResult.getAuthToken());

        LogoutService logoutService = new LogoutService();
        LogoutRequest logoutRequest = new LogoutRequest();
        logoutRequest.setAuthorization("24");

        LogoutResult logoutResult = logoutService.logout(logoutRequest);
        assertEquals(401, logoutResult.getStatusCode());
    }
}
