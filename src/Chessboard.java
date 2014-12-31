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
	
	private boolean whiteTurn = true;
	
	SquareListener listener = new SquareListener();
	
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
				
				if (whiteTurn){
					if(victory(ChessPiece.WHITE))
						System.out.println("White wins.");
				}
				else
					if(victory(ChessPiece.BLACK))
						System.out.println("Black wins.");
				
				whiteTurn = !whiteTurn;
			}
		}
		
	}
	
	private boolean victory(int side){

//		System.out.println("in victory.");
		
		int otherSide;
		if (side == ChessPiece.WHITE)
			otherSide = ChessPiece.BLACK;
		else
			otherSide = ChessPiece.WHITE;
			
		if (testCheck(otherSide)){
//			System.out.println("check detected.");
			return testCheckMate(otherSide);
		}
		return false;
//		return testCheck(side) && testCheckMate(side);
	}
	
	private void selectSquare(Square square){
		selection = square;
		for (Position p : square.getMoves()){
			if (testMove(square, grid[p.row][p.column])){
				selectionMoves.add(grid[p.row][p.column]);
				grid[p.row][p.column].setBackground(availableColor);
			}
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

	private boolean isValid(int row, int column){
		return row < 8 && column < 8 && row > -1 && column > -1;
	}
	
	/**
	 * RETURNS TRUE IF IN CHECKMATE 
	 * 
	 * @param side
	 * @return
	 */
	private boolean testCheckMate(int side){
		
		boolean inCheckMate = true;
		
		ArrayList<Square> pieces = new ArrayList<Square>();
		for (Square[] squares : grid){
			for (Square s : squares){
				if (s.getPiece() != null && s.getPiece().getSide() == side)
					pieces.add(s);
			}
		}

		for (Square s : pieces){
			for (Position p : s.getMoves()){
				if (testMove(s, grid[p.row][p.column])){
					// This is where I would grab the squares for highlight options for moving out of check
//					System.out.println("Valid move: " + s + " to " + grid[p.row][p.column]);
					inCheckMate = false;
				}
			}
		}
		
		return inCheckMate;
		
	}
	
	/**
	 * RETURNS TRUE IF IN CHECK
	 * 
	 * @param side
	 * @return true if in check
	 */
	private boolean testCheck(int side){
//		System.out.println("Testing check.");
		Square kingPosition = null;
		for (int row = 0; row < 8; row++){
			for (int column = 0; column < 8; column++){
				if (grid[row][column].getPiece() != null &&
						grid[row][column].getPiece() instanceof King &&	
						grid[row][column].getPiece().getSide() == side){
					kingPosition = grid[row][column];
					break;
				}
			}
		}
//		System.out.println("King found in testCheck.");
		
		return checkKnight(kingPosition) | checkPawn(kingPosition) | checkLines(kingPosition);
	}
	private boolean checkKnight(Square kingPosition){
		
//		System.out.println("in checkKnight.");
		
		boolean inCheck = false;
		
		int row = kingPosition.row;
		int column = kingPosition.column;
		ChessPiece piece = kingPosition.getPiece();
		
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
					grid[p.row][p.column].getPiece() instanceof Knight &&
					grid[p.row][p.column].getPiece().getSide() != piece.getSide()){
				inCheck = true;
				grid[p.row][p.column].setBackground(checkColor);
			}
		}
		
		return inCheck;
	}
	private boolean checkPawn (Square kingPosition){
		
//		System.out.println("in checkPawn");
		
		int side = kingPosition.getPiece().getSide();
		int adjust;
		// Getting direction
		if (side == ChessPiece.WHITE)
			adjust = -1;
		else
			adjust = 1;
		
		ChessPiece piece1 = null;
		if (isValid(kingPosition.row + adjust, kingPosition.column + adjust))
			piece1 = grid[kingPosition.row + adjust][kingPosition.column + adjust].getPiece();
		ChessPiece piece2 = null; 
		if (isValid(kingPosition.row + adjust, kingPosition.column - adjust))
			piece2 = grid[kingPosition.row + adjust][kingPosition.column - adjust].getPiece();
		
		boolean inCheck = false;
		
		if (piece1 instanceof Pawn && piece1.getSide() != side){
			grid[kingPosition.row + adjust][kingPosition.column + adjust].setBackground(checkColor);
			inCheck = true;
		}
		if (piece2 instanceof Pawn && piece2.getSide() != side){
			grid[kingPosition.row + adjust][kingPosition.column - adjust].setBackground(checkColor);
			inCheck = true;
		}
		
		return inCheck;
	}
	private boolean checkLines(Square kingPosition){
		
//		System.out.println("in checKLines.");
		
		boolean inCheck = false;
		
		int row = kingPosition.row;
		int column = kingPosition.column;
		//Only meant to be used with the king position
		King piece = (King)kingPosition.getPiece();
		
		Square conflictSquare = null;
		
		int[][] diagonals = new int[][]{new int[]{1, 1}, new int[]{1, -1}, new int[]{-1, 1}, new int[]{-1, -1}};
		int[][] rows = 		new int[][]{new int[]{1, 0}, new int[]{-1, 0}, new int[]{0, -1}, new int[]{0, 1}};
		//Diagonals
		for (int[] diag : diagonals){
			conflictSquare = checkRecursive(row, column, diag[0], diag[1]);
			if (conflictSquare != null &&
				conflictSquare.getPiece().getSide() != piece.getSide() && 
				(conflictSquare.getPiece() instanceof Queen || conflictSquare.getPiece() instanceof Bishop)){
				conflictSquare.setBackground(checkColor);
				inCheck = true;
			}
		}
		//Rows & Columns
		for (int[] ro : rows){
			conflictSquare = checkRecursive(row, column, ro[0], ro[1]);
			if (conflictSquare != null && 
				conflictSquare.getPiece().getSide() != piece.getSide() &&
				(conflictSquare.getPiece() instanceof Rook || conflictSquare.getPiece() instanceof Queen)){
				conflictSquare.setBackground(checkColor);
				inCheck = true;
			}
		}
		
		return inCheck;
	}
	private Square checkRecursive(int row, int column, int rowAdjust, int columnAdjust){
		
		Square square = null;
		
		int currentRow = row + rowAdjust;
		int currentColumn = column + columnAdjust;
		if(isValid(currentRow, currentColumn)){
			if(grid[currentRow][currentColumn].getPiece() == null)
				square = checkRecursive(currentRow, currentColumn, rowAdjust, columnAdjust);
			else
				square = grid[currentRow][currentColumn];
		}
		return square;
	}

	private boolean testMove(Square origin, Square target){
		boolean validity = true;
		
		Chessboard checkBoard = (Chessboard)this.clone();
		checkBoard.movePiece(checkBoard.grid[origin.row][origin.column], checkBoard.grid[target.row][target.column]);
		
		if (checkBoard.testCheck(origin.getPiece().getSide()))
			validity = false;
		
		return validity;
	}

	private Chessboard(Chessboard original){
		for (int row = 0; row < 8; row++){
			for (int column = 0; column < 8; column++){
				this.grid[row][column] = (Square)original.grid[row][column].clone();
			}
		}
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
