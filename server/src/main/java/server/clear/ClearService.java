package server.clear;

import service.Service;

public class ClearService extends Service {

    public void clear() {
        userDAO.clearUsers();
    }
}
