import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;

import chessPieces.Bishop;
import chessPieces.Castleable;
import chessPieces.ChessPiece;
import chessPieces.King;
import chessPieces.Knight;
import chessPieces.Pawn;
import chessPieces.Queen;
import chessPieces.Rook;

/**
 * This is a simple chess game. It follows the normal rules. For a full version
 * of the rules, consult Wikipedia.
 * 
 * This version does not support an AI.
 * 
 * @author Leif Raptis-Firth
 *
 */
public class Chessboard extends JPanel implements Cloneable {

	private static final long serialVersionUID = 1L;
	// The squares in the game
	private Square[][] grid = new Square[8][8];
	// Color references
	private static final Color SIENNA = new Color(160, 82, 45); // Color1
	private static final Color BEIGE = new Color(245, 245, 220); // Color2
	private static final Color FIREBRICK = new Color(178, 34, 34); // Selected
	private static final Color DARK_ORANGE = new Color(255, 140, 0); // Available, brighter for Castling
	private static final Color DARK_SLATE_BLUE = new Color(72, 61, 139); // Check
	// Colors used in the program
	private Color selectColor = FIREBRICK;
	private Color availableColor = DARK_ORANGE;
	private Color castleColor = availableColor.brighter();
	private Color checkColor = DARK_SLATE_BLUE;
	// The currently selected square
	private Square selection;
	// The moves that the current square may perform
	private ArrayList<Square> selectionMoves = new ArrayList<Square>();
	// Whose turn it currently is
	private boolean whiteTurn = true;
	// Stack of moves for undo and En Passant
	// [0] is origin, [1] is target
	private Stack<Square[]> moveHistory = new Stack<Square[]>();

	/**
	 * Provides an example instance of the game.
	 */
	public static void main(String[] args) {

		JFrame frame = new JFrame();
		Chessboard board = new Chessboard();
		frame.setTitle("Chessboard");
		frame.add(board);
		frame.setVisible(true);
		frame.setSize(800, 800);
		frame.setMinimumSize(new Dimension(600, 600));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
	}

	// -------------------------------------------------------------------------- Start of Building Board
	/**
	 * Default constructor Uses SIENNA and BEIGE colors
	 */
	public Chessboard() {
		this(SIENNA, BEIGE);
	}

	/**
	 * Creates a chess board with the given colors. It initializes the board and
	 * sides.
	 * 
	 * @param color1
	 *            Color for odd squares.
	 * @param color2
	 *            Color for even squares.
	 */
	public Chessboard(Color color1, Color color2) {

		initializeBoard(color1, color2);
		initializeSide(ChessPiece.WHITE);
		initializeSide(ChessPiece.BLACK);

	}
	

	/**
	 * Populates the grid of Squares and sets their colors. Attaches a
	 * SquareListener to each square.
	 * 
	 * @param color1
	 *            Color for odd squares.
	 * @param color2
	 *            Color for even squares.
	 */
	private void initializeBoard(Color color1, Color color2) {
		int size = grid.length;
		GridLayout layout = new GridLayout(size, size);
		this.setLayout(layout);
		Color currentColor = color1;
		// This listener does the game logic & user interaction
		SquareListener listener = new SquareListener();
		// For each square in the grid
		for (int row = 0; row < size; row++) {
			for (int column = 0; column < size; column++) {
				// Select color based on square position
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

	/**
	 * Initializes the indicated side. Side is intended to be either
	 * ChessPiece.WHITE or ChessPiece.BLACK. White is set at the bottom of the
	 * board, Black at the top.
	 * 
	 * The side is defined as the following configuration of pieces: [Rook]
	 * [Knight] [Bishop] [Queen] [King] [Bishop] [Knight] [Rook] [Pawn] [Pawn]
	 * [Pawn] [Pawn] [Pawn] [Pawn] [Pawn] [Pawn]
	 * 
	 * @param side
	 *            ChessPiece.WHITE or ChessPiece.BLACK
	 */
	private void initializeSide(int side) {
		// Starts as ChessPiece.WHITE
		int boardSide = getBoardSide(side);
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
	// -------------------------------------------------------------------------- End of Building Board
	
	// -------------------------------------------------------------------------- Start of Square Handling
	/**
	 * This is the primary component of the Chessboard. This class contains its
	 * xy position in the grid, background color, and current piece. No two
	 * squares should share the same positions.
	 * 
	 * There are only 64 instances of Square, one for each square on the
	 * Chessboard. A square's background color changes based on selection,
	 * check, and move availability.
	 * 
	 * Every square is assigned a SquareListener. Currently, this is done in
	 * initializeBoard(). It may be better to move it into Square later.
	 * 
	 * SquareListener is separately defined inner class that works directly with
	 * Square. It may be better to implement ActionListener in Square instead
	 * and do away with SquareListener.
	 * 
	 * @author Leif Raptis-Firth
	 *
	 */
	private class Square extends JButton implements Cloneable {

		private static final long serialVersionUID = 1L;

		private ChessPiece piece;
		private Color bkg;
		public final int row;
		public final int column;

		/**
		 * This constructor is to initialize an empty Square.
		 * 
		 * @param color
		 *            Background color.
		 * @param row
		 *            Row index in the grid. Expected to be between 0 and 7
		 *            inclusive.
		 * @param column
		 *            Column index in the grid. Expected to be between 0 and 7
		 *            inclusive.
		 */
		public Square(Color color, int row, int column) {
			this(color, null, row, column);
		}

		/**
		 * This constructor is to initialize a Square with a ChessPiece.
		 * 
		 * @param color
		 *            Background color.
		 * @param piece
		 *            A ChessPiece.
		 * @param row
		 *            Row index in the grid. Expected to be between 0 and 7
		 *            inclusive.
		 * @param column
		 *            Column index in the grid. Expected to be between 0 and 7
		 *            inclusive.
		 */
		public Square(Color color, ChessPiece piece, int row, int column) {
			super();
			// Technical configurations
			super.setBackground(color);
			this.row = row;
			this.column = column;
			this.setPiece(piece);
			this.bkg = color;
			// Cosmetic configurations
			this.setFocusPainted(false);
			this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		}

		/**
		 * Sets the Square's piece. Pawn promotion is handled here. If the
		 * ChessPiece does not have a valid ImageIcon, no image is set.
		 * 
		 * -- This may be a memory leak
		 * 
		 * @param piece
		 *            The ChessPiece for this Square.
		 */
		public void setPiece(ChessPiece piece) {
			this.piece = piece;
			if (piece instanceof Pawn && (row == 0 || row == 7)) {
				this.piece = new Queen(piece.side);
			}
			if (piece == null) {
				super.setIcon(null);
			} else {
				super.setIcon(this.piece.getIcon());
			}
		}

		/**
		 * ChessPiece Accessor
		 * 
		 * @return The Square's ChessPiece.
		 */
		public ChessPiece getPiece() {
			return this.piece;
		}

		/**
		 * Reset's the Square's background color to its original one.
		 */
		public void resetBackground() {
			super.setBackground(bkg);
		}

		/**
		 * Returns an exact replica of this square.
		 */
		public Object clone() {
			// This is really bad form.
			// Every piece should be cloned in this process.
			// Minimizing cloning is done solely to increase the speed of the
			// program.
			Square clonedSquare = new Square(this.bkg, this.row, this.column);
			if (this.piece instanceof King || this.piece instanceof Rook)
				clonedSquare.setPiece((ChessPiece) this.piece.clone());
			else
				clonedSquare.setPiece(this.piece);
			// Square clonedSquare = new Square(this.bkg, this.piece, this.row,
			// this.column);
			return clonedSquare;
		}

		/**
		 * A Square is defined by its indices and piece. Output: "(" + row +
		 * ", " + column + ") " + piece i.e. "(3, 0) White Rook
		 * 
		 * @return String representation of the Square.
		 */
		public String toString() {
			return "(" + row + ", " + column + ") " + piece;
		}

		// -------------------------------------------------------------------------- Start of Move Logic
		/**
		 * Checks whether the two numbers are between 0 and 7 inclusive. This is
		 * to determine whether or not it is a valid index on the grid.
		 * 
		 * @param row
		 *            Row index of the grid.
		 * @param column
		 *            Column index of the grid.
		 * @return true if both row and column are between 0 and 7 inclusive.
		 */
		private boolean isValid(int row, int column) {
			return row < 8 && column < 8 && row > -1 && column > -1;
		}

		/**
		 * Determines the list of possible moves for the piece. This DOES NOT
		 * take "check" into consideration. The list includes moves that may
		 * leave the King in check. These are not legal and are handled later.
		 * 
		 * -- Consider including testCheck here.
		 * 
		 * @return List of possible moves.
		 */
		private ArrayList<Square> getMoves() {
			ArrayList<Square> moves = new ArrayList<Square>();
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

		/**
		 * Determines the list of possible moves for a Knight.
		 * 
		 * @return List of possible moves for a Knight.
		 */
		private ArrayList<Square> getMovesKnight() {

			// Moves for a Knight independent of the board
			ArrayList<int[]> moves = new ArrayList<int[]>();
			moves.add(new int[] { 1, 2 });
			moves.add(new int[] { 1, -2 });
			moves.add(new int[] { -1, 2 });
			moves.add(new int[] { -1, -2 });
			moves.add(new int[] { 2, 1 });
			moves.add(new int[] { 2, -1 });
			moves.add(new int[] { -2, 1 });
			moves.add(new int[] { -2, -1 });

			// Filtering valid moves
			ArrayList<Square> squares = new ArrayList<Square>();
			for (int[] move : moves) {
				if (isValid(row + move[0], column + move[1])) {
					Square target = grid[row + move[0]][column + move[1]];
					// Target square is empty
					if (target.getPiece() == null)
						squares.add(grid[row + move[0]][column + move[1]]);
					// Target square holds an opposing piece
					else if (target.getPiece().side != piece.side)
						squares.add(grid[row + move[0]][column + move[1]]);
				}
			}

			return squares;
		}

		/**
		 * Determines the list of possible moves for a Bishop.
		 * 
		 * @return List of possible moves for a Bishop.
		 */
		private ArrayList<Square> getMovesBishop() {
			return moveDiagonal();
		}

		/**
		 * Determines the list of possible moves for a Rook.
		 * 
		 * @return List of possible moves for a Rook.
		 */
		private ArrayList<Square> getMovesRook() {
			ArrayList<Square> moves = new ArrayList<Square>();
			moves.addAll(moveHorizontal());
			moves.addAll(moveVertical());
			return moves;
		}

		/**
		 * Determines the list of possible moves for a Queen.
		 * 
		 * @return List of possible moves for a Queen.
		 */
		private ArrayList<Square> getMovesQueen() {
			ArrayList<Square> moves = new ArrayList<Square>();
			moves.addAll(moveDiagonal());
			moves.addAll(moveHorizontal());
			moves.addAll(moveVertical());
			return moves;
		}

		/**
		 * Determines the list of possible moves for a Pawn. This does not cover
		 * En-Passent.
		 * 
		 * @return List of possible moves for a Pawn.
		 */
		private ArrayList<Square> getMovesPawn() {
			int shift;
			if (piece.side == ChessPiece.BLACK)
				shift = 1;
			else
				shift = -1;

			int newRow = row + shift;

			ArrayList<Square> moves = new ArrayList<Square>();
			// Move forward 2 if on original row
			if ((row == 1 && piece.side == ChessPiece.BLACK || row == 6
					&& piece.side == ChessPiece.WHITE)
					&& grid[newRow][column].getPiece() == null
					&& grid[newRow + shift][column].getPiece() == null) {
				moves.add(grid[newRow + shift][column]);
			}
			// Move forward 1
			if (grid[newRow][column].getPiece() == null)
				moves.add(grid[newRow][column]);

			for (int newColumn : new int[] { column + shift, column - shift }) {
				// Take piece on a diagonal
				if (isValid(newRow, newColumn)
						&& grid[newRow][newColumn].getPiece() != null
						&& grid[newRow][newColumn].getPiece().side != piece.side)
					moves.add(grid[newRow][newColumn]);
			}

			// En Passant to one diagonal
			int enPassantRow = 0;
			if (piece.side == ChessPiece.WHITE)
				enPassantRow = 3;
			else
				enPassantRow = 4;

			for (int newColumn : new int[] { column + shift, column - shift }) {
				// Pawn in the correct row for En Passant
				if (row == enPassantRow && isValid(row, newColumn)
						&&
						// Adjacent square contains a Black Pawn
						grid[row][newColumn].getPiece() instanceof Pawn
						&& grid[row][newColumn].getPiece().side != piece.side) {
					Square[] lastMove = moveHistory.peek();
					Square origin = lastMove[0];
					Square target = lastMove[1];
					// Black Pawn moved forward 2 on the last move
					if (Math.abs(origin.row - target.row) == 2
							&& origin.getPiece().equals(
									grid[row][newColumn].getPiece()))
						moves.add(grid[newRow][newColumn]);
				}
			}
			return moves;
		}

		/**
		 * Determines the list of possible moves for a King. This does not
		 * filter moving into check. This is handled later on, in testCheck.
		 * 
		 * @return List of possible moves for a King.
		 */
		private ArrayList<Square> getMovesKing() {
			ArrayList<Square> moves = new ArrayList<Square>();
			int[] shift = new int[] { 1, 0, -1 };
			// For each possible target index
			// Technically considers the King's own position as well
			for (int i : shift) {
				for (int j : shift) {
					if (isValid(row + i, column + j)) {
						ChessPiece piece = grid[row + i][column + j].getPiece();
						if (piece == null || piece.side != this.piece.side)
							moves.add(grid[row + i][column + j]);
					}
				}
			}

			if (testKingSideCastle(this.piece.side))
				moves.add(grid[getBoardSide(this.piece.side)][6]);
			if (testQueenSideCastle(this.piece.side))
				moves.add(grid[getBoardSide(this.piece.side)][2]);

			return moves;
		}

		/**
		 * @return List of possible moves along a diagonal line.
		 */
		private ArrayList<Square> moveDiagonal() {
			ArrayList<Square> moves = new ArrayList<Square>();
			moves.addAll(moveRecursive(row, column, 1, 1));
			moves.addAll(moveRecursive(row, column, 1, -1));
			moves.addAll(moveRecursive(row, column, -1, 1));
			moves.addAll(moveRecursive(row, column, -1, -1));
			return moves;
		}

		/**
		 * @return List of possible moves along a horizontal line.
		 */
		private ArrayList<Square> moveHorizontal() {
			ArrayList<Square> moves = new ArrayList<Square>();
			moves.addAll(moveRecursive(row, column, 0, 1));
			moves.addAll(moveRecursive(row, column, 0, -1));
			return moves;
		}

		/**
		 * @return List of possible moves along a vertical.
		 */
		private ArrayList<Square> moveVertical() {
			ArrayList<Square> moves = new ArrayList<Square>();
			moves.addAll(moveRecursive(row, column, 1, 0));
			moves.addAll(moveRecursive(row, column, -1, 0));
			return moves;
		}

		/**
		 * This is the primary worker for move generators. It will check all
		 * valid moves originating from the given index.
		 * 
		 * @param row
		 *            Starting row.
		 * @param column
		 *            Starting column.
		 * @param rowAdjust
		 *            Adjustment for row.
		 * @param columnAdjust
		 *            Adjustment for column.
		 * @return List of valid moves from the originating square.
		 */
		public ArrayList<Square> moveRecursive(int row, int column,
				int rowAdjust, int columnAdjust) {
			int currentRow = row + rowAdjust;
			int currentColumn = column + columnAdjust;
			ArrayList<Square> moves = new ArrayList<Square>();
			if (isValid(currentRow, currentColumn)) {
				if (grid[currentRow][currentColumn].getPiece() == null) {
					moves.add(grid[currentRow][currentColumn]);
					moves.addAll(moveRecursive(currentRow, currentColumn,
							rowAdjust, columnAdjust));
				} else if (grid[currentRow][currentColumn].getPiece().side != piece.side)
					moves.add(grid[currentRow][currentColumn]);
			}
			return moves;
		}
		// -------------------------------------------------------------------------- End of Move Logic
	}

	/**
	 * This listener determines the validity of action. Alternating of turns and
	 * end of game are determined here. It uses the following global variables:
	 * selection, selectionMoves, whiteTurn.
	 * 
	 * -- Consider refactoring this to be a part of Square by implementation.
	 * 
	 * @author Leif Raptis-Firth
	 *
	 */
	private class SquareListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Square square = (Square) e.getSource();
			
			int currentSide;
			if (whiteTurn) {
				currentSide = ChessPiece.WHITE;
			} else {
				currentSide = ChessPiece.BLACK;
			}

			// Selecting the piece
			if (selection == null && square.getPiece() != null
					&& square.getPiece().side == currentSide) {
				selectSquare(square);
			} else if (selection == square) {
				deselectSquare(square);
			}
			// Moving the piece
			else if (selectionMoves.contains(square)) {
				// Castling handler
				if (selection.getPiece() instanceof King
						&& !((Castleable) selection.getPiece()).hasMoved()
						&& square.row == getBoardSide(currentSide)) {
					if (square.column == 6)
						kingSideCastle(currentSide);
					else if (square.column == 2)
						queenSideCastle(currentSide);
				} 
				if (selection.getPiece() instanceof Pawn
						&& square.getPiece() == null
						&& grid[selection.row][square.column].getPiece() instanceof Pawn
						&& grid[selection.row][square.column].getPiece().side != selection.getPiece().side) {
					System.out.println("Performing enPassant.");
					enPassant(selection, square);
				}

				// Standard move
				else{
					System.out.println("Performing standard move.");
					movePiece(selection, square);
				}
				deselectSquare(selection);

				// Reseting colors
				resetBoardColors();
				
				// Testing for end of game
				if (whiteTurn)
					victory(ChessPiece.WHITE);
				else
					victory(ChessPiece.BLACK);

				whiteTurn = !whiteTurn;
			}
		}

	}
	// -------------------------------------------------------------------------- End of Square Handling


	// -------------------------------------------------------------------------- Start of Selection Handling
	/**
	 * Selects the clicked square. This method uses the global variables:
	 * selection, selectionMoves
	 * 
	 * -- Refactor to remove the use of globals
	 * 
	 * @param square
	 *            The clicked square.
	 */
	private void selectSquare(Square square) {
		selection = square;
		// Highlight possible moves
		for (Square destination : square.getMoves()) {
			if (testMove(square, destination)) {
				selectionMoves.add(destination);
				destination.setBackground(availableColor);

				// Highlight castling square
				if (square.getPiece() instanceof King
						&& !((Castleable) square.getPiece()).hasMoved()
						&& destination.row == getBoardSide(square.getPiece().side)) {
					if (destination.column == 6 || destination.column == 2)
						destination.setBackground(castleColor);
				}

			}
		}
		// If the piece can't move, don't highlight it
		if (selectionMoves.size() != 0) {
			square.setBackground(selectColor);
		} else
			selection = null;

	}

	/**
	 * Deselects the currently selected square. This method uses globals
	 * selection and selectionMoves.
	 *
	 * -- Consider refactoring out globals.
	 * 
	 * @param square
	 *            The square to be deselected
	 */
	private void deselectSquare(Square square) {
		square.resetBackground();
		selection = null;
		for (Square s : selectionMoves) {
			s.resetBackground();
		}
		selectionMoves.clear();
	}

	/**
	 * Moves the origin Square's piece to the target square. The origin square
	 * is set to have no piece afterwards.
	 * 
	 * @param origin
	 *            Originating square.
	 * @param target
	 *            Target square.
	 */
	protected void movePiece(Square origin, Square target) {

//		System.out.println("Moving " + origin + " to " + target);

		moveHistory.add(new Square[] { (Square) origin.clone(),
				(Square) target.clone() });

		ChessPiece piece = origin.getPiece();
		// Checking Castleable
		if (piece instanceof Castleable)
			((Castleable) piece).setMoved();
		target.setPiece(piece);
		origin.setPiece(null);
	}

	// -------------------------------------------------------------------------- End of Selection Handling
	
	// -------------------------------------------------------------------------- Start of Check Handling

	/**
	 * Determines whether the given side has check mated their opponent.
	 * 
	 * @param side
	 *            ChessPiece.WHITE or ChessPiece.BLACK
	 * @return true if the given side has achieved a check mate.
	 */
	private boolean victory(int side) {
		
		boolean gameOver = false;
		
		Color winnerColour = null;
		int otherSide;
		// Getting the proper sides
		if (side == ChessPiece.WHITE) {
			winnerColour = Color.WHITE;
			otherSide = ChessPiece.BLACK;
		} else {
			winnerColour = Color.BLACK;
			otherSide = ChessPiece.WHITE;
		}
		// Testing check the checkMate
		// Consider refactoring to only test checkMate here
		if (testCheck(otherSide)) {
			gameOver = testCheckMate(otherSide);
		}
		// Set border to indicate check mate to user
		if (gameOver) {
			Border b = BorderFactory.createLineBorder(winnerColour, 10);
			this.setBorder(b);
		}
		return gameOver;
	}
	/**
	 * Tests whether the given side has been check mated. Check mate is defined
	 * such that there are no possible moves for the given side that do not
	 * result in being in check.
	 * 
	 * @param side
	 *            ChessPiece.WHITE or ChessPiece.BLACK.
	 * @return True if the given side is IN check mate.
	 */
	private boolean testCheckMate(int side) {

		boolean inCheckMate = true;

		// Getting pieces on the given side
		ArrayList<Square> pieces = new ArrayList<Square>();
		for (Square[] squares : grid) {
			for (Square s : squares) {
				if (s.getPiece() != null && s.getPiece().side == side)
					pieces.add(s);
			}
		}
		// Test if they can move
		for (Square s : pieces) {
			for (Square destination : s.getMoves()) {
				if (testMove(s, destination)) {
					// This is where I would grab the squares for highlight
					// options for moving out of check
					// System.out.println("Valid move: " + s + " to " +
					// grid[p.row][p.column]);
					inCheckMate = false;
				}
			}
		}

		return inCheckMate;

	}

	/**
	 * Tests whether the given side is in check. Check is defined as one or more
	 * opposing pieces being able to move to the Square the king currently
	 * occupies. This is tested from the King's position outward.
	 * 
	 * @param side
	 *            The given side to test for being in check.
	 * @return true if in check
	 */
	private boolean testCheck(int side) {
		Square kingPosition = null;
		boolean inCheck = false;
		// Finding the King
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 8; column++) {
				if (grid[row][column].getPiece() != null
						&& grid[row][column].getPiece() instanceof King
						&& grid[row][column].getPiece().side == side) {
					kingPosition = grid[row][column];
					break;
				}
			}
		}
		ArrayList<Square> threats = new ArrayList<Square>();
		threats.addAll(getThreats(kingPosition, side));
		for (Square s : threats)
			s.setBackground(checkColor);
		if (threats.size() != 0)
			inCheck = true;

		return inCheck;

	}
	// -------------------------------------------------------------------------- End of Check Handling
	
	// -------------------------------------------------------------------------- Start of Threat Handling
	private ArrayList<Square> getThreats(Square target, int side) {
		ArrayList<Square> threats = new ArrayList<Square>();
		threats.addAll(knightThreat(target, side));
		threats.addAll(pawnThreat(target, side));
		threats.addAll(lineThreat(target, side));
		return threats;
	}

	/**
	 * Analogous to moveKnight in Square. Checks all the possible ways a Knight
	 * could access the King's square. If any of the Squares contains an
	 * opposing Knight, the King is in check.
	 * 
	 * @param kingPosition
	 *            The position of the King.
	 * @return true if the king is in check from a Knight.
	 */
	private ArrayList<Square> knightThreat(Square kingPosition, int side) {

		int row = kingPosition.row;
		int column = kingPosition.column;

		// Possible moves
		ArrayList<int[]> moves = new ArrayList<int[]>();
		moves.add(new int[] { 1, 2 });
		moves.add(new int[] { 1, -2 });
		moves.add(new int[] { -1, 2 });
		moves.add(new int[] { -1, -2 });
		moves.add(new int[] { 2, 1 });
		moves.add(new int[] { 2, -1 });
		moves.add(new int[] { -2, 1 });
		moves.add(new int[] { -2, -1 });

		// Filtering valid moves
		ArrayList<Square> squares = new ArrayList<Square>();
		for (int[] move : moves) {
			if (isValid(row + move[0], column + move[1]))
				squares.add(grid[row + move[0]][column + move[1]]);
		}
		// Checking for check
		ArrayList<Square> threatSquares = new ArrayList<Square>();
		for (int i = 0; i < squares.size(); i++) {
			Square s = squares.get(i);
			// fairly sure checking null then instanceof is redundant
			if (s.getPiece() != null && s.getPiece() instanceof Knight
					&& s.getPiece().side != side) {
				threatSquares.add(s);
			}
		}

		return threatSquares;
	}

	/**
	 * Checks the possible ways a Pawn could attack the given square. This is
	 * necessary because a Pawn may move forward but it cannot capture a piece
	 * in doing so. This method only determines capturing moves.
	 * 
	 * If the given square is empty, then there is no possible capture. An
	 * ArrayList of size 0 is returned.
	 * 
	 * @param target
	 * @return
	 */
	private ArrayList<Square> pawnThreat(Square target, int side) {

		ArrayList<Square> validCaptures = new ArrayList<Square>();

		int adjust;
		// Getting direction
		if (side == ChessPiece.WHITE)
			adjust = -1;
		else
			adjust = 1;
		// Getting both possible moves
		Square square1 = null;
		if (isValid(target.row + adjust, target.column + adjust))
			square1 = grid[target.row + adjust][target.column + adjust];
		Square square2 = null;
		if (isValid(target.row + adjust, target.column - adjust))
			square2 = grid[target.row + adjust][target.column - adjust];

		if (square1.getPiece() instanceof Pawn
				&& square1.getPiece().side != side)
			validCaptures.add(square1);
		if (square2.getPiece() instanceof Pawn
				&& square2.getPiece().side != side)
			validCaptures.add(square2);

		return validCaptures;
	}

	/**
	 * Checks diagonals, horizontals, and verticals for check.
	 * 
	 * This is only meant to be used with the King's position. Any other call
	 * will result in a casting Exception.
	 * 
	 * @param kingPosition
	 *            Position of the King.
	 * @return true if the King is in check.
	 */
	private ArrayList<Square> lineThreat(Square kingPosition, int side) {

		int row = kingPosition.row;
		int column = kingPosition.column;

		Square conflictSquare = null;

		ArrayList<Square> squares = new ArrayList<Square>();

		// Possible lines
		int[][] diagonals = new int[][] { new int[] { 1, 1 },
				new int[] { 1, -1 }, new int[] { -1, 1 }, new int[] { -1, -1 } };
		int[][] rowCols = new int[][] { new int[] { 1, 0 },
				new int[] { -1, 0 }, new int[] { 0, -1 }, new int[] { 0, 1 } };
		// Diagonals
		for (int[] diag : diagonals) {
			conflictSquare = threatRecursive(row, column, diag[0], diag[1]);
			if (conflictSquare != null
					&& conflictSquare.getPiece().side != side
					&& (conflictSquare.getPiece() instanceof Queen || conflictSquare
							.getPiece() instanceof Bishop)) {
				squares.add(conflictSquare);
			}
		}
		// Rows & Columns
		for (int[] line : rowCols) {
			conflictSquare = threatRecursive(row, column, line[0], line[1]);
			if (conflictSquare != null
					&& conflictSquare.getPiece().side != side
					&& (conflictSquare.getPiece() instanceof Rook || conflictSquare
							.getPiece() instanceof Queen)) {
				squares.add(conflictSquare);
			}
		}

		return squares;
	}

	/**
	 * Analogous to moveRecursive in Square. Adjustments should be 1, 0, or -1.
	 * 
	 * Worker method for the "check" testing methods.
	 * 
	 * @param row
	 *            Originating Square's row.
	 * @param column
	 *            Originating Square's column.
	 * @param rowAdjust
	 *            Row adjustment.
	 * @param columnAdjust
	 *            Row adjustment.
	 * @return Following Square if the move is valid.
	 */
	private Square threatRecursive(int row, int column, int rowAdjust,
			int columnAdjust) {

		Square square = null;

		int currentRow = row + rowAdjust;
		int currentColumn = column + columnAdjust;
		if (isValid(currentRow, currentColumn)) {
			if (grid[currentRow][currentColumn].getPiece() == null)
				square = threatRecursive(currentRow, currentColumn, rowAdjust,
						columnAdjust);
			else
				square = grid[currentRow][currentColumn];
		}
		return square;
	}

	// -------------------------------------------------------------------------- End of Threat Handling
	
	// -------------------------------------------------------------------------- Start of Castling Handling
	/**
	 * Castling follows this logic: 1. Neither the king nor the chosen rook have
	 * been previously moved 2. There are no pieces between the king and the
	 * chosen rook. 3. The king is not currently in check. 4. The king does not
	 * pass through a square that is attacked by an enemy piece.
	 * 
	 * @param side
	 *            ChessPiece.WHITE or ChessPiece.BLACK
	 * @return true if move is possible.
	 */
	private boolean testKingSideCastle(int side) {

		int boardSide = getBoardSide(side);

		ChessPiece king = grid[boardSide][4].getPiece();
		ChessPiece rook = grid[boardSide][7].getPiece();

		// King is castleable
		boolean kingValid = king instanceof King && king.side == side
				&& !((Castleable) king).hasMoved() && !testCheck(side);
		// Rook is castleable
		boolean rookValid = rook instanceof Rook && rook.side == side
				&& !((Castleable) rook).hasMoved();
		// No square between the King and the Rook is threatened
		boolean betweenValid = grid[boardSide][5].getPiece() == null
				&& getThreats(grid[boardSide][5], side).size() == 0
				&& grid[boardSide][6].getPiece() == null
				&& getThreats(grid[boardSide][6], side).size() == 0;

		return kingValid && rookValid && betweenValid;
	}
	/**
	 * Castling follows this logic: 1. Neither the king nor the chosen rook have
	 * been previously moved 2. There are no pieces between the king and the
	 * chosen rook. 3. The king is not currently in check. 4. The king does not
	 * pass through a square that is attacked by an enemy piece.
	 * 
	 * @param side
	 *            ChessPiece.WHITE or ChessPiece.BLACK
	 * @return true if move is possible.
	 */
	private boolean testQueenSideCastle(int side) {

		int boardSide = getBoardSide(side);

		ChessPiece king = grid[boardSide][4].getPiece();
		ChessPiece rook = grid[boardSide][0].getPiece();

		// King is castleable
		boolean kingValid = king instanceof King && king.side == side
				&& !((Castleable) king).hasMoved() && !testCheck(side);
		// Rook is castleable
		boolean rookValid = rook instanceof Rook && rook.side == side
				&& !((Castleable) rook).hasMoved();
		// No square between the King and the Rook is threatened
		boolean betweenValid = grid[boardSide][3].getPiece() == null
				&& getThreats(grid[boardSide][3], side).size() == 0
				&& grid[boardSide][2].getPiece() == null
				&& getThreats(grid[boardSide][2], side).size() == 0;

		return kingValid && rookValid && betweenValid;
	}
	/**
	 * It is important that the rook move before the king.
	 * 
	 * @param side
	 */
	private void kingSideCastle(int side) {

		System.out.println("Castling King Side");

		int boardSide = getBoardSide(side);

		movePiece(grid[boardSide][7], grid[boardSide][5]);
		movePiece(grid[boardSide][4], grid[boardSide][6]);
	}

	/**
	 * It is important that the rook move before the king.
	 * 
	 * @param side
	 */
	private void queenSideCastle(int side) {
		int boardSide = getBoardSide(side);

		movePiece(grid[boardSide][0], grid[boardSide][3]);
		movePiece(grid[boardSide][4], grid[boardSide][2]);
	}
	// -------------------------------------------------------------------------- End of Castling Handling

	
	/**
	 * Tests whether the move results in check. A clone of the current board is
	 * made and the proposed move is applied. If the resulting board has the
	 * King in check, the move is rejected.
	 * 
	 * -- Consider bundling this in with getMoves().
	 * 
	 * @param origin
	 *            Originating Square.
	 * @param target
	 *            Target Square.
	 * @return true if the move does not result in a check.
	 */
	private boolean testMove(Square origin, Square target) {
		boolean validity = true;

		Chessboard checkBoard = (Chessboard) this.clone();
		checkBoard.movePiece(checkBoard.grid[origin.row][origin.column],
				checkBoard.grid[target.row][target.column]);

		if (checkBoard.testCheck(origin.getPiece().side))
			validity = false;

		return validity;
	}

	/**
	 * Performs the enPassant move. The logic is as follows: 1. The capturing
	 * pawn is on the 3rd row if White, 4th row if Black. 2. The captured pawn
	 * is on a column-adjacent square 3. The opponent's last move was to move
	 * the captured pawn forward two squares
	 * 
	 * @param origin
	 *            Capturing pawn.
	 * @param target
	 *            Empty square behind the captured pawn.
	 */
	private void enPassant(Square origin, Square target) {

		movePiece(origin, target);

		// This "impossible" scenario is occasionally being fired.
		// It is not producing errors, but should be investigated
		// if (!(grid[origin.row][target.column].getPiece() instanceof Pawn))
		// System.out.println("Error: Performing En Passant on non-Pawn piece.");

		grid[origin.row][target.column].setPiece(null);

	}
	
	/**
	 * Returns the row index for the corresponding side.
	 * @param side ChessPiece.WHITE or ChessPiece.BLACK
	 * @return 0 for Black, 7 for White.
	 */
	private int getBoardSide(int side) {
		int boardSide = 0;
		if (side == ChessPiece.WHITE)
			boardSide = 7;
		return boardSide;
	}
	/**
	 * Analogous to isValid in Square.
	 */
	private boolean isValid(int row, int column) {
		return row < 8 && column < 8 && row > -1 && column > -1;
	}
	
	/**
	 * Undoes the last move made. Implements a Stack and pops the top move.
	 * Moves are stored as an array of 2 Squares, the origin and target.
	 * 
	 * If there are no moves to undo, nothing happens.
	 */
	public void undo() {
		if (!moveHistory.isEmpty()) {
			Square[] move = moveHistory.pop();
			Square origin = move[0];
			Square target = move[1];

			grid[origin.row][origin.column].setPiece(origin.getPiece());
			grid[target.row][target.column].setPiece(target.getPiece());

			// Detecting castling
			// Assumes that King was moved after Rook during castling
			if (origin.getPiece() instanceof King
					&& Math.abs(origin.column - target.column) == 2) {
				whiteTurn = !whiteTurn;
				undo();
			}
			// Detecting En Passant
			else if (origin.getPiece() instanceof Pawn
					&& target.getPiece() == null
					&&
					// Moved diagonally
					Math.abs(origin.column - target.column) == 1
					&& Math.abs(origin.row - target.row) == 1) {
				System.out.println("Undoing En Passant.");
				int otherSide = ChessPiece.WHITE;
				if (origin.getPiece().side == ChessPiece.WHITE)
					otherSide = ChessPiece.BLACK;
				grid[origin.row][target.column].setPiece(new Pawn(otherSide));
			}

			whiteTurn = !whiteTurn;
		}
		
		// Reseting colors
		resetBoardColors();
		
		// Testing for end of game
		if (!whiteTurn)
			victory(ChessPiece.WHITE);
		else
			victory(ChessPiece.BLACK);

	}

	// -------------------------------------------------------------------------- Start of Utility Methods
	/**
	 * Reverts square colors to original color.
	 */
	private void resetBoardColors(){
		for (Square[] squares : grid)
			for (Square s : squares)
				s.resetBackground();
	}
	
	/**
	 * Private constructor solely for the purpose of cloning.
	 * 
	 * @param original
	 *            this board.
	 */
	private Chessboard(Chessboard original) {
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 8; column++) {
				this.grid[row][column] = (Square) original.grid[row][column]
						.clone();
			}
		}
	}

	public Chessboard clone() {
		return new Chessboard(this);
	}

	public String toString() {
		String output = "";
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 8; column++) {
				String rep = "[";
				if (grid[row][column].getPiece() != null) {
					rep += grid[row][column].getPiece();
				} else
					rep += "  " + row + " " + "," + " " + column + "   ";
				rep += "] ";
				output += rep;
			}
			output += "\n";
		}
		return output;
	}

	/**
	 * Utility method
	 */
	private void sleep() {
		sleep(1);
	}

	/**
	 * Utility method
	 * 
	 * @param s
	 *            Duration of pause in seconds.
	 */
	private void sleep(int s) {
		try {
			Thread.sleep(s * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	// -------------------------------------------------------------------------- End of Utility Methods
	
}
