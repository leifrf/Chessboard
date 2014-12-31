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


public class Chessboard extends JPanel implements Cloneable{

	private static final long serialVersionUID = 1L;

	private Square[][] grid = new Square[8][8];
	private Color selectColor = Color.red;
	private Color availableColor = Color.orange;
	private Color checkColor = Color.magenta;
	private Square selection;
	private ArrayList<Square> selectionMoves = new ArrayList<Square>();
	
	private ArrayList<Square> blackPieces = new ArrayList<Square>();
	private ArrayList<Square> whitePieces = new ArrayList<Square>();
	// Not sure if this is really necessary

	private boolean whiteTurn = true;
	
	
	public static void main(String[] args){
		JFrame frame = new JFrame();
		Chessboard board = new Chessboard();
		frame.setTitle("Chessboard");
		frame.add(board);
		
		frame.setVisible(true);
		frame.setSize(800,800);
		frame.setMinimumSize(new Dimension(600, 600));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		
	}
	
	public Chessboard(){
		this(Color.gray, Color.white);
	}
	
	public Chessboard(Color color1, Color color2){
		
		initializeBoard(color1, color2);
		
		initializeSide(ChessPiece.WHITE);
		initializeSide(ChessPiece.BLACK);
		
		scanBoard();
	}
	
	private void initializeBoard(Color color1, Color color2){
		int size = grid.length;
		GridLayout layout = new GridLayout(size, size);
		this.setLayout(layout);
//		grid = new Square[size][size];
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
	
	// Really inefficient way of updating sides
	private void scanBoard(){
		whitePieces.clear();
		blackPieces.clear();
		for (int row = 0; row < 8; row ++){
			for (int column = 0; column < 8; column++){
				ChessPiece piece = grid[row][column].getPiece();
				if (piece != null){
					if (piece.getSide() == ChessPiece.WHITE)
						whitePieces.add(grid[row][column]);
					else
						blackPieces.add(grid[row][column]);
				}
			}
		}
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
				super.setIcon(piece.getIcon());
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
				scanBoard();
				whiteTurn = !whiteTurn;
			}
//			testCheck(ChessPiece.WHITE);
//			testCheck(ChessPiece.BLACK);
		}
		
	}
	
	protected void selectSquare(Square square){
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

	public boolean testCheck(int side){
		ArrayList<Square> positions;
		Position kingPosition = null;
		boolean checkDetected = false;
		// Getting positions & kingPosition
		if (side == ChessPiece.BLACK){
			System.out.println("Testing black side.");
			for (Square s : blackPieces){
				if (s.getPiece() instanceof King){
					kingPosition = new Position(s.row, s.column);
					break;
				}
			}
			positions = whitePieces;
		}
		else{
			System.out.println("Testing white side.");
			for (Square s : whitePieces){
				if (s.getPiece() instanceof King){
					kingPosition = new Position(s.row, s.column);
					break;
				}
			}
			positions = blackPieces;
		} // Done getting positions
		
		// Finding checks
		System.out.println("King at: " + kingPosition);
		for (Square s : positions){
//			if(s.getPiece() instanceof Queen)
//				System.out.println("Testing piece " + s.getPiece() + " at " + s.row + ", " + s.column);
//			System.out.println("Finding checks");
			for (Position m : s.getMoves()){
//				if(s.getPiece() instanceof Queen)
//					System.out.println("Queen accesses square " + m + " King at " + kingPosition);
				if (m.row == kingPosition.row && m.column == kingPosition.column){
					s.setBackground(checkColor);
					checkDetected = true;
					System.out.println("Check detected.");
					break;
				}
			}
		}
		return checkDetected;
	}

	public boolean testCheckMate(int side){
		return false;
	}
	
	//returns true if move is valid
	private boolean testMove(Square square){
		System.out.println("Testing square: " + square);
		Square backupSquare1 = (Square)selection.clone();
		Square backupSquare2 = (Square)square.clone();
		boolean validity = true;
		
		Chessboard checkBoard = (Chessboard)this.clone();
		System.out.println("Board cloned in testMove.");
		checkBoard.movePiece(checkBoard.grid[backupSquare1.row][backupSquare1.column], checkBoard.grid[backupSquare2.row][backupSquare2.column]);
		System.out.println(checkBoard);
		if (whiteTurn){
//			System.out.println("In here");
//			System.out.println(checkBoard);
//			System.out.println(checkBoard.whitePieces.size());
			if (checkBoard.testCheck(ChessPiece.WHITE)){
				System.out.println("Invalid move found.");
				validity = false;
			}
		}
		else { //blackTurn
//			System.out.println("Or in here");
//			System.out.println(checkBoard);
//			System.out.println(checkBoard.blackPieces.size());
			if (checkBoard.testCheck(ChessPiece.BLACK)){
				System.out.println("Invalid move found.");
				validity = false;
			}
		}
		return validity;
	}

	private Chessboard(Chessboard original){
		for (int row = 0; row < 8; row++){
			for (int column = 0; column < 8; column++){
				this.grid[row][column] = (Square)original.grid[row][column].clone();
			}
		}
		this.scanBoard();
	}
	
	public Chessboard clone(){
		return new Chessboard(this);
	}

	public String toString(){
		String output = "";
		for (int row = 0; row < 8; row++){
			for (int column = 0; column < 8; column ++){
				String rep = "[";
				if (grid[row][column].getPiece() != null){
					rep += grid[row][column].getPiece();
				}
				else rep += "          ";
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
