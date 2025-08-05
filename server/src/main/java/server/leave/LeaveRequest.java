package server.leave;

import server.joingame.UpdateGameRequest;

public class LeaveRequest extends UpdateGameRequest {
    private boolean isWhitePlayer;
    private boolean isObserver;

    public boolean getIsWhitePlayer() {
        return isWhitePlayer;
    }
    public boolean getIsObserver() {
        return isObserver;
    }
}
