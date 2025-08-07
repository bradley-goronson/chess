package ui;

import chess.*;
import model.GameData;
import server.ResponseException;
import server.ServerFacade;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class GamePlayREPL {
    String serverURL = "http://localhost:8080";
    GameData currentGameState = null;
    ServerFacade facade = new ServerFacade(serverURL);

    public void play(GameData currentGame, boolean whitePerspective, boolean isObserver, String authToken) {
        currentGameState = currentGame;
        printBoard(currentGame, whitePerspective);
        String method;
        boolean gameOver = false;

        try {
            help(isObserver);
            while (!gameOver) {
                System.out.println("What would you like to do? ");
                Scanner scanner = new Scanner(System.in);
                String request = scanner.nextLine();
                String[] requestArray = request.split(" ");
                method = requestArray[0];

                switch (method) {
                    case "help" -> help(isObserver);
                    case "redraw" -> printBoard(currentGame, whitePerspective);
                    case "leave" -> gameOver = leave(isObserver, whitePerspective, authToken);
                    case "move" -> move(requestArray, isObserver, authToken);
                    case "resign" -> gameOver = resign(isObserver, authToken);
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
        facade.leave(currentGameState.gameID(), isObserver, isWhitePlayer, authToken);
        System.out.print(
                EscapeSequences.SET_TEXT_COLOR_GREEN +
                        "Leaving game...\n" +
                        EscapeSequences.SET_TEXT_COLOR_WHITE);
        return true;
    }

    private void move(String[] requestArray, boolean isObserver, String authToken) throws ResponseException {
        if (!isObserver) {
            ChessPosition startPosition = getChessPosition(requestArray[1]);
            ChessPosition endPosition = getChessPosition(requestArray[2]);

            currentGameState = facade.makeMove(currentGameState.gameID(), new ChessMove(startPosition, endPosition, null), authToken);
            printBoard(currentGameState, true);
        } else {
            System.out.print(
                    EscapeSequences.SET_TEXT_COLOR_RED +
                    "Only players can make moves. You are an observer.\n" +
                    EscapeSequences.SET_TEXT_COLOR_WHITE);
        }
    }

    private ChessPosition getChessPosition(String position) {
        ArrayList<Character> letterColumns = new ArrayList<>(Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g'));
        ArrayList<Integer> integerRows = new ArrayList<>(Arrays.asList(8, 7, 6, 5, 4, 3, 2, 1));

        Character givenLetter = position.charAt(0);
        char givenNumberChar = position.charAt(1);
        Integer givenNumber = Character.getNumericValue(givenNumberChar);

        int rowIndex = integerRows.indexOf(givenNumber);
        int columnIndex = letterColumns.indexOf(givenLetter);

        return new ChessPosition(rowIndex + 1, columnIndex + 1);
    }

    private boolean resign(boolean isObserver, String authToken) throws ResponseException {
        if (!isObserver) {
            System.out.print("Are you sure you want to resign? Confirm with \"YES\" cancel with \"NO\"\n");
            Scanner scanner = new Scanner(System.in);
            String response = scanner.nextLine();
            boolean resigned = response.equals("YES");
            if (resigned) {
                System.out.print(
                        EscapeSequences.SET_TEXT_COLOR_GREEN +
                        "You have surrendered. Leaving game...\n" +
                        EscapeSequences.SET_TEXT_COLOR_WHITE);
            }
            return resigned;
        } else {
            System.out.print(
                    EscapeSequences.SET_TEXT_COLOR_RED +
                    "Only players can resign from a game. You are an observer.\n" +
                    EscapeSequences.SET_TEXT_COLOR_WHITE);
            return false;
        }
    }

    private void showMoves(String[] requestArray, String authToken) throws ResponseException {
        System.out.print("You tried to show moves");
    }

    private void printBoard(GameData gameData, boolean whitePerspective) {
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

            ChessPiece[] currentRow = currentChessBoard[7 - i];
            if (!whitePerspective) {
                currentRow = currentChessBoard[i];
            }
            for (int j = 0; j < currentRow.length; j++) {
                if (j % 2 == 0) {
                    output.print(evenColor);
                } else {
                    output.print(oddColor);
                }
                ChessPiece tile = currentRow[j];
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
}
