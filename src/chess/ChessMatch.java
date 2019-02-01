package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {
	
	private Board board;
	private int turn;
	private Color currentPlayer;
	
	
	public ChessMatch() {
		board = new Board(8, 8);
		turn = 1;
		currentPlayer = Color.WHITE;
		InitialSetup();
	}
	
	public int getTurn() {
		return turn;
	}

	public Color getCurrentPlayer() {
		return currentPlayer;
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
	
	public boolean[][] possibleMoves(ChessPosition sourcePosition) {
		validateSourcePosition(sourcePosition);
		return board.piece(sourcePosition.toPosition()).possibleMoves();
	}
	
	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		validateSourcePosition(sourcePosition);
		validateTargetPosition(sourcePosition, targetPosition);
		Piece capturedPiece = makeMove(sourcePosition.toPosition(), targetPosition.toPosition());
		nextTurn();
		return (ChessPiece)capturedPiece;
	}

	private Piece makeMove(Position source, Position target) {
		Piece piece = board.removePiece(source);
		Piece capturedPiece = board.removePiece(target);
		board.placePiece(piece, target);
		return capturedPiece;
	}

	private void validateSourcePosition(ChessPosition source) {
		if (!board.thereIsAPiece(source.toPosition())) {
			throw new ChessException("There is no peace on position " + source.getColumn() + source.getRow());
		}
		if (currentPlayer != ((ChessPiece)board.piece(source.toPosition())).getColor()) {
			throw new ChessException("The chosen piece is not yours");
		}
		if (!board.piece(source.toPosition()).isThereAnyPossibleMove()) {
			throw new ChessException("There is no possible movies for the chosen piece");
		}
	}
	
	private void validateTargetPosition(ChessPosition sourcePosition, ChessPosition targetPosition) {
		if (!board.piece(sourcePosition.toPosition()).possibleMove(targetPosition.toPosition())) {
			throw new ChessException("The chosen piece can't move to target position");
		}
	}
	
	private void nextTurn() {
		turn++;
		currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}

	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
	}
	
	private void InitialSetup() {
		placeNewPiece('c', 1, new Rook(board, Color.WHITE));
        placeNewPiece('c', 2, new Rook(board, Color.WHITE));
        placeNewPiece('d', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 1, new Rook(board, Color.WHITE));
        placeNewPiece('d', 1, new King(board, Color.WHITE));

        placeNewPiece('c', 7, new Rook(board, Color.BLACK));
        placeNewPiece('c', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 8, new King(board, Color.BLACK));
	}
}
