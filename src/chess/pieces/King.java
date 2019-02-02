package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class King extends ChessPiece {

	private ChessMatch chessMatch;

	public King(Board board, Color color, ChessMatch chessMatch) {
		super(board, color);
		this.chessMatch = chessMatch;
	}

	@Override
	public String toString() {
		return "K";
	}

	private boolean canMove(Position p) {
		return getBoard().positionExists(p) && (!getBoard().thereIsAPiece(p) || isThereOpponentPiece(p));
	}

	private boolean testRookCastling(Position position) {
		ChessPiece p = (ChessPiece) getBoard().piece(position);
		return p != null && p instanceof Rook && p.getColor() == getColor() && p.getMoveCount() == 0;
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getRows()];
		Position p = new Position(0, 0);
		Position r = new Position(0, 0);

		// above
		p.setValues(this.position.getRow() - 1, this.position.getColumn());
		if (canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		// left
		p.setValues(this.position.getRow(), this.position.getColumn() - 1);
		if (canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
			// castling left
			if (getMoveCount() == 0 && !chessMatch.isCheck()) {
				p.setValues(this.position.getRow(), this.position.getColumn() - 2);
				r.setValues(this.position.getRow(), 0);
				if (canMove(p) && testRookCastling(r)) {
					mat[p.getRow()][p.getColumn()] = true;
				}
			}
		}

		// right
		p.setValues(this.position.getRow(), this.position.getColumn() + 1);
		if (canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
			// castling right
			if (getMoveCount() == 0 && !chessMatch.isCheck()) {
				p.setValues(this.position.getRow(), this.position.getColumn() + 2);
				r.setValues(this.position.getRow(), getBoard().getColumns() - 1);
				if (canMove(p) && testRookCastling(r)) {
					mat[p.getRow()][p.getColumn()] = true;
				}
			}
		}

		// below
		p.setValues(this.position.getRow() + 1, this.position.getColumn());
		if (canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		// above left
		p.setValues(this.position.getRow() - 1, this.position.getColumn() - 1);
		if (canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		// above right
		p.setValues(this.position.getRow() - 1, this.position.getColumn() + 1);
		if (canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		// below left
		p.setValues(this.position.getRow() + 1, this.position.getColumn() - 1);
		if (canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		// below right
		p.setValues(this.position.getRow() + 1, this.position.getColumn() + 1);
		if (canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		return mat;
	}

}
