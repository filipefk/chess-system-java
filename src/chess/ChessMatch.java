package chess;

import boardgame.Board;
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
	
	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
	}
	
	private void InitialSetup() {
		ChessPiece whiteRook = new Rook(board, Color.WHITE);
		ChessPiece blackKing = new King(board, Color.BLACK);
		ChessPiece whiteKing = new King(board, Color.WHITE);
		
		placeNewPiece('b', 6, whiteRook);
		placeNewPiece('e', 8, blackKing);
		placeNewPiece('e', 1, whiteKing);
		
	}
}
