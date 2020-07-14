package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {

	private int turn;
	private Color currentPlayer;
	private Board board;
	private boolean check;
	
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
				
		nextTurn();
		return (ChessPiece) capturedPiece;
	}
	
	/*
	 Retira a pe�a da posi��o de origem e guarda em p, remove a pe�a em traget 
	 e guarda em capturedPiece, posiciona p em target
	 */
	private Piece makeMove(Position source, Position target) {
		Piece p = board.removePiece(source);
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
		Piece p = board.removePiece(target);
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
	private Color opponent(Color color) {
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
	
	//Instancia a posi��o de uma nova pe�a passando a posi��o em coord de xadrez
	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column,row).toPosition());
		piecesOnTheBoard.add(piece);
	}
	
	private void initialSetup() {
		placeNewPiece('C', 1, new Rook(board, Color.WHITE));
		placeNewPiece('E', 1, new Rook(board, Color.WHITE));	        
		placeNewPiece('D', 1, new King(board, Color.WHITE));	        
        placeNewPiece('E', 2, new Rook(board, Color.WHITE));
        placeNewPiece('C', 2, new Rook(board, Color.WHITE));
        placeNewPiece('D', 2, new Rook(board, Color.WHITE));

        placeNewPiece('C', 7, new Rook(board, Color.BLACK));
        placeNewPiece('C', 8, new Rook(board, Color.BLACK));
        placeNewPiece('D', 7, new Rook(board, Color.BLACK));
        placeNewPiece('E', 7, new Rook(board, Color.BLACK));
        placeNewPiece('E', 8, new Rook(board, Color.BLACK));
        placeNewPiece('D', 8, new King(board, Color.BLACK));
	}
}
