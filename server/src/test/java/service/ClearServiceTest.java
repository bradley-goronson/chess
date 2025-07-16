package service;

import model.UserData;
import org.junit.jupiter.api.Test;
import server.clear.ClearService;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClearServiceTest {
    @Test
    void clear() {
        UserData user1 = new UserData("bradle1", "1", "cool");
        UserData user2 = new UserData("bradle2", "2", "nice");
        UserData user3 = new UserData("bradle3", "3", "frog");

        ClearService testClearService = new ClearService();
        testClearService.getUserDAO().addUser(user1);
        testClearService.getUserDAO().addUser(user2);
        testClearService.getUserDAO().addUser(user3);

        testClearService.clear();
        assertEquals(0, testClearService.getUserDAO().size());
    }
}
