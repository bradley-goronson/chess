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
        int initialUserCount = 0;
        try {
            initialUserCount = userDAO.size();
        } catch (DataAccessException ex) {
            System.out.println("size error");
        }

        UserData pulledUser;

        int newUserCount = 0;
        try {
            userDAO.addUser(testUser);
            pulledUser = userDAO.getUser("bradle");
            try {
                newUserCount = userDAO.size();
            } catch (DataAccessException ex) {
                System.out.println("size error");
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        assertEquals(0, initialUserCount);
        assertEquals(1, newUserCount);
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
        MySQLUserDAO userDAO = new MySQLUserDAO();
        int initialUserCount = 0;
        try {
            initialUserCount = userDAO.size();
        } catch (DataAccessException ex) {
            System.out.println("size error");
        }

        UserData testUser1 = new UserData("bradle", "goron", "bg@cool.com");
        UserData testUser2 = new UserData("bradle2", "goron2", "bg2@cool.com");

        int newUserCount = 0;
        try {
            userDAO.addUser(testUser1);
            userDAO.addUser(testUser2);
            try {
                newUserCount = userDAO.size();
            } catch (DataAccessException ex) {
                System.out.println("size error");
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        int finalUserCount = 100;
        userDAO.clearUsers();
        try {
            finalUserCount = userDAO.size();
        } catch (DataAccessException e) {
            System.out.println("size error");
        }

        assertEquals(0, initialUserCount);
        assertEquals(2, newUserCount);
        assertEquals(0, finalUserCount);
    }

    @Test
    void getSizeSuccess() {
        UserData testUser1 = new UserData("bradle", "goron", "bg@cool.com");
        UserData testUser2 = new UserData("bradle2", "goron2", "bg2@cool.com");

        MySQLUserDAO userDAO = new MySQLUserDAO();
        int initialUserCount = 0;
        try {
            initialUserCount = userDAO.size();
        } catch (DataAccessException ex) {
            System.out.println("size error");
        }

        int newUserCount = 0;
        try {
            userDAO.addUser(testUser1);
            userDAO.addUser(testUser2);
            try {
                newUserCount = userDAO.size();
            } catch (DataAccessException ex) {
                System.out.println("size error");
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        assertEquals(0, initialUserCount);
        assertEquals(2, newUserCount);
    }
}
