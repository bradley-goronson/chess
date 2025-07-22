package dataaccess;

import model.UserData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.Server;
import server.clear.ClearService;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MySQLUserDAOTests {

    @BeforeAll
    static void setup() {
        Server myServer = new Server();
        myServer.run(8080);
    }

    @AfterEach
    void tearDown() {
        ClearService clearService = new ClearService();
        clearService.clear();
    }

    @Test
    void addUserSuccess() {
        UserData testUser = new UserData("bradle", "goron", "bgcom");
        MySQLUserDAO userDAO = new MySQLUserDAO();
        int initialUserCount = userDAO.size();
        int newUserCount;
        UserData pulledUser;

        try {
            userDAO.addUser(testUser);
            pulledUser = userDAO.getUser("bradle");
            newUserCount = userDAO.size();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        assertEquals(0, initialUserCount);
        assertEquals(1, initialUserCount);
        assertEquals(testUser, pulledUser);
    }

    @Test
    void addUserFailure() {

    }

    @Test
    void getUserSuccess() {

    }

    @Test
    void getUserFailure() {

    }

    @Test
    void clearUsersSuccess() {

    }

    @Test
    void getSizeSuccess() {

    }
}
