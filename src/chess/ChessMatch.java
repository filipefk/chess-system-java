package chess;

import boardgame.Board;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {
	
	private Board board;
	
	public ChessMatch() {
		board = new Board(8, 8);
		InitialSetup();
	}
	
	public ChessPiece[][] getPieces() {
		ChessPiece[][] chessPieces = new ChessPiece[board.getRows()][board.getColumns()];
		for (int r = 0; r < board.getRows(); r++) {
			for (int c = 0; c < board.getColumns(); c++) {
				chessPieces[r][c] = (ChessPiece)board.piece(r, c);
			}
		}
		return chessPieces;
	}
	
	private void InitialSetup() {
		ChessPiece whiteRook = new Rook(board, Color.WHITE);
		ChessPiece blackKing = new King(board, Color.BLACK);
		ChessPiece whiteKing = new King(board, Color.WHITE);
		board.placePiece(whiteRook, new Position(2, 1));
		board.placePiece(blackKing, new Position(0, 4));
		
		Position position = new Position(7, 4);
		if (!board.ThereIsAPiece(position)){
			board.placePiece(whiteKing, position);
		}
	}
}
