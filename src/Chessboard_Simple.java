import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import chessPieces.Bishop;
import chessPieces.ChessPiece;
import chessPieces.King;
import chessPieces.Knight;
import chessPieces.Pawn;
import chessPieces.Queen;
import chessPieces.Rook;


public class Chessboard_Simple extends JPanel implements Cloneable{

	private static final long serialVersionUID = 1L;

	private Square[][] grid = new Square[8][8];
	
	private Color selectColor = Color.red;
	private Color availableColor = Color.orange;
	private Color checkColor = Color.magenta;
	
	private Square selection;
	private ArrayList<Square> selectionMoves = new ArrayList<Square>();
	
	private boolean whiteTurn = true;
	
	
	public static void main(String[] args){
		JFrame frame = new JFrame();
		Chessboard_Simple board = new Chessboard_Simple();
		frame.setTitle("Chessboard");
		frame.add(board);
		
		frame.setVisible(true);
		frame.setSize(800,800);
		frame.setMinimumSize(new Dimension(600, 600));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		
	}
	
	public Chessboard_Simple(){
		this(Color.gray, Color.white);
	}
	
	public Chessboard_Simple(Color color1, Color color2){
		
		initializeBoard(color1, color2);
		
		initializeSide(ChessPiece.WHITE);
		initializeSide(ChessPiece.BLACK);
	}
	
	private void initializeBoard(Color color1, Color color2){
		int size = grid.length;
		GridLayout layout = new GridLayout(size, size);
		this.setLayout(layout);
		Color currentColor = color1;
		for (int row = 0; row < size; row++){
			for (int column = 0; column < size; column++){
				if (((row + column) & 1) == 1)
					currentColor = color1;
				else
					currentColor = color2;
				grid[row][column] = new Square(currentColor, row, column);
				SquareListener listener = new SquareListener();
				grid[row][column].addActionListener(listener);
//				grid[row][column].addActionListener(new SquareListener());
				this.add(grid[row][column]);
			}
		}
	}
	
	private void initializeSide(int side){
		int boardSide = 7;
		if (side == ChessPiece.BLACK)
			boardSide = 0;
		// Setting pawns
		for (int column = 0; column < grid.length; column++)
			grid[Math.abs(boardSide - 1)][column].setPiece(new Pawn(side));
		grid[boardSide][0].setPiece(new Rook(side));
		grid[boardSide][7].setPiece(new Rook(side));
		grid[boardSide][1].setPiece(new Knight(side));
		grid[boardSide][6].setPiece(new Knight(side));
		grid[boardSide][2].setPiece(new Bishop(side));
		grid[boardSide][5].setPiece(new Bishop(side));
		grid[boardSide][3].setPiece(new Queen(side));
		grid[boardSide][4].setPiece(new King(side));
	}

	private class Position{
		final int row;
		final int column;
		
		Position(int row, int column){
			this.row = row;
			this.column = column;
		}
		
		public String toString(){
			return "(" + row + ", " + column + ")";
		}
	}
	
	private class Square extends JButton implements Cloneable{
		
		private static final long serialVersionUID = 1L;
		
		private ChessPiece piece;
		private Color bkg;
		public final int row;
		public final int column;
		
		public Square(Color color, int row, int column){
			this(color, null, row, column);
		}
		
		public Square(Color color, ChessPiece piece, int row, int column){
			super();
			super.setBackground(color);
			this.row = row;
			this.column = column;
			this.setPiece(piece);
			this.bkg = color;		
		}
		
		public void setPiece(ChessPiece piece){
			this.piece = piece;
			if (piece instanceof Pawn && (row == 0 || row == 7)){
				System.out.println("Promoting pawn: " + this);
				this.piece = new Queen(piece.getSide());
			}
			if (piece == null){
				super.setIcon(null);
			}
			else {
				super.setIcon(this.piece.getIcon());
			}
		}
		
		public ChessPiece getPiece(){
			return this.piece;
		}
		
		public void resetBackground(){
			super.setBackground(bkg);
		}

		@Override
		public boolean equals(Object compare){
			Square other = (Square)compare;
			return other.getPiece() == this.piece && 
					other.row == this.row && 
					other.column == this.column;
		}
		
		public Object clone(){
			return new Square(this.bkg, this.piece, this.row, this.column);
		}
		
		public String toString(){
			return "(" + row + ", " + column +") " + piece;
		}
		
		/**
		 * Move Logic
		 */

		private boolean isValid(int row, int column){
			return row < 8 && column < 8 && row > -1 && column > -1;
		}

		private ArrayList<Position> getMoves(){
			ArrayList<Position> moves = new ArrayList<Position>();
			if (piece instanceof Knight)
				moves = getMovesKnight();
			else if (piece instanceof Bishop)
				moves = getMovesBishop();
			else if (piece instanceof Rook)
				moves = getMovesRook();
			else if (piece instanceof Queen)
				moves = getMovesQueen();
			else if (piece instanceof Pawn)
				moves = getMovesPawn();
			else if (piece instanceof King)
				moves = getMovesKing();
			return moves;
		}
		
		private ArrayList<Position> getMovesKnight(){
			
			ArrayList<int[]> moves = new ArrayList<int[]>();
			moves.add(new int[]{1, 2});
			moves.add(new int[]{1, -2});
			moves.add(new int[]{-1, 2});
			moves.add(new int[]{-1, -2});
			moves.add(new int[]{2, 1});
			moves.add(new int[]{2, -1});
			moves.add(new int[]{-2, 1});
			moves.add(new int[]{-2, -1});
			
			ArrayList<Position> positions = new ArrayList<Position>();
			for (int[] move : moves){
				if(isValid(row + move[0], column + move[1]))
					positions.add(new Position(row + move[0], column + move[1]));
			}
			for (int i = 0; i < positions.size(); i++){
				Position p = positions.get(i);
				if (grid[p.row][p.column].getPiece() != null &&
						grid[p.row][p.column].getPiece().getSide() == piece.getSide())
					positions.remove(p);
			}
			
			return positions;
		}
		private ArrayList<Position> getMovesBishop(){
			return moveDiagonal();
		}
		private ArrayList<Position> getMovesRook(){
			ArrayList<Position> moves = new ArrayList<Position>();
			moves.addAll(moveHorizontal());
			moves.addAll(moveVertical());
			return moves;
		}
		private ArrayList<Position> getMovesQueen(){
			ArrayList<Position> moves = new ArrayList<Position>();
			moves.addAll(moveDiagonal());
			moves.addAll(moveHorizontal());
			moves.addAll(moveVertical());
			return moves;
		}
		private ArrayList<Position> getMovesPawn(){
			int shift;
			if (piece.getSide() == ChessPiece.BLACK)
				shift = 1;
			else
				shift = -1;
			
			ArrayList<Position> moves = new ArrayList<Position>();
			// Move forward 2 if on original row
			if ((row == 1 && piece.getSide() == ChessPiece.BLACK ||
					row == 6 && piece.getSide() == ChessPiece.WHITE) &&
					grid[row + shift][column].getPiece() == null &&
					grid[row + shift * 2][column].getPiece() == null){
				moves.add(new Position(row + shift * 2, column));		
			}
			// Move forward 1
			if (grid[row + shift][column].getPiece() == null)
				moves.add(new Position(row + shift, column));
			// Take piece to the right
			if (isValid(row + shift, column + shift) &&
					grid[row + shift][column + shift].getPiece() != null &&
					grid[row + shift][column + shift].getPiece().getSide() != piece.getSide())
				moves.add(new Position(row + shift, column + shift));
			// Take piece to the left
			if (isValid(row + shift, column - shift) &&
					grid[row + shift][column - shift].getPiece() != null &&
					grid[row + shift][column - shift].getPiece().getSide() != piece.getSide())
				moves.add(new Position(row + shift, column - shift));
			return moves;
		}
		private ArrayList<Position> getMovesKing(){
			ArrayList<Position> moves = new ArrayList<Position>();
			int[] shift = new int[]{1, 0, -1};
			for (int i : shift){
				for (int j : shift){
					if (isValid(row + i, column + j)){
						ChessPiece piece = grid[row + i][column + j].getPiece();
						if (piece == null || piece.getSide() != this.piece.getSide())
							moves.add(new Position(row + i, column + j));
					}
				}
			}
			return moves;
		}

		public ArrayList<Position> moveDiagonal(){
			ArrayList<Position> moves = new ArrayList<Position>();
			moves.addAll(moveRecursive(row, column, 1, 1));
			moves.addAll(moveRecursive(row, column, 1, -1));
			moves.addAll(moveRecursive(row, column, -1, 1));
			moves.addAll(moveRecursive(row, column, -1, -1));
//			System.out.println("For the Queen -1,-1 diagonal" + moveRecursive(row, column, -1, -1));
			return moves;
		}
		public ArrayList<Position> moveHorizontal(){
			ArrayList<Position> moves = new ArrayList<Position>();
			moves.addAll(moveRecursive(row, column, 0, 1));
			moves.addAll(moveRecursive(row, column,0, -1));
			return moves;
		}
		public ArrayList<Position> moveVertical(){
			ArrayList<Position> moves = new ArrayList<Position>();
			moves.addAll(moveRecursive(row, column, 1, 0));
			moves.addAll(moveRecursive(row, column, -1, 0));
			return moves;
		}	
		public ArrayList<Position> moveRecursive(int row, int column, int rowAdjust, int columnAdjust){
			int currentRow = row + rowAdjust;
			int currentColumn = column + columnAdjust;
			ArrayList<Position> moves = new ArrayList<Position>();
			if(isValid(currentRow, currentColumn)){
//				System.out.println(grid[currentRow][currentColumn]);
				if(grid[currentRow][currentColumn].getPiece() == null){
					moves.add(new Position(currentRow, currentColumn));
					moves.addAll(moveRecursive(currentRow, currentColumn, rowAdjust, columnAdjust));
				}
				else if (grid[currentRow][currentColumn].getPiece().getSide() != piece.getSide())
					moves.add(new Position(currentRow, currentColumn));
			}
			return moves;
		}

	}

	private class SquareListener implements ActionListener{
		
		@Override
		public void actionPerformed(ActionEvent e) {
			Square square = (Square)e.getSource();
			int currentSide;
			if (whiteTurn){
				currentSide = ChessPiece.WHITE;
			}
			else{
				currentSide = ChessPiece.BLACK;
			}
			// Selecting the piece
			if(selection == null && square.getPiece() != null && square.getPiece().getSide() == currentSide){
				selectSquare(square);
			}
			else if(selection == square){
				deselectSquare(square);
			}
			// Moving the piece
			else if(selectionMoves.contains(square)){
				movePiece(selection, square);
				deselectSquare(selection);
				whiteTurn = !whiteTurn;
			}
//			testCheck(ChessPiece.WHITE);
//			testCheck(ChessPiece.BLACK);
		}
		
	}
	
	private void selectSquare(Square square){
		selection = square;
		for (Position p : square.getMoves()){
			if (testMove(grid[p.row][p.column])){
				selectionMoves.add(grid[p.row][p.column]);
				grid[p.row][p.column].setBackground(availableColor);
			}
			else
				System.out.println("Move filtered.");
		}
		if (selectionMoves.size() != 0){
			square.setBackground(selectColor);
		}
		else
			selection = null;
		
	}
	private void deselectSquare(Square square){
		square.resetBackground();
		selection = null;
		for (Square s : selectionMoves){
			s.resetBackground();
		}
		selectionMoves.clear();
	}
	protected void movePiece(Square origin, Square target){
		target.setPiece(origin.getPiece());
		origin.setPiece(null);
	}

	private ArrayList<Square> getPieces(int side){
		ArrayList<Square> pieces = new ArrayList<Square>();
		for (int row = 0; row < 8; row++){
			for (int column = 0; column < 8; column++){
				Square s = grid[row][column];
				if (s.getPiece() != null){
					if (s.getPiece().getSide() == side)
						pieces.add(s);
				}
			}
		}
		return pieces;
	}
	
	private boolean testCheck(int side){
		boolean checkDetected = false;
		Square king = null;
		ArrayList<Square> pieces;
		int good;
		int bad;
		
		if (side == ChessPiece.WHITE){
			good = ChessPiece.WHITE;
			bad = ChessPiece.BLACK;
		}
		else{
			good = ChessPiece.BLACK;
			bad = ChessPiece.WHITE;
		}
			
		pieces = getPieces(bad);
		for (Square s : getPieces(good)){
			if (s.getPiece() instanceof King){
				king = s;
				break;
			}
		}
		
//		System.out.println(this);
//		System.out.println("White: " + getPieces(ChessPiece.WHITE));
//		System.out.println("Black: " + getPieces(ChessPiece.BLACK));
//		System.out.println();
		
		for (Square s : pieces){
			ArrayList<Position> moves = new ArrayList<Position>();
			// Get pawn threats
			if (s.getPiece() instanceof Pawn){
				Position pos1;
				Position pos2;
				int adjust;
				//row < 8 && column < 8 && row > -1 && column > -1
				if (s.getPiece().getSide() == ChessPiece.WHITE)
					adjust = -1;
				else
					adjust = 1;
				pos1 = new Position(s.row + adjust, s.column -1);
				pos2 = new Position(s.row + adjust, s.column +1);
				if(pos1.row < 8 && pos1.column < 8 && pos1.row > -1 && pos1.column > -1)
					moves.add(pos1);
				if(pos2.row < 8 && pos2.column < 8 && pos2.row > -1 && pos2.column > -1)
					moves.add(pos2);
			}
			else
				moves = s.getMoves();
			for (Position p : moves){
				if (p.row == king.row && p.column == king.column){
					checkDetected = true;
					s.setBackground(checkColor);
				}
			}
		}
		return checkDetected;
		}

	//returns true if move is valid
	private boolean testMove(Square square){
		System.out.println("Testing square: " + square);
		boolean validity = true;
		
		Chessboard_Simple checkBoard = (Chessboard_Simple)this.clone();
		checkBoard.movePiece(checkBoard.grid[selection.row][selection.column], checkBoard.grid[square.row][square.column]);

		if (whiteTurn){
			if (checkBoard.testCheck(ChessPiece.WHITE)){
				System.out.println("Invalid move found.");
				validity = false;
			}
		}
		else { //blackTurn
			if (checkBoard.testCheck(ChessPiece.BLACK)){
				System.out.println("Invalid move found.");
				validity = false;
			}
		}
		return validity;
	}

	private Chessboard_Simple(Chessboard_Simple original){
		for (int row = 0; row < 8; row++){
			for (int column = 0; column < 8; column++){
				this.grid[row][column] = (Square)original.grid[row][column].clone();
			}
		}
	}
	
	public Chessboard_Simple clone(){
		return new Chessboard_Simple(this);
	}

	public String toString(){
		String output = "";
		for (int row = 0; row < 8; row++){
			for (int column = 0; column < 8; column ++){
				String rep = "[";
				if (grid[row][column].getPiece() != null){
					rep += grid[row][column].getPiece();
				}
				else rep += "  "+row+" "+","+" "+column+"   ";
				rep += "] ";
				output += rep;
			}
			output += "\n";
		}
		return output;
	}

	private void sleep(){
		sleep(1);
	}
	
	private void sleep(int s){
		try {
			Thread.sleep(s * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
