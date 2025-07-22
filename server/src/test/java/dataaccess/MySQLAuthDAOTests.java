package dataaccess;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.Server;
import server.clear.ClearService;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MySQLAuthDAOTests {
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
    void addAuthSuccess() {
        String firstUsername = "bradle";
        int finalRowCount = -1;

        MySQLAuthDAO authDAO = new MySQLAuthDAO();
        try {
            authDAO.addAuth(firstUsername);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        try {
            finalRowCount = authDAO.size();
        } catch (DataAccessException e) {
            System.out.println("size error");
        }

        assertEquals(1, finalRowCount);
    }

    @Test
    void addAuthFailure() {
        String firstUsername = null;
        int finalRowCount = -1;

        MySQLAuthDAO authDAO = new MySQLAuthDAO();
        try {
            authDAO.addAuth(firstUsername);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        try {
            finalRowCount = authDAO.size();
        } catch (DataAccessException e) {
            System.out.println("size error");
        }

        assertEquals(1, finalRowCount);
    }

    @Test
    void getAuthSuccess() {

    }

    @Test
    void getAuthFailure() {

    }

    @Test
    void removeAuthSuccess() {

    }

    @Test
    void removeAuthFailure() {

    }

    @Test
    void clearAuthSuccess() {
        String firstUsername = "bradle";
        String secondUsername = "bradle2";
        MySQLAuthDAO authDAO = new MySQLAuthDAO();

        int initalRowCount = -1;
        try {
            initalRowCount = authDAO.size();
        } catch (DataAccessException e) {
            System.out.println("size error");
        }

        try {
            authDAO.addAuth(firstUsername);
            authDAO.addAuth(secondUsername);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        int updatedRowCount = -1;
        try {
            updatedRowCount = authDAO.size();
        } catch (DataAccessException e) {
            System.out.println("size error");
        }

        authDAO.clearAuth();

        int finalRowCount = -1;
        try {
            finalRowCount = authDAO.size();
        } catch (DataAccessException e) {
            System.out.println("size error");
        }

        assertEquals(0, initalRowCount);
        assertEquals(2, updatedRowCount);
        assertEquals(0, finalRowCount);
    }

    @Test
    void getSizeSuccess() {

    }
}
