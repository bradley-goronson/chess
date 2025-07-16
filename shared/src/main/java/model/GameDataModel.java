package model;

import chess.ChessGame;

public record GameDataModel(int gameID,
                            String whiteUsername,
                            String blackUsername,
                            String gameName,
                            ChessGame game)
{}
