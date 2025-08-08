package ui;

import chess.*;
import model.GameData;
import model.MovesContainer;
import server.ResponseException;
import server.ServerFacade;
import websocket.NotificationHandler;
import Facade.WebSocketFacade;
import websocket.messages.ServerMessage;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

public class GamePlayREPL implements NotificationHandler {
    String serverURL = "http://localhost:8080";
    GameData currentGameState = null;
    Integer gameID;
    boolean isWhitePerspective;
    ServerFacade facade = new ServerFacade(serverURL);
    WebSocketFacade ws;
    boolean gameOver = false;
    boolean resigned = false;
    boolean leftGame = false;

    public GamePlayREPL(Integer newGameID) {
        this.gameID = newGameID;
    }

    public void play(GameData currentGame, boolean whitePerspective, boolean isObserver, String authToken) throws ResponseException {
        ws = new WebSocketFacade(serverURL, this, gameID, authToken);
        isWhitePerspective = whitePerspective;
        currentGameState = currentGame;
        String method;

        try {
            help(isObserver);
            while (!leftGame) {
                System.out.println("What would you like to do? ");
                Scanner scanner = new Scanner(System.in);
                String request = scanner.nextLine();
                String[] requestArray = request.split(" ");
                method = requestArray[0];

                switch (method) {
                    case "help" -> help(isObserver);
                    case "redraw" -> printBoard(currentGame, whitePerspective, null, null);
                    case "leave" -> leftGame = leave(isObserver, whitePerspective, authToken);
                    case "move" -> move(requestArray, isObserver, whitePerspective, authToken);
                    case "resign" -> resign(isObserver, authToken);
                    case "show" -> showMoves(requestArray, authToken);
                    default -> System.out.println(
                            EscapeSequences.SET_TEXT_COLOR_RED +
                                    "error: invalid command given - use \"help\" for a list of available commands and usages" +
                                    EscapeSequences.SET_TEXT_COLOR_WHITE);
                }
            }
        } catch (ResponseException e) {
            System.out.println(
                    EscapeSequences.SET_TEXT_COLOR_RED +
                            e.getMessage() +
                            EscapeSequences.SET_TEXT_COLOR_WHITE);
        }
    }

    private void help(boolean observer) {
        System.out.println(
                EscapeSequences.SET_TEXT_UNDERLINE + EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_WHITE +
                        "Available commands:" +
                        EscapeSequences.RESET_TEXT_UNDERLINE + EscapeSequences.RESET_TEXT_BOLD_FAINT);

        System.out.println(
                EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.SET_TEXT_BOLD +
                        "help" +
                        EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.RESET_TEXT_BOLD_FAINT +
                        ": display a list of available commands");

        System.out.println(
                EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.SET_TEXT_BOLD +
                        "redraw" +
                        EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.RESET_TEXT_BOLD_FAINT +
                        ": redraw chess board");

        System.out.println(
                EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.SET_TEXT_BOLD +
                        "leave" +
                        EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.RESET_TEXT_BOLD_FAINT +
                        ": leave the game");

        if (!observer) {
            System.out.println(
                    EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.SET_TEXT_BOLD +
                            "move" +
                            EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.RESET_TEXT_BOLD_FAINT +
                            ": display a list of all active chess games"
            );
            System.out.println("   usage: move <startPosition> <endPosition>");

            System.out.println(
                    EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.SET_TEXT_BOLD +
                            "resign" +
                            EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.RESET_TEXT_BOLD_FAINT +
                            ": join the indicated chess game playing as the indicated color"
            );
        }

        System.out.println(
                EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.SET_TEXT_BOLD +
                        "show" +
                        EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.RESET_TEXT_BOLD_FAINT +
                        ": highlight possible moves from the given position"
        );
        System.out.println("   usage: show <position>");
    }

    private boolean leave(boolean isObserver, boolean isWhitePlayer, String authToken) throws ResponseException {
        ws.leave(authToken);
        facade.leave(currentGameState.gameID(), isObserver, isWhitePlayer, authToken);
        System.out.print(
                EscapeSequences.SET_TEXT_COLOR_GREEN +
                        "Leaving game...\n" +
                        EscapeSequences.SET_TEXT_COLOR_WHITE);
        return true;
    }

    private void move(String[] requestArray, boolean isObserver, boolean isWhitePerspective, String authToken) throws ResponseException {
        if (!isObserver) {
            ChessPosition startPosition = getChessPosition(requestArray[1], isWhitePerspective);
            ChessPosition endPosition = getChessPosition(requestArray[2], isWhitePerspective);
            ChessMove move = new ChessMove(startPosition, endPosition, null);
            ws.move(move, authToken, requestArray[1], requestArray[2]);
        } else {
            System.out.print(
                    EscapeSequences.SET_TEXT_COLOR_RED +
                    "Only players can make moves. You are an observer.\n" +
                    EscapeSequences.SET_TEXT_COLOR_WHITE);
        }
    }

    private ChessPosition getChessPosition(String position, boolean isWhitePerspective) {
        ArrayList<Character> letterColumns = new ArrayList<>(Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'));
        ArrayList<Integer> integerRows = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));
        Character givenLetter = position.charAt(0);
        char givenNumberChar = position.charAt(1);
        Integer givenNumber = Character.getNumericValue(givenNumberChar);

        int rowIndex = integerRows.indexOf(givenNumber);
        int columnIndex = letterColumns.indexOf(givenLetter);

        return new ChessPosition(rowIndex + 1, columnIndex + 1);
    }

    private void resign(boolean isObserver, String authToken) throws ResponseException {
        if (!isObserver) {
            if (gameOver) {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED +
                        "Error: The game is already over. You can't resign.\n" +
                        EscapeSequences.SET_TEXT_COLOR_WHITE);
                return;
            }
            System.out.print("Are you sure you want to resign? Confirm with \"YES\" cancel with \"NO\"\n");
            Scanner scanner = new Scanner(System.in);
            String response = scanner.nextLine();
            boolean resigning = response.equals("YES");
            if (resigning) {
                ws.resign(authToken);
                resigned = true;
                gameOver = true;
            } else {
                return;
            }
            System.out.print(
                        EscapeSequences.SET_TEXT_COLOR_GREEN +
                        "You have surrendered. Game over.\n" +
                        EscapeSequences.SET_TEXT_COLOR_WHITE);
        } else {
            System.out.print(
                    EscapeSequences.SET_TEXT_COLOR_RED +
                    "Only players can resign from a game. You are an observer.\n" +
                    EscapeSequences.SET_TEXT_COLOR_WHITE);
        }
    }

    private void showMoves(String[] requestArray, String authToken) throws ResponseException {
        ChessPosition startPosition = getChessPosition(requestArray[1], isWhitePerspective);
        MovesContainer validMovesContainer = facade.showMoves(gameID, startPosition, authToken);
        Collection<ChessMove> validMoves = validMovesContainer.validMoves();
        ArrayList<ChessPosition> endPositions = new ArrayList<>();
        for (ChessMove move : validMoves) {
            endPositions.add(move.getEndPosition());
        }
        printBoard(currentGameState, isWhitePerspective, startPosition, endPositions);

        if (currentGameState.game().getBoard().getPiece(startPosition) == null) {
            System.out.print(
                    EscapeSequences.SET_TEXT_COLOR_RED +
                            "You need to select a tile that has a piece on it to show moves.\n" +
                            EscapeSequences.SET_TEXT_COLOR_WHITE);
        }
    }

    private void printBoard(GameData gameData, boolean whitePerspective, ChessPosition startPosition, ArrayList<ChessPosition> endPositions) {
        boolean showingMoves = false;
        if (endPositions != null) {
            showingMoves = true;
        }
        PrintStream output = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        String[] whiteEdge = {" ", "a", "b", "c", "d", "e", "f", "g", "h", " "};
        String[] blackEdge = {" ", "h", "g", "f", "e", "d", "c", "b", "a", " "};
        if (whitePerspective) {
            printEdge(output, whiteEdge);
        } else {
            printEdge(output, blackEdge);
        }
        ChessPiece[][] currentChessBoard = gameData.game().getBoard().board;
        for (int i = 0; i < currentChessBoard.length; i++) {
            output.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
            output.print(EscapeSequences.SET_TEXT_COLOR_YELLOW);
            if (whitePerspective) {
                output.print(" " + (8 - i) + " ");
            } else {
                output.print(" " + (i + 1) + " ");
            }
            String evenColor = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
            String oddColor = EscapeSequences.SET_BG_COLOR_DARK_GREEN;
            if (i % 2 != 0) {
                evenColor = EscapeSequences.SET_BG_COLOR_DARK_GREEN;
                oddColor = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
            }

            int currentRowIndex = 7 - i;
            if (!whitePerspective) {
                currentRowIndex = i;
            }
            ChessPiece[] currentRow = currentChessBoard[currentRowIndex];

            for (int j = 0; j < currentRow.length; j++) {
                if (j % 2 == 0) {
                    output.print(evenColor);
                } else {
                    output.print(oddColor);
                }
                int currentColIndex = j;
                if (!whitePerspective) {
                    currentColIndex = 7 - j;
                }
                ChessPiece tile = currentRow[currentColIndex];

                if (showingMoves) {
                    if (startPosition.getRow() - 1 == currentRowIndex && startPosition.getColumn() - 1 == currentColIndex) {
                        output.print(EscapeSequences.SET_BG_COLOR_YELLOW);
                    } else {
                        for (ChessPosition position : endPositions) {
                            if (position.getRow() - 1 == currentRowIndex && position.getColumn() - 1 == currentColIndex) {
                                output.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN);
                            }
                        }
                    }
                }

                output.print(" ");
                if (tile == null) {
                    output.print(" ");
                } else {
                    if (tile.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
                        output.print(EscapeSequences.SET_TEXT_FAINT + EscapeSequences.SET_TEXT_COLOR_WHITE);
                    } else {
                        output.print(EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_BLACK);
                    }
                    switch (tile.getPieceType()) {
                        case KING -> output.print("K");
                        case QUEEN -> output.print("Q");
                        case BISHOP -> output.print("B");
                        case KNIGHT -> output.print("N");
                        case ROOK -> output.print("R");
                        case PAWN -> output.print("P");
                    }
                }
                output.print(" ");
            }
            output.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
            output.print(EscapeSequences.SET_TEXT_COLOR_YELLOW);
            if (whitePerspective) {
                output.print(" " + (8 - i) + " ");
            } else {
                output.print(" " + (i + 1) + " ");
            }            output.print(EscapeSequences.SET_BG_COLOR_BLACK);
            output.print("\n");
        }
        if (whitePerspective) {
            printEdge(output, whiteEdge);
        } else {
            printEdge(output, blackEdge);
        }
        System.out.println(EscapeSequences.RESET_TEXT_BOLD_FAINT + EscapeSequences.SET_TEXT_COLOR_WHITE + "\n");
    }

    private void printEdge(PrintStream output, String[] row) {
        output.print(EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_BG_COLOR_DARK_GREY);
        for (String letter : row) {
            output.print(" ");
            output.print(EscapeSequences.SET_TEXT_COLOR_YELLOW);
            output.print(letter);
            output.print(" ");
        }
        output.print(EscapeSequences.SET_BG_COLOR_BLACK);
        output.print("\n");
    }

    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> loadGame(message.getGame());
            case NOTIFICATION -> displayNotification(message.getMessage());
            case ERROR -> displayError(message.getErrorMessage());
        }
    }

    private void loadGame(GameData newGameState) {
        currentGameState = newGameState;
        printBoard(currentGameState, isWhitePerspective, null, null);
    }

    private void displayNotification(String notification) {
        System.out.println(
                EscapeSequences.SET_TEXT_COLOR_BLUE +
                notification +
                EscapeSequences.SET_TEXT_COLOR_WHITE
        );
    }

    private void displayError(String errorMessage) {
        System.out.println(
                EscapeSequences.SET_TEXT_COLOR_BLUE +
                errorMessage +
                EscapeSequences.SET_TEXT_COLOR_WHITE
        );
    }
}
