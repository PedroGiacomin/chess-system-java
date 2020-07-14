package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Knight extends ChessPiece{

	public Knight(Board board, Color color) {
		super(board, color);
	}
	
	//Define se a posição fornecida está disponível
	public boolean canMove(Position position) {
		ChessPiece p = (ChessPiece) getBoard().piece(position);
		return p == null || p.getColor() != getColor();
	}
	
	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
		
		Position p = new Position(0,0);
		
		//1 up 2 left
		p.setValues(position.getRow() - 1, position.getColumn() -  2);
		if(getBoard().positionExists(p) && canMove(p)){
			mat[p.getRow()][p.getColumn()] = true;
		}
		
		//2 up 1 left
		p.setValues(position.getRow() - 2 , position.getColumn() - 1);
		if(getBoard().positionExists(p) && canMove(p)){
		mat[p.getRow()][p.getColumn()] = true;
		}
		
		//1 up 2 right
		p.setValues(position.getRow() - 1, position.getColumn() + 2);
		if(getBoard().positionExists(p) && canMove(p)){
		mat[p.getRow()][p.getColumn()] = true;
		}
		
		//2 up 1 right
		p.setValues(position.getRow() - 2, position.getColumn() + 1);
		if(getBoard().positionExists(p) && canMove(p)){
		mat[p.getRow()][p.getColumn()] = true;
		}
		
		//1 down 2 right
		p.setValues(position.getRow() + 1, position.getColumn() + 2);
		if(getBoard().positionExists(p) && canMove(p)){
		mat[p.getRow()][p.getColumn()] = true;
		}
		
		//2 down 1 right
		p.setValues(position.getRow() + 2, position.getColumn() + 1);
		if(getBoard().positionExists(p) && canMove(p)){
		mat[p.getRow()][p.getColumn()] = true;
		}
		
		//1 down 2 left
		p.setValues(position.getRow() + 1, position.getColumn() - 2);
		if(getBoard().positionExists(p) && canMove(p)){
		mat[p.getRow()][p.getColumn()] = true;
		}
		//2 down 1 left
		p.setValues(position.getRow() + 2, position.getColumn() - 1);
		if(getBoard().positionExists(p) && canMove(p)){
		mat[p.getRow()][p.getColumn()] = true;
		}
		
		return mat;
	}

	@Override 
	public String toString() {
		return "N";
	}
}

