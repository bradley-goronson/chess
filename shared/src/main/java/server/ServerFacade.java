package server;

import chess.ChessMove;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

public class ServerFacade {
    String serverURL;

    public ServerFacade(String serverURL) {
        this.serverURL = serverURL;
    }

    public String register(String username, String password, String email) throws ResponseException {
        ClientRegisterRequest request = new ClientRegisterRequest(username, password, email);
        AuthData authData = makeRequest("POST", "/user", request, AuthData.class, null);
        return authData.authToken();
    }

    public String login(String username, String password) throws ResponseException {
        ClientLoginRequest request = new ClientLoginRequest(username, password);
        AuthData authData = makeRequest("POST", "/session", request, AuthData.class, null);
        return authData.authToken();
    }

    public void logout(String authToken) throws ResponseException {
        makeRequest("DELETE", "/session", null, null, authToken);
    }

    public ArrayList<GameData> listGames(String authToken) throws ResponseException {
        record ListGamesResponse(ArrayList<GameData> games) {}
        ListGamesResponse response = makeRequest("GET", "/game", null, ListGamesResponse.class, authToken);
        return response.games;
    }

    public int createGame(String gameName, String authToken) throws ResponseException {
        ClientCreateGameRequest request = new ClientCreateGameRequest(gameName);
        GameData gameData = makeRequest("POST", "/game", request, GameData.class, authToken);
        return gameData.gameID();
    }

    public void joinGame(Integer gameID, String playerColor, String authToken) throws ResponseException {
        ClientJoinGameRequest request = new ClientJoinGameRequest(playerColor, gameID);
        makeRequest("PUT", "/game", request, null, authToken);
    }

    public void leave(int gameID, boolean isObserver, boolean isWhitePlayer, String authToken) throws ResponseException {
        ClientLeaveRequest request = new ClientLeaveRequest(gameID, isObserver, isWhitePlayer);
        makeRequest("PUT", "/leave", request, null, authToken);
    }

    public GameData makeMove(Integer gameID, ChessMove move, String authToken) throws ResponseException {
        System.out.print("You made it to the point where you're about to create the make move request: Provided move is" + move + "\n");
        ClientMakeMoveRequest request = new ClientMakeMoveRequest(gameID, move);
        System.out.print("You made it to the point where you just made the make move request: Created request is" + request + "\n");
        GameData updatedGameData = makeRequest("PUT", "/move", request, GameData.class, authToken);
        System.out.print("You made it to the point where you're about to return the updated game on the client side. The game: " + updatedGameData + "\n");
        return updatedGameData;
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws ResponseException {
        try {
            URL url = (new URI(serverURL + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            http.setRequestProperty("authorization", authToken);
            writeBody(request, http);
            http.connect();
            throwIfUnsuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException e) {
            throw e;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String requestData = new Gson().toJson(request);
            try (OutputStream requestBody = http.getOutputStream()) {
                requestBody.write(requestData.getBytes());
            }
        }
    }

    private <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream responseBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(responseBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private void throwIfUnsuccessful(HttpURLConnection http) throws IOException, ResponseException {
        int status = http.getResponseCode();
        if (!(status / 100 == 2)) {
            try (InputStream responseError = http.getErrorStream()) {
                if (responseError != null) {
                    throw ResponseException.fromJson(responseError);
                }
            }
            throw new ResponseException(status, "some failure: " + status);
        }
    }
}
