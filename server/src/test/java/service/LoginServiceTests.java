package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.clear.ClearService;
import server.login.LoginRequest;
import server.login.LoginResult;
import server.login.LoginService;
import server.register.RegisterRequest;
import server.register.RegisterResult;
import server.register.RegisterService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class LoginServiceTests {
    @BeforeEach
    void clear() {
        ClearService clearService = new ClearService();
        clearService.clear();
    }

    @Test
    void successfullyLoginWhenGivenCorrectPassword() {
        RegisterService registerService = new RegisterService();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("bradle1");
        registerRequest.setPassword("goron");
        registerRequest.setEmail("bg@gmail.com");

        RegisterResult registerResult = registerService.register(registerRequest);

        assertEquals(200, registerResult.getStatusCode());
        assertEquals("bradle1", registerResult.getUsername());
        assertNotEquals(null, registerResult.getAuthToken());

        LoginService loginService = new LoginService();
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("bradle1");
        loginRequest.setPassword("goron");

        LoginResult loginResult = loginService.login(loginRequest);
        assertEquals(200, loginResult.getStatusCode());
        assertEquals("bradle1", loginResult.getUsername());
        assertNotEquals(null, loginResult.getAuthToken());
    }

    @Test
    void failToLoginWhenGivenIncorrectPassword() {
        RegisterService registerService = new RegisterService();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("bradle1");
        registerRequest.setPassword("goron");
        registerRequest.setEmail("bg@gmail.com");

        RegisterResult registerResult = registerService.register(registerRequest);

        assertEquals(200, registerResult.getStatusCode());
        assertEquals("bradle1", registerResult.getUsername());
        assertNotEquals(null, registerResult.getAuthToken());

        LoginService loginService = new LoginService();
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("bradle1");
        loginRequest.setPassword("wowzers");

        LoginResult loginResult = loginService.login(loginRequest);
        assertEquals(401, loginResult.getStatusCode());
    }

    @Test
    void failToLoginWhenGivenIncompleteRequest() {
        RegisterService registerService = new RegisterService();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("bradle1");
        registerRequest.setPassword("goron");
        registerRequest.setEmail("bg@gmail.com");

        RegisterResult registerResult = registerService.register(registerRequest);

        assertEquals(200, registerResult.getStatusCode());
        assertEquals("bradle1", registerResult.getUsername());
        assertNotEquals(null, registerResult.getAuthToken());

        LoginService loginService = new LoginService();
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("bradle1");
        loginRequest.setPassword(null);

        LoginResult loginResult = loginService.login(loginRequest);
        assertEquals(400, loginResult.getStatusCode());
    }

    @Test
    void failToLoginWhenGivenIncompleteRequestNoUsername() {
        RegisterService registerService = new RegisterService();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("bradle1");
        registerRequest.setPassword("goron");
        registerRequest.setEmail("bg@gmail.com");

        RegisterResult registerResult = registerService.register(registerRequest);

        assertEquals(200, registerResult.getStatusCode());
        assertEquals("bradle1", registerResult.getUsername());
        assertNotEquals(null, registerResult.getAuthToken());

        LoginService loginService = new LoginService();
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(null);
        loginRequest.setPassword("wowzers");

        LoginResult loginResult = loginService.login(loginRequest);
        assertEquals(400, loginResult.getStatusCode());
    }
}
