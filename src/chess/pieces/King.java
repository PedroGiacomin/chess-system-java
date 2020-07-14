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

	//Define se a posição fornecida está disponível
	public boolean canMove(Position position) {
		ChessPiece p = (ChessPiece) getBoard().piece(position);
		return p == null || p.getColor() != getColor();
	}
	
	/*JOGADA ESPECIAL ROQUE: o rei e a torre trocam de lugar se nenhum dos dois
	 *tiver se movido na partida e se não estiver em xeque.*/
	private boolean testRookCastling(Position position){
		//teste se tem torre e se está apta para roque
		ChessPiece p = (ChessPiece)getBoard().piece(position);
		return p!= null && p instanceof Rook && p.getColor() == getColor() && p.getMoveCount() == 0;
	}
	
	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
		
		Position p = new Position(0,0);
		
		//above
		p.setValues(position.getRow() - 1, position.getColumn());
		if(getBoard().positionExists(p) && canMove(p)){
			mat[p.getRow()][p.getColumn()] = true;
		}
		
		//below
		p.setValues(position.getRow() + 1, position.getColumn());
		if(getBoard().positionExists(p) && canMove(p)){
		mat[p.getRow()][p.getColumn()] = true;
		}
		
		//right
		p.setValues(position.getRow(), position.getColumn() + 1);
		if(getBoard().positionExists(p) && canMove(p)){
		mat[p.getRow()][p.getColumn()] = true;
		}
		
		//left
		p.setValues(position.getRow(), position.getColumn() - 1);
		if(getBoard().positionExists(p) && canMove(p)){
		mat[p.getRow()][p.getColumn()] = true;
		}
		
		//above and left
		p.setValues(position.getRow() - 1, position.getColumn() - 1);
		if(getBoard().positionExists(p) && canMove(p)){
		mat[p.getRow()][p.getColumn()] = true;
		}
		//above and right
		p.setValues(position.getRow() - 1, position.getColumn() + 1);
		if(getBoard().positionExists(p) && canMove(p)){
		mat[p.getRow()][p.getColumn()] = true;
		}
		//below and left
		p.setValues(position.getRow() + 1, position.getColumn() - 1);
		if(getBoard().positionExists(p) && canMove(p)){
		mat[p.getRow()][p.getColumn()] = true;
		}
		//below and right
		p.setValues(position.getRow() + 1, position.getColumn() + 1);
		if(getBoard().positionExists(p) && canMove(p)){
		mat[p.getRow()][p.getColumn()] = true;
		}
		//ROQUE
		if(getMoveCount() == 0 && !chessMatch.getCheck()) {
			//roque pequeno
			Position posT1 = new Position(position.getRow(), position.getColumn() + 3);
			if (testRookCastling(posT1)) {
				Position p1 = new Position(position.getRow(), position.getColumn() + 1);
				Position p2 = new Position(position.getRow(), position.getColumn() + 2);
				//Testa se não há peças entre o rei e a torre
				if(getBoard().piece(p1) == null && getBoard().piece(p2) == null) {
					//Então permite o movimento para 2 casas à direita
					mat[position.getRow()][position.getColumn() + 2] = true;
				}
			}
			//roque grande 
			Position posT2 = new Position(position.getRow(), position.getColumn() - 4);
			if (testRookCastling(posT2)) {
				Position p1 = new Position(position.getRow(), position.getColumn() - 1);
				Position p2 = new Position(position.getRow(), position.getColumn() - 2);
				Position p3 = new Position(position.getRow(), position.getColumn() - 3);
				//Testa se não há peças entre o rei e a torre
				if(getBoard().piece(p1) == null && getBoard().piece(p2) == null && getBoard().piece(p3) == null) {
					//Então permite o movimento para 2 casas à esquerda
					mat[position.getRow()][position.getColumn() - 2] = true;
				}
			}
			
		}
		return mat;
	}	
}
