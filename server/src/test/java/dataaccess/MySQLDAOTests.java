package dataaccess;

import dataaccess.exceptions.DataAccessException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import server.Server;

public class MySQLDAOTests {
    static Server myServer = new Server();

    @BeforeAll
    static void setup() {
        myServer.run(8080);
    }

    @AfterEach
    void tearDownReset() {
        try {
            DatabaseManager.dropTables();
            DatabaseManager.createTables();
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    @AfterAll
    static void closeConnection() {
        myServer.stop();
    }
}
