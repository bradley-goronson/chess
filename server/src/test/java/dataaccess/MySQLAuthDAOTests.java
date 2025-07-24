package dataaccess;

import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.UnauthorizedException;
import model.AuthData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MySQLAuthDAOTests extends MySQLDAOTests {
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
            System.out.println(e.getMessage());
        }

        try {
            finalRowCount = authDAO.size();
        } catch (DataAccessException e) {
            System.out.println("size error");
        }

        assertEquals(0, finalRowCount);
    }

    @Test
    void getAuthSuccess() {
        String firstUsername = "bradle";
        AuthData testAuth = new AuthData("thunder", "pikachu");
        String generatedAuthToken;
        MySQLAuthDAO authDAO = new MySQLAuthDAO();

        try {
            generatedAuthToken = authDAO.addAuth(firstUsername);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        try {
            testAuth = authDAO.getAuth(generatedAuthToken);
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }

        assertEquals(testAuth.authToken(), generatedAuthToken);
    }

    @Test
    void getAuthFailure() {
        String firstUsername = "bradle";
        AuthData testAuth = null;
        MySQLAuthDAO authDAO = new MySQLAuthDAO();

        try {
            authDAO.addAuth(firstUsername);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        try {
            testAuth = authDAO.getAuth("it's a wonderful life");
        } catch (DataAccessException | UnauthorizedException e) {
            System.out.println(e.getMessage());
        }

        assertNull(testAuth);
    }

    @Test
    void removeAuthSuccess() {
        String firstUsername = "bradle";
        String generatedAuthToken;
        int updatedRowCount = -1;
        int finalRowCount = -1;

        MySQLAuthDAO authDAO = new MySQLAuthDAO();
        try {
            generatedAuthToken = authDAO.addAuth(firstUsername);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        try {
            updatedRowCount = authDAO.size();
        } catch (DataAccessException e) {
            System.out.println("size error");
        }

        try {
            authDAO.removeAuth(generatedAuthToken);
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }

        try {
            finalRowCount = authDAO.size();
        } catch (DataAccessException e) {
            System.out.println("size error");
        }

        assertEquals(1, updatedRowCount);
        assertEquals(0, finalRowCount);
    }

    @Test
    void removeAuthFailure() {
        String firstUsername = "bradle";
        int updatedRowCount = -1;
        int finalRowCount = -1;

        MySQLAuthDAO authDAO = new MySQLAuthDAO();
        try {
            authDAO.addAuth(firstUsername);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        try {
            updatedRowCount = authDAO.size();
        } catch (DataAccessException e) {
            System.out.println("size error");
        }

        try {
            authDAO.removeAuth("I like turtles");
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }

        try {
            finalRowCount = authDAO.size();
        } catch (DataAccessException e) {
            System.out.println("size error");
        }

        assertEquals(1, updatedRowCount);
        assertEquals(1, finalRowCount);
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

        try {
            authDAO.clearAuth();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

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
        String username1 = "bradle1";
        String username2 = "bradle2";
        MySQLAuthDAO authDAO = new MySQLAuthDAO();

        int initialUserCount = -1;
        try {
            initialUserCount = authDAO.size();
        } catch (DataAccessException ex) {
            System.out.println("size error");
        }

        int newUserCount = 0;
        try {
            authDAO.addAuth(username1);
            authDAO.addAuth(username2);
            try {
                newUserCount = authDAO.size();
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
