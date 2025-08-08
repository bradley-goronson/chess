package ui;

import chess.*;
import model.GameData;
import requests.ResponseException;
import requests.ServerFacade;
import websocket.NotificationHandler;
import facade.WebSocketFacade;
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
                    case "redraw" -> printBoard(currentGameState, isWhitePerspective, null, null);
                    case "leave" -> leftGame = leave(isObserver, isWhitePerspective, authToken);
                    case "move" -> move(requestArray, isObserver, authToken);
                    case "resign" -> resign(isObserver, authToken);
                    case "show" -> showMoves(requestArray);
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
        //facade.leave(currentGameState.gameID(), isObserver, isWhitePlayer, authToken);
        System.out.print(
                EscapeSequences.SET_TEXT_COLOR_GREEN +
                        "Leaving game...\n" +
                        EscapeSequences.SET_TEXT_COLOR_WHITE);
        return true;
    }

    private void move(String[] requestArray, boolean isObserver, String authToken) throws ResponseException {
        if (!isObserver) {
            try {
                ChessPosition startPosition = getChessPosition(requestArray[1]);
                ChessPiece movingPiece = currentGameState.game().getBoard().getPiece(startPosition);
                if (movingPiece == null) {
                    System.out.print(
                            EscapeSequences.SET_TEXT_COLOR_RED +
                                    "Error: You need a piece to move. Your start position wasn't an empty tile" +
                                    EscapeSequences.SET_TEXT_COLOR_WHITE);
                    return;
                }

                ChessGame.TeamColor playerColor;
                if (isWhitePerspective) {
                    playerColor = ChessGame.TeamColor.WHITE;
                } else {
                    playerColor = ChessGame.TeamColor.BLACK;
                }

                if (currentGameState.game().getTeamTurn() != playerColor) {
                    System.out.print(
                            EscapeSequences.SET_TEXT_COLOR_RED +
                                    "Error: You can't move when it isn't your turn!" +
                                    EscapeSequences.SET_TEXT_COLOR_WHITE);
                    return;
                }

                if (movingPiece.getTeamColor() != currentGameState.game().getTeamTurn()) {
                    System.out.print(
                            EscapeSequences.SET_TEXT_COLOR_RED +
                                    "Error: You can't move an opponent's piece!" +
                                    EscapeSequences.SET_TEXT_COLOR_WHITE);
                    return;
                }
                ChessPosition endPosition = getChessPosition(requestArray[2]);
                ChessPiece.PieceType promotionPiece = null;
                if (movingPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
                    if (movingPiece.getTeamColor() == ChessGame.TeamColor.WHITE && endPosition.getRow() == 8) {
                        promotionPiece = checkForPromotion(startPosition, endPosition);
                    }
                    if (movingPiece.getTeamColor() == ChessGame.TeamColor.BLACK && endPosition.getRow() == 1) {
                        promotionPiece = checkForPromotion(startPosition, endPosition);
                    }
                }
                System.out.println(promotionPiece);
                ChessMove move = new ChessMove(startPosition, endPosition, promotionPiece);
                ws.move(move, authToken, requestArray[1], requestArray[2]);
            } catch (IndexOutOfBoundsException e) {
                System.out.print(
                        EscapeSequences.SET_TEXT_COLOR_RED +
                                "Invalid input. Make sure to type \"move <firstPosition> <secondPosition>\" " +
                                "with no spaces between the letter and number of the positions.\n" +
                                EscapeSequences.SET_TEXT_COLOR_WHITE);
            }
        } else {
            System.out.print(
                    EscapeSequences.SET_TEXT_COLOR_RED +
                    "Only players can make moves. You are an observer.\n" +
                    EscapeSequences.SET_TEXT_COLOR_WHITE);
        }
    }

    private ChessPosition getChessPosition(String position) {
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
            System.out.print("Are you sure you want to resign? Confirm with \"YES\" cancel with any other input\n");
            Scanner scanner = new Scanner(System.in);
            String response = scanner.nextLine();
            boolean resigning = response.equals("YES");
            if (resigning) {
                ws.resign(authToken);
                resigned = true;
                gameOver = true;
                System.out.print(
                        EscapeSequences.SET_TEXT_COLOR_YELLOW +
                                "You have surrendered. Game over.\n" +
                                EscapeSequences.SET_TEXT_COLOR_WHITE);
            }
        } else {
            System.out.print(
                    EscapeSequences.SET_TEXT_COLOR_RED +
                    "Only players can resign from a game. You are an observer.\n" +
                    EscapeSequences.SET_TEXT_COLOR_WHITE);
        }
    }

    private void showMoves(String[] requestArray) throws ResponseException {
        ChessPosition startPosition = getChessPosition(requestArray[1]);
        //MovesContainer validMovesContainer = facade.showMoves(gameID, startPosition, authToken);
        Collection<ChessMove> validMoves = currentGameState.game().validMoves(startPosition);
        ArrayList<ChessPosition> endPositions = new ArrayList<>();
        if (validMoves != null) {
            for (ChessMove move : validMoves) {
                endPositions.add(move.getEndPosition());
            }
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
                    checkForHighlighting(output, startPosition, currentRowIndex, currentColIndex, endPositions);
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

    private void checkForHighlighting(PrintStream output, ChessPosition start, int rowIndex, int colIndex, ArrayList<ChessPosition> ends) {
        if (start.getRow() - 1 == rowIndex && start.getColumn() - 1 == colIndex) {
            output.print(EscapeSequences.SET_BG_COLOR_MAGENTA);
        } else {
            for (ChessPosition position : ends) {
                if (position.getRow() - 1 == rowIndex && position.getColumn() - 1 == colIndex) {
                    output.print(EscapeSequences.SET_BG_COLOR_GREEN);
                }
            }
        }
    }

    private ChessPiece.PieceType checkForPromotion(ChessPosition startPosition, ChessPosition endPosition) {
        Collection<ChessMove> validMoves = currentGameState.game().validMoves(startPosition);
        ChessPiece.PieceType returnPiece = null;

        boolean valid = false;
        for (ChessMove move : validMoves) {
            if (move.getEndPosition().getColumn() == endPosition.getColumn() && move.getEndPosition().getRow() == endPosition.getRow()) {
                valid = true;
                break;
            }
        }

        if (valid) {
            boolean done = false;
            while (!done) {
                System.out.println("You're moving a pawn to a promotion spot! What piece do you want to promote to?\n");
                System.out.println("Queen: type \"QUEEN\"\n" +
                        "Bishop: type \"BISHOP\"\n" +
                        "Knight: type \"KNIGHT\"\n" +
                        "Rook: type \"ROOK\"\n");
                Scanner scanner = new Scanner(System.in);
                String answer = scanner.nextLine();
                switch (answer) {
                    case "QUEEN" -> returnPiece = ChessPiece.PieceType.QUEEN;
                    case "BISHOP" -> returnPiece = ChessPiece.PieceType.BISHOP;
                    case "KNIGHT" -> returnPiece = ChessPiece.PieceType.KNIGHT;
                    case "ROOK" -> returnPiece = ChessPiece.PieceType.ROOK;
                }
                if (returnPiece != null) {
                    done = true;
                }
            }
        }
        return returnPiece;
    }
}
