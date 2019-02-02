package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece {

	private ChessMatch chessMatch;

	public Pawn(Board board, Color color, ChessMatch chessMatch) {
		super(board, color);
		this.chessMatch = chessMatch;
	}

	@Override
	public String toString() {
		return "P";
	}

	private boolean canMove(Position p) {
		return getBoard().positionExists(p) && !getBoard().thereIsAPiece(p);
	}

	private boolean canCapture(Position p) {
		return getBoard().positionExists(p) && isThereOpponentPiece(p);
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getRows()];
		Position p = new Position(0, 0);

		if (getColor() == Color.WHITE) {
			// above
			p.setValues(this.position.getRow() - 1, this.position.getColumn());
			if (canMove(p)) {
				mat[p.getRow()][p.getColumn()] = true;
				// above first move
				if (getMoveCount() == 0) {
					p.setValues(this.position.getRow() - 2, this.position.getColumn());
					if (canMove(p)) {
						mat[p.getRow()][p.getColumn()] = true;
					}
				}
			}

			// above left
			p.setValues(this.position.getRow() - 1, this.position.getColumn() - 1);
			if (canCapture(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}

			// above right
			p.setValues(this.position.getRow() - 1, this.position.getColumn() + 1);
			if (canCapture(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}

			// #specialmove en passant
			if (this.position.getRow() == 3 && chessMatch.getEnPassantVulnerable() != null) {
				Position left = new Position(position.getRow(), position.getColumn() - 1);
				if (getBoard().positionExists(left) && isThereOpponentPiece(left) && getBoard().piece(left) == chessMatch.getEnPassantVulnerable()) {
					mat[left.getRow() - 1][left.getColumn()] = true;
				}
				Position right = new Position(position.getRow(), position.getColumn() + 1);
				if (getBoard().positionExists(right) && isThereOpponentPiece(right) && getBoard().piece(right) == chessMatch.getEnPassantVulnerable()) {
					mat[right.getRow() - 1][right.getColumn()] = true;
				}
			}


		} else {
			// below
			p.setValues(this.position.getRow() + 1, this.position.getColumn());
			if (canMove(p)) {
				mat[p.getRow()][p.getColumn()] = true;
				// below first move
				if (getMoveCount() == 0) {
					p.setValues(this.position.getRow() + 2, this.position.getColumn());
					if (canMove(p)) {
						mat[p.getRow()][p.getColumn()] = true;
					}
				}
			}

			// below left
			p.setValues(this.position.getRow() + 1, this.position.getColumn() - 1);
			if (canCapture(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}

			// below right
			p.setValues(this.position.getRow() + 1, this.position.getColumn() + 1);
			if (canCapture(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			
			// #specialmove en passant
			if (this.position.getRow() == 4 && chessMatch.getEnPassantVulnerable() != null) {
				Position left = new Position(position.getRow(), position.getColumn() - 1);
				if (getBoard().positionExists(left) && isThereOpponentPiece(left) && getBoard().piece(left) == chessMatch.getEnPassantVulnerable()) {
					mat[left.getRow() + 1][left.getColumn()] = true;
				}
				Position right = new Position(position.getRow(), position.getColumn() + 1);
				if (getBoard().positionExists(right) && isThereOpponentPiece(right) && getBoard().piece(right) == chessMatch.getEnPassantVulnerable()) {
					mat[right.getRow() + 1][right.getColumn()] = true;
				}
			}
		}

		return mat;
	}

}
