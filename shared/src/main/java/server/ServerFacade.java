package server;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

public class ServerFacade {
    String serverURL = "http://localhost:8080";

    public String register(String username, String password, String email) {
        ClientRegisterRequest request = new ClientRegisterRequest(username, password, email);

        AuthData authData = makeRequest("POST", "/user", request, AuthData.class);
        return authData.authToken();
    }

    public String login(String username, String password) {
        String authToken = "null";


        return authToken;
    }

    public void logout() {

    }

    public ArrayList<GameData> listGames() {
        ArrayList<GameData> gamesArray = new ArrayList<>();

        return gamesArray;
    }

    public void createGame() {

    }

    public void joinGame() {

    }

    public void observeGame() {

    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) {
        try {
            URL url = (new URI(serverURL + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfUnsuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
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

    private void throwIfUnsuccessful(HttpURLConnection http) throws IOException {
        int status = http.getResponseCode();
        if (!(status / 100 == 2)) {
            try (InputStream responseError = http.getErrorStream()) {
                if (responseError != null) {
                    throw new RuntimeException();
                }
            }

            throw new RuntimeException();
        }
    }
}
