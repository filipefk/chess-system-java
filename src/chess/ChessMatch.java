package chess;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;

public class ChessMatch {
	
	private Board board;
	private int turn;
	private Color currentPlayer;
	private boolean check;
	private boolean checkMate;
	private ChessPiece enPassantVulnerable;
	private ChessPiece promoted;
	
	private List<ChessPiece> piecesOnTheBoard = new ArrayList<>();
	private List<ChessPiece> capturedPieces = new ArrayList<>();
	
	public ChessMatch() {
		board = new Board(8, 8);
		turn = 1;
		currentPlayer = Color.WHITE;
		check = false;
		InitialSetup();
	}
	
	public int getTurn() {
		return turn;
	}

	public Color getCurrentPlayer() {
		return currentPlayer;
	}
	
	public boolean isCheck() {
		return check;
	}
	
	public boolean isCheckMate() {
		return checkMate;
	}
	
	public ChessPiece getEnPassantVulnerable() {
		return enPassantVulnerable;
	}
	
	public ChessPiece getPromoted() {
		return promoted;
	}
	
	public List<ChessPiece> getCapturedPieces() {
		return capturedPieces;
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
	
	public void performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		validateSourcePosition(sourcePosition);
		validateTargetPosition(sourcePosition, targetPosition);
		Piece capturedPiece = makeMove(sourcePosition.toPosition(), targetPosition.toPosition());
		if (testCheck(currentPlayer)) {
			undoMove(sourcePosition.toPosition(), targetPosition.toPosition(), capturedPiece);
			throw new ChessException("You can't put yourself in check");
		}
		
		ChessPiece movedPiece = (ChessPiece)board.piece(targetPosition.toPosition());
		
		// #specialmove promotion
		promoted = null;
		if (movedPiece instanceof Pawn) {
			if ((movedPiece.getColor() == Color.WHITE && targetPosition.toPosition().getRow() == 0) || (movedPiece.getColor() == Color.BLACK && targetPosition.toPosition().getRow() == 7)) {
				promoted = (ChessPiece)board.piece(targetPosition.toPosition());
				promoted = replacePromotedPiece("Q");
			}
		}
		
		check = testCheck(opponent(currentPlayer));
		if (testCheckMate(opponent(currentPlayer))) {
			checkMate = true;
		} else {
			nextTurn();
		}
		
		// #specialmove en passant
		if (isEnPassantVulnerable(sourcePosition.toPosition(), targetPosition.toPosition())) {
			enPassantVulnerable = movedPiece;
		} else {
			enPassantVulnerable = null;
		}
		
	}
	
	public ChessPiece replacePromotedPiece(String type) {
		if (promoted == null) {
			throw new IllegalStateException("There is no piece to be promoted");
		}
		if (!type.equals("B") && !type.equals("N") && !type.equals("R") & !type.equals("Q")) {
			throw new InvalidParameterException("Invalid type for promotion");
		}
		
		Position pos = promoted.getChessPosition().toPosition();
		Piece p = board.removePiece(pos);
		piecesOnTheBoard.remove(p);
		
		ChessPiece newPiece = newPiece(type, promoted.getColor());
		board.placePiece(newPiece, pos);
		piecesOnTheBoard.add(newPiece);
		
		return newPiece;
	}
	
	private ChessPiece newPiece(String type, Color color) {
		if (type.equals("B")) return new Bishop(board, color);
		if (type.equals("N")) return new Knight(board, color);
		if (type.equals("Q")) return new Queen(board, color);
		return new Rook(board, color);
	}

	private boolean isEnPassantVulnerable(Position source, Position target) {
		ChessPiece piece = (ChessPiece)board.piece(target);
		return piece instanceof Pawn && (target.getRow() == source.getRow() + 2 || target.getRow() == source.getRow() - 2);
	}
	
	private boolean isCastlingMove(Position source, Position target) {
		ChessPiece piece = (ChessPiece)board.piece(source);
		return piece instanceof King && (target.getColumn() == source.getColumn() + 2 || target.getColumn() == source.getColumn() - 2);
	}
	
	private Piece makeMove(Position source, Position target) {
		boolean castlingMove = isCastlingMove(source, target);
		ChessPiece piece = (ChessPiece)board.removePiece(source);
		Piece capturedPiece = board.removePiece(target);
		board.placePiece(piece, target);
		if (capturedPiece != null) {
			piecesOnTheBoard.remove(capturedPiece);
			capturedPieces.add((ChessPiece)capturedPiece);
		}
		piece.increaseMoveCount();
		
		if (castlingMove) {
			Position rookPosition = new Position();
			Position rookTarget = new Position();
			ChessPiece rook = null;
			if (target.getColumn() == source.getColumn() + 2) {
				rookPosition.setValues(source.getRow(), source.getColumn() + 3);
				rookTarget.setValues(source.getRow(), target.getColumn() - 1);
			} else {
				rookPosition.setValues(source.getRow(), 0);
				rookTarget.setValues(source.getRow(), target.getColumn() + 1);
			}
			rook = (ChessPiece)board.removePiece(rookPosition);
			board.placePiece(rook, rookTarget);
			rook.increaseMoveCount();
		}
		
		if (piece instanceof Pawn) {
			if (source.getColumn() != target.getColumn() && capturedPiece == null) {
				Position pawnPosition = new Position();
				if (piece.getColor() == Color.WHITE) {
					pawnPosition.setValues(target.getRow() + 1, target.getColumn());
				} else {
					pawnPosition.setValues(target.getRow() - 1, target.getColumn());
				}
				capturedPiece = board.removePiece(pawnPosition);
				piecesOnTheBoard.remove(capturedPiece);
				capturedPieces.add((ChessPiece)capturedPiece);
			}
		}
		
		return capturedPiece;
	}
	
	private void undoMove(Position source, Position target, Piece capturedPiece) {
		boolean castlingMove = isCastlingMove(target, source);
		ChessPiece piece = (ChessPiece)board.removePiece(target);
		board.placePiece(piece, source);
		if (capturedPiece != null) {
			board.placePiece(capturedPiece, target);
			capturedPieces.remove(capturedPiece);
			piecesOnTheBoard.add((ChessPiece)capturedPiece);
		}
		piece.decreaseMoveCount();
		if (castlingMove) {
			Position rookPosition = new Position();
			Position rookTarget = new Position();
			ChessPiece rook = null;
			if (source.getColumn() == target.getColumn() + 2) {
				rookPosition.setValues(target.getRow(), target.getColumn() + 1);
				rookTarget.setValues(target.getRow(), 0);
			} else {
				rookPosition.setValues(target.getRow(), target.getColumn() - 1);
				rookTarget.setValues(target.getRow(), target.getColumn() + 1);
			}
			rook = (ChessPiece)board.removePiece(rookPosition);
			board.placePiece(rook, rookTarget);
			rook.decreaseMoveCount();
		}
		
		if (piece instanceof Pawn) {
			if (source.getColumn() != target.getColumn() && capturedPiece == enPassantVulnerable) {
				ChessPiece pawn = (ChessPiece)board.removePiece(target);
				Position pawnPosition = new Position();
				if (piece.getColor() == Color.WHITE) {
					pawnPosition.setValues(3, target.getColumn());
				} else {
					pawnPosition.setValues(4, target.getColumn());
				}
				board.placePiece(pawn, pawnPosition);
				capturedPiece = board.removePiece(pawnPosition);
			}
		}
		
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
	
	private Color opponent(Color color) {
		return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}
	
	private ChessPiece king(Color color) {
		List<ChessPiece> kings = piecesOnTheBoard.stream().filter(x -> x instanceof King && x.getColor() == color).collect(Collectors.toList());
		if (kings.size() == 0) {
			throw new IllegalStateException("There is no " + color + " king on the board");
		}
		return kings.get(0);
	}
	
	private boolean testCheck(Color color) {
		Position kingPosition = king(color).getChessPosition().toPosition();
		List<ChessPiece> checks = piecesOnTheBoard.stream().filter(x -> x.getColor() == opponent(color) && x.possibleMoves()[kingPosition.getRow()][kingPosition.getColumn()]).collect(Collectors.toList());
		return checks.size() > 0;
	}
	
	private boolean testCheckMate(Color color) {
		if (!testCheck(color)) {
			return false;
		}
		List<ChessPiece> myPieces = piecesOnTheBoard.stream().filter(x -> x.getColor() == color).collect(Collectors.toList());
		for (ChessPiece p : myPieces) {
			boolean[][] mat = p.possibleMoves();
			for (int r = 0; r < mat.length; r++) {
				for (int c = 0; c < mat.length; c++) {
					if (mat[r][c]) {
						Position source = p.getChessPosition().toPosition();
						Position target = new Position(r, c);
						Piece capturedPiece = makeMove(source, target);
						boolean testCheck = testCheck(color);
						undoMove(source, target, capturedPiece);
						if (!testCheck) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
		piecesOnTheBoard.add(piece);
	}
	
	private void InitialSetup() {
		placeNewPiece('a', 1, new Rook(board, Color.WHITE));
        placeNewPiece('b', 1, new Knight(board, Color.WHITE));
        placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('d', 1, new Queen(board, Color.WHITE));
        placeNewPiece('e', 1, new King(board, Color.WHITE, this));
        placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('g', 1, new Knight(board, Color.WHITE));
        placeNewPiece('h', 1, new Rook(board, Color.WHITE));
        placeNewPiece('a', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('b', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('c', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('d', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('e', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('f', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('g', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('h', 2, new Pawn(board, Color.WHITE, this));

        placeNewPiece('a', 8, new Rook(board, Color.BLACK));
        placeNewPiece('b', 8, new Knight(board, Color.BLACK));
        placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('d', 8, new Queen(board, Color.BLACK));
        placeNewPiece('e', 8, new King(board, Color.BLACK, this));
        placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('g', 8, new Knight(board, Color.BLACK));
        placeNewPiece('h', 8, new Rook(board, Color.BLACK));
        placeNewPiece('a', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('b', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('c', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));
	}

}
