package service;

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
        testClearService.getUserDAO().addUser(user1);
        testClearService.getUserDAO().addUser(user2);
        testClearService.getUserDAO().addUser(user3);

        assertEquals(3, testClearService.getUserDAO().size());

        testClearService.getGameDAO().addGame("mario party");
        testClearService.getGameDAO().addGame("home evening");
        testClearService.getGameDAO().addGame("rival match");

        assertEquals(3, testClearService.getUserDAO().size());

        testClearService.getAuthDAO().addAuth("bradle1");
        testClearService.getAuthDAO().addAuth("bradle1");
        testClearService.getAuthDAO().addAuth("bradle1");

        assertEquals(3, testClearService.getAuthDAO().size());



        testClearService.clear();

        assertEquals(0, testClearService.getUserDAO().size());
        assertEquals(0, testClearService.getGameDAO().size());
        assertEquals(0, testClearService.getAuthDAO().size());
    }
}
