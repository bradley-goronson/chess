package service;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.clear.ClearService;
import server.register.RegisterRequest;
import server.register.RegisterResult;
import server.register.RegisterService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class RegisterServiceTests {
    @BeforeEach
    void clear() {
        ClearService clearService = new ClearService();
        clearService.clear();
    }

    @Test
    void successfullyRegisterNewUser() {
        RegisterService registerService = new RegisterService();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("bradle1");
        registerRequest.setPassword("goron");
        registerRequest.setEmail("bg@gmail.com");

        RegisterResult registerResult = registerService.register(registerRequest);

        assertEquals(200, registerResult.getStatusCode());
        assertEquals("bradle1", registerResult.getUsername());
        assertNotEquals(null, registerResult.getAuthToken());
        try {
            assertEquals(1, registerService.getUserDAO().size());
        } catch (DataAccessException ex) {
            System.out.println("size error");
        }
    }

    @Test
    void failToRegisterUserWithExistingUsername() {
        RegisterService registerService = new RegisterService();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("bradle1");
        registerRequest.setPassword("goron");
        registerRequest.setEmail("bg@gmail.com");

        RegisterResult registerResult = registerService.register(registerRequest);

        assertEquals(200, registerResult.getStatusCode());
        assertEquals("bradle1", registerResult.getUsername());
        assertNotEquals(null, registerResult.getAuthToken());
        try {
            assertEquals(1, registerService.getUserDAO().size());
        } catch (DataAccessException ex) {
            System.out.println("size error");
        }

        RegisterRequest registerRequest2 = new RegisterRequest();
        registerRequest2.setUsername("bradle1");
        registerRequest2.setPassword("wow");
        registerRequest2.setEmail("bg2@gmail.com");

        RegisterResult registerResult2 = registerService.register(registerRequest2);

        assertEquals(403, registerResult2.getStatusCode());
    }

    @Test
    void badRequestWhenNotGivenAllParameters() {
        RegisterService registerService = new RegisterService();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("bradle1");
        registerRequest.setPassword("goron");

        RegisterResult registerResult = registerService.register(registerRequest);

        assertEquals(400, registerResult.getStatusCode());
    }
}
