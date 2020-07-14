package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece{

	private ChessMatch chessMatch;
	
	public Pawn(Board board, Color color, ChessMatch chessMatch) {
		super(board, color);
		this.chessMatch = chessMatch;
	}

	@Override
	public boolean[][] possibleMoves() {
		
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
		
		Position p = new Position(0,0);
		
		if(getColor() == Color.WHITE) {
			p.setValues(position.getRow() - 1, position.getColumn());
			if(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() - 2, position.getColumn());
			//p2 é a primeira casa à frente do peão
			Position p2 = new Position(position.getRow() - 1, position.getColumn());
			//Testa se a peça já se moveu alguma vez, se não for o caso, pode andar duas casas à frente
			if(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p) && 
					getBoard().positionExists(p2) && !getBoard().thereIsAPiece(p2)  && getMoveCount() == 0) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			//Testa se há peça adversária nas diagonais imediatas do peão
			p.setValues(position.getRow() - 1, position.getColumn() - 1);
			if(getBoard().positionExists(p) && isThereOpponentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() - 1, position.getColumn() + 1);
			if(getBoard().positionExists(p) && isThereOpponentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}			
			
			/*JOGADA ESPECIAL EN PASSANT: peão 1 pode capturar o peão 2 que tiver se movido
			 *2 casas à frente (no seu primeiro movimento) se o 2 se colocar logo ao seu lado.
			 *O peão 1 se move para a sua diagonal imediata 
			 */
			//EN PASSANT white
			if(position.getRow() == 3) {
				Position left = new Position(position.getRow(), position.getColumn() - 1);
				//Verifica se a peça à esquerda é vulnerável ao en passant
				if(getBoard().positionExists(left) && isThereOpponentPiece(left) && getBoard().piece(left) == chessMatch.getEnPassantVulnerable()){
					mat[left.getRow() - 1][left.getColumn()] = true;
				}
				Position right = new Position(position.getRow(), position.getColumn() + 1);
				//Verifica se a peça à direita é vulnerável ao en passant
				if(getBoard().positionExists(right) && isThereOpponentPiece(right) && getBoard().piece(right) == chessMatch.getEnPassantVulnerable()){
					mat[right.getRow() - 1][right.getColumn()] = true;
				}
			}
		}
		else {
			p.setValues(position.getRow() + 1, position.getColumn());
			if(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() + 2, position.getColumn());
			//p2 é a primeira casa à frente do peão
			Position p2 = new Position(position.getRow() + 1, position.getColumn());
			//Testa se a peça já se moveu alguma vez, se não for o caso, pode andar duas casas à frente
			if(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p) && 
					getBoard().positionExists(p2) && !getBoard().thereIsAPiece(p2)  && getMoveCount() == 0) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			//Testa se há peça adversária nas diagonais imediatas do peão
			p.setValues(position.getRow() + 1, position.getColumn() - 1);
			if(getBoard().positionExists(p) && isThereOpponentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() + 1, position.getColumn() + 1);
			if(getBoard().positionExists(p) && isThereOpponentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}	
			//EN PASSANT black
			if(position.getRow() == 4) {
				Position left = new Position(position.getRow(), position.getColumn() - 1);
				//Verifica se a peça à esquerda é vulnerável ao en passant
				if(getBoard().positionExists(left) && isThereOpponentPiece(left) && getBoard().piece(left) == chessMatch.getEnPassantVulnerable()){
					mat[left.getRow() + 1][left.getColumn()] = true;
				}
				Position right = new Position(position.getRow(), position.getColumn() + 1);
				//Verifica se a peça à direita é vulnerável ao en passant
				if(getBoard().positionExists(right) && isThereOpponentPiece(right) && getBoard().piece(right) == chessMatch.getEnPassantVulnerable()){
					mat[right.getRow() + 1][right.getColumn()] = true;
				}
			}
		}
		return mat;
	}
	
	@Override
	public String toString(){
		return "P";
	}
	
	

}
