package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece {

	public Pawn(Board board, Color color) {
		super(board, color);
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
		}

		return mat;
	}
	
}
