package requests;

public record ClientLeaveRequest (int gameID, boolean isObserver, boolean isWhitePlayer) {
}
