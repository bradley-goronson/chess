package dataaccess;

import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import server.Server;
import server.clear.ClearService;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLUserDAOTests {
    @BeforeAll
    static void setup() {
        Server myServer = new Server();
        myServer.run(8080);
    }

    @AfterEach
    void tearDownReset() {
        ClearService clearService = new ClearService();
        clearService.clear();
        try {
            DatabaseManager.dropTables();
            DatabaseManager.createTables();
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void addUserSuccess() {
        UserData testUser = new UserData("tommy", "bumpkins", "bgcom");
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
            pulledUser = userDAO.getUser("tommy");
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
        assertEquals(testUser.username(), pulledUser.username());
        assertEquals(testUser.email(), pulledUser.email());
        BCrypt.checkpw(testUser.password(), pulledUser.password());
    }

    @Test
    void addUserFailure() {
        UserData testUser1 = new UserData("bradle", "goron", "bgcom");
        UserData testUser2 = new UserData("bradle", "goron2", "bgcom2");
        MySQLUserDAO userDAO = new MySQLUserDAO();

        try {
            userDAO.addUser(testUser1);
            userDAO.addUser(testUser2);
        } catch (DataAccessException | AlreadyTakenException e) {
            System.out.println(e.getMessage());
        }

        int userCount = -1;
        try {
            userCount = userDAO.size();
        } catch (DataAccessException ex) {
            System.out.println("size error");
        }

        assertEquals(1, userCount);
    }

    @Test
    void getUserSuccess() {
        UserData testUser1 = new UserData("bradle", "goron", "bgcom");
        UserData testUser2 = new UserData("bradle2", "goron2", "bgcom2");
        MySQLUserDAO userDAO = new MySQLUserDAO();
        UserData pulledUser1;
        UserData pulledUser2;

        try {
            userDAO.addUser(testUser1);
            userDAO.addUser(testUser2);
            pulledUser1 = userDAO.getUser("bradle");
            pulledUser2 = userDAO.getUser("bradle2");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        assertEquals(testUser1.username(), pulledUser1.username());
        assertEquals(testUser1.email(), pulledUser1.email());
        BCrypt.checkpw(testUser1.password(), pulledUser1.password());

        assertEquals(testUser2.username(), pulledUser2.username());
        assertEquals(testUser2.email(), pulledUser2.email());
        BCrypt.checkpw(testUser2.password(), pulledUser2.password());
    }

    @Test
    void getUserFailure() {
        UserData testUser1 = new UserData("bradle", "goron", "bgcom");
        MySQLUserDAO userDAO = new MySQLUserDAO();
        UserData pulledUser1 = null;

        try {
            userDAO.addUser(testUser1);
            pulledUser1 = userDAO.getUser("bradlex");
        } catch (DataAccessException e) {
            System.out.println("user not found");
        }

        assertNull(pulledUser1);
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

        int finalUserCount = -1;
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
        UserData testUser2 = new UserData("tommy", "bumpkins", "bg2@cool.com");

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
