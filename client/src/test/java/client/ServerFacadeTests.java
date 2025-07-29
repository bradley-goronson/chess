package client;

import org.junit.jupiter.api.*;
import server.ResponseException;
import server.Server;
import server.ServerFacade;
import server.clear.ClearService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


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
    public void successfullyLogin() {
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
}
