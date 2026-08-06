package service;

import chess.ChessGame;
import model.GameData;

public record MoveResult(GameData game, ChessGame.TeamColor moverColor) {}
