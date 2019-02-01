package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class King extends ChessPiece {

	public King(Board board, Color color) {
		super(board, color);
	}

	@Override
	public String toString() {
		return "K";
	}
	
	private boolean canMove(Position p) {
		return getBoard().positionExists(p) && (!getBoard().thereIsAPiece(p) || isThereOpponentPiece(p));
	}
	
	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getRows()];
		Position p = new Position(0, 0);

		// above
		p.setValues(this.position.getRow() - 1, this.position.getColumn());
		if (canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		// left
		p.setValues(this.position.getRow(), this.position.getColumn() - 1);
		if (canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		// right
		p.setValues(this.position.getRow(), this.position.getColumn() + 1);
		if (canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
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
