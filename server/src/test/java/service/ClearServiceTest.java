package service;

import dataaccess.exceptions.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.clear.ClearService;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClearServiceTest {
    @BeforeEach
    void setup() {
        ClearService clearService = new ClearService();
        clearService.clear();
    }

    @Test
    void clear() {
        ClearService testClearService = new ClearService();

        UserData user1 = new UserData("bradle1", "1", "cool");
        UserData user2 = new UserData("bradle2", "2", "nice");
        UserData user3 = new UserData("bradle3", "3", "frog");
        try {
            testClearService.getUserDAO().addUser(user1);
            testClearService.getUserDAO().addUser(user2);
            testClearService.getUserDAO().addUser(user3);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        try {
            assertEquals(3, testClearService.getUserDAO().size());
        } catch (DataAccessException ex) {
            System.out.println("size error");
        }

        try {
            testClearService.getGameDAO().addGame("mario party");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        try {
            testClearService.getGameDAO().addGame("home evening");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        try {
            testClearService.getGameDAO().addGame("rival match");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        try {
            assertEquals(3, testClearService.getUserDAO().size());
        } catch (DataAccessException ex) {
            System.out.println("size error");
        }

        try {
            testClearService.getAuthDAO().addAuth("bradle1");
            testClearService.getAuthDAO().addAuth("bradle1");
            testClearService.getAuthDAO().addAuth("bradle1");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        try {
            assertEquals(3, testClearService.getAuthDAO().size());
        } catch (DataAccessException e) {
            System.out.println("size error");
        }


        testClearService.clear();

        try {
            assertEquals(0, testClearService.getUserDAO().size());
            assertEquals(0, testClearService.getGameDAO().size());
            assertEquals(0, testClearService.getAuthDAO().size());
        } catch (DataAccessException ex) {
            System.out.println("size error");
        }
    }
}
