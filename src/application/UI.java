package application;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.Color;

public class UI {

	// https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
	public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
	public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
	public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
	public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
	public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
	public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
	public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
	
	// https://stackoverflow.com/questions/2979383/java-clear-the-console
	public static void clearScreen() {
		System.out.print("\033[H\033[2J");
		System.out.flush();
	}

	//L� a posi��o do usu�rio
	public static ChessPosition readChessPosition(Scanner sc) {
		try {
			String s = sc.nextLine();
			char column = s.charAt(0);
			int row = Integer.parseInt(s.substring(1));
			return new ChessPosition(column, row);
		}
		catch (RuntimeException e) {
			throw new InputMismatchException("Error reading ChessPosition.");
		}
	}
	
	//Imprime a partida (al�m de apenas o tabuleiro), recebe tamb�m as pe�as capturadas
	public static void printMatch(ChessMatch chessMatch, List<ChessPiece> captured) {
		printBoard(chessMatch.getPieces());
		System.out.println();
		printCapturedPieces(captured);
		System.out.println();
		System.out.println("Turn: " + chessMatch.getTurn());
		//Antes de imprimir o pr�ximo turno testa se teve xequemate
		if(!chessMatch.getCheckMate()) {
			System.out.println("Waiting player: " + chessMatch.getCurrentPlayer());
			//Avisa se o jogador atual est� em check
			if(chessMatch.getCheck()) {
				System.out.println("CHECK!!");
			}
		}
		else {
			System.out.println("CHECKMATE!!");
			System.out.println("Winner: " + chessMatch.getCurrentPlayer());
			System.out.println(chessMatch.opponent(chessMatch.getCurrentPlayer()) + 
					ANSI_RED + " BUSTED" + ANSI_RESET);
		}
		System.out.println();
	}
	
	//Imprime o tabuleiro 
	public static void printBoard(ChessPiece[][] pieces) {
		for (int i = 0; i < pieces.length; i++) {
			System.out.print(ANSI_CYAN + (8 - i) + ANSI_RESET + " ");
			for (int j = 0; j < pieces.length; j++) {
				printPiece(pieces[i][j], false);
			}
			System.out.println();
		}
		System.out.println(ANSI_CYAN + "  A B C D E F G H" + ANSI_RESET);
	}
	
	//Imprime tabuleiro colorindo posi��es poss�veis
	public static void printBoard(ChessPiece[][] pieces, boolean[][] possibleMoves) {
		for (int i = 0; i < pieces.length; i++) {
			System.out.print(ANSI_CYAN + (8 - i) + ANSI_RESET + " ");
			for (int j = 0; j < pieces.length; j++) {
				printPiece(pieces[i][j], possibleMoves[i][j]);
			}
			System.out.println();
		}
		System.out.println(ANSI_CYAN + "  A B C D E F G H" + ANSI_RESET);
	}

	//Imprime cada pe�a de xadrez
	private static void printPiece(ChessPiece piece, boolean background) {
		//So executa se a vari�vel background (possibleMoves no metodo printBoard) for true
		if(background) {
			System.out.print(ANSI_BLUE_BACKGROUND);
		}
		if (piece == null) {
			System.out.print("-" + ANSI_RESET);
		} 
		else {
			if (piece.getColor() == Color.WHITE) {
				System.out.print(ANSI_WHITE + piece + ANSI_RESET);
			}        
	        else {
	        	System.out.print(ANSI_YELLOW + piece + ANSI_RESET);
	        }
		}
		System.out.print(" ");
	}
	
	//Recebe uma lista de pe�as capturadas, separa as cores e imprime as duas listas resultantes
	private static void printCapturedPieces(List<ChessPiece> captured) {
		List<ChessPiece> white = captured.stream().filter(x -> x.getColor() == Color.WHITE).collect(Collectors.toList());
		List<ChessPiece> black = captured.stream().filter(x -> x.getColor() == Color.BLACK).collect(Collectors.toList());
		System.out.println("Captured pieces:");
		System.out.print("White: ");
		System.out.print(ANSI_WHITE);
		System.out.println(Arrays.toString(white.toArray()));
		System.out.print(ANSI_RESET);
		System.out.print("Black: ");
		System.out.print(ANSI_YELLOW);
		System.out.println(Arrays.toString(black.toArray()));
		System.out.print(ANSI_RESET);		
		
	}
}
