package chess;

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

	private int turn;
	private Color currentPlayer;
	private Board board;
	private boolean check;
	private boolean checkMate;
	
	/*Essas s�o duas listas para manter o controle das pe�as que est�o no tabuleiro ou
	 fora dele (capturadas). As pe�as s�o adicionadas �s listas nas situa��es correspondentes,
	 no placeNewPiece e no makeMove*/
	 
	private List<Piece> piecesOnTheBoard = new ArrayList<>();
	private List<Piece> capturedPieces = new ArrayList<>();
	
	public ChessMatch() {
		board = new Board(8, 8);
		turn = 1;
		currentPlayer = Color.WHITE;
		initialSetup();
	}
	
	public int getTurn() {
		return turn;
	}
	
	public Color getCurrentPlayer() {
		return currentPlayer;
	}
	
	public boolean getCheck() {
		return check;
	}
	
	public boolean getCheckMate() {
		return checkMate;
	}
		
	//Retorna uma matriz de pe�as de xadrez
	public ChessPiece[][] getPieces(){
		ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
		for(int i = 0; i < board.getRows(); i++) {
			for(int j = 0; j < board.getColumns(); j++) {
				mat[i][j] = (ChessPiece) board.piece(i, j);
			}
		}
		return mat;
	}
	
	//Retorna matriz de boolean com movimentos poss�veis, para colorir eles
	public boolean[][] possibleMoves(ChessPosition sourcePosition){
		Position position = sourcePosition.toPosition();
		validateSourcePosition(position);
		return board.piece(position).possibleMoves();
	}
	
	//Retorna a pe�a que estava na posi��o target, ap�s realizar o movimento
	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		validateSourcePosition(source);
		validateTargetPosition(source, target);
		Piece capturedPiece = makeMove(source, target);
		
		//Testa se o jogador atual se p�s em xeque, desfazendo o movimento se for o caso
		if(testCheck(currentPlayer)) {
			undoMove(source, target, capturedPiece);
			throw new ChessException("You can't put yourself in check!");
		}
		
		//Se o oponente ficar em cheque, atualiza o atributo check para true
		check = (testCheck(opponent(currentPlayer))) ? true : false;
		
		//Antes de mudar o turno, verifica se houve checkMate
		if (testCheckMate(opponent(currentPlayer))) {
			checkMate = true;
		}
		else {
			nextTurn();
		}
		return (ChessPiece) capturedPiece;
	}
	
	/*
	 Retira a pe�a da posi��o de origem e guarda em p, remove a pe�a em traget 
	 e guarda em capturedPiece, posiciona p em target
	 */
	private Piece makeMove(Position source, Position target) {
		ChessPiece p = (ChessPiece) board.removePiece(source);
		p.increaseMoveCount();
		Piece capturedPiece = board.removePiece(target);
		board.placePiece(p, target);
		
		if(capturedPiece != null) {
			piecesOnTheBoard.remove(capturedPiece);
			capturedPieces.add(capturedPiece);
		}		
		return capturedPiece;
	}
	
	//Desfaz o movimento, o inverso do makeMove (usado na l�gica de xeque)
	private void undoMove(Position source, Position target, Piece capturedPiece) {
		ChessPiece p = (ChessPiece)board.removePiece(target);
		p.decreaseMoveCount();
		board.placePiece(p, source);
		if(capturedPiece != null) {
			board.placePiece(capturedPiece, target);
			capturedPieces.remove(capturedPiece);
			piecesOnTheBoard.add(capturedPiece);
		}
	}
	
	//Testa as condi��es para que a pe�a possa ser movida
	private void validateSourcePosition(Position position) {
		if(!board.thereIsAPiece(position)) {
			throw new ChessException("There is no piece on source position");
		}
		if(currentPlayer != ((ChessPiece)board.piece(position)).getColor()) {
			//Verifica se a pe�a selecionada � da cor do jogador atual
			throw new ChessException("The chosen piece is not yours");
		}
		if(!board.piece(position).isThereAnyPossibleMove( )) {
			throw new ChessException("There is no possible moves");
		}
	}
	
	private void validateTargetPosition(Position source, Position target) {
		if (!board.piece(source).possibleMove(target)) {
			throw new ChessException("The chosen piece can't move to target position");
		}
	}
	
	//Troca de turno com uma express�o tern�ria
	private void nextTurn() {
		turn++;
		currentPlayer = (currentPlayer == Color.WHITE ? Color.BLACK : Color.WHITE); 
	}
	
	//Retorna a cor do oponente
	public Color opponent(Color color) {
		return (color == Color.WHITE ? Color.BLACK : Color.WHITE);
	}
	
	//Retorna o rei da cor fornecida como par�metro
	private ChessPiece king(Color color) {
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		for(Piece p : list) {
			if (p instanceof King) {
				return (ChessPiece) p;
			}
		}
		throw new IllegalStateException("There is no " + color + "king on the board");
	}
	
	private boolean testCheck(Color color) {
		//D� a posi��o do rei
		Position kingPosition = king(color).getChessPosition().toPosition();
		//D� uma lista de pe�as advers�rias que est�o no tabuleiro (a partir de pecesOnTheBoard) 
		List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == opponent(color)).collect(Collectors.toList());
		/*Testa a matriz de boolean de movimentos poss�veis de cada pe�a da lista acima 
		  para tentar achar alguma posi��o da matriz que corresponda � posi��o do rei*/
		for(Piece p : opponentPieces) {
			boolean[][] mat = p.possibleMoves(); 
			if (mat[kingPosition.getRow()][kingPosition.getColumn()]) {
				return true;
			}
		}
		return false;
	}
	
	private boolean testCheckMate(Color color) {
		if(!testCheck(color)) {
			return false;
		}
		//D� uma lista de pe�as da cor do jogador atual que est�o no tabuleiro
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		for (Piece p : list) {
			//D� uma matriz de boolean de todos os movimentos poss�veis da pe�a em quest�o
			boolean[][] mat = p.possibleMoves();
			for(int i = 0; i < board.getRows(); i++) {
				for(int j = 0; j < board.getColumns(); j++) {
					//Testa se � um movimento poss�vel
					if(mat[i][j]) {
						//Move a pe�a em quest�o para a posi��o que est� sendo testada
						Position source = ((ChessPiece)p).getChessPosition().toPosition();
						Position target = new Position(i, j);
						Piece capturedPiece = makeMove(source, target);
						//Testa se o rei da cor em quest�o ainda est� em xeque
						boolean testCheck = testCheck(color);
						undoMove(source, target, capturedPiece);
						//Se n�o estiver em xeque mesmo assim, retorna falso para xequemate
						if(!testCheck) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	//Instancia a posi��o de uma nova pe�a passando a posi��o em coord de xadrez
	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column,row).toPosition());
		piecesOnTheBoard.add(piece);
	}
	
	private void initialSetup() {
		placeNewPiece('A', 1, new Rook(board, Color.WHITE));
		placeNewPiece('B', 1, new Knight(board, Color.WHITE));
		placeNewPiece('C', 1, new Bishop(board, Color.WHITE));
		placeNewPiece('D', 1, new Queen(board, Color.WHITE));
		placeNewPiece('E', 1, new King(board, Color.WHITE));
		placeNewPiece('F', 1, new Bishop(board, Color.WHITE));
		placeNewPiece('G', 1, new Knight(board, Color.WHITE));
		placeNewPiece('H', 1, new Rook(board, Color.WHITE));
		placeNewPiece('A', 2, new Pawn(board, Color.WHITE));
		placeNewPiece('B', 2, new Pawn(board, Color.WHITE));
		placeNewPiece('C', 2, new Pawn(board, Color.WHITE));
		placeNewPiece('D', 2, new Pawn(board, Color.WHITE));
		placeNewPiece('E', 2, new Pawn(board, Color.WHITE));
		placeNewPiece('F', 2, new Pawn(board, Color.WHITE));
		placeNewPiece('G', 2, new Pawn(board, Color.WHITE));
		placeNewPiece('H', 2, new Pawn(board, Color.WHITE));
		
        placeNewPiece('A', 8, new Rook(board, Color.BLACK));
        placeNewPiece('B', 8, new Knight(board, Color.BLACK));
        placeNewPiece('C', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('D', 8, new Queen(board, Color.BLACK));
        placeNewPiece('E', 8, new King(board, Color.BLACK));
        placeNewPiece('F', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('G', 8, new Knight(board, Color.BLACK));
        placeNewPiece('H', 8, new Rook(board, Color.BLACK));
        placeNewPiece('A', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('B', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('C', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('D', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('E', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('F', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('G', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('H', 7, new Pawn(board, Color.BLACK));
	}
}
