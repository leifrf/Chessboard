import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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


public class Chessboard extends JPanel{

	Square[][] grid = new Square[8][8];
	Color selectColor = Color.RED;
	Square selection;
	
	public static void main(String[] args){
		JFrame frame = new JFrame();
		Chessboard board = new Chessboard();
		frame.setTitle("Chessboard");
		frame.add(board);
//		Chessboard c = new Chessboard();
//		frame.add(c.new TestPanel());
		
		frame.setVisible(true);
		frame.setSize(800,800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public Chessboard(){
		this(Color.gray, Color.white);
	}
	
	public Chessboard(Color color1, Color color2){
		//Right now, initializeBoard and initializeSides
		//Overlaps a bit on work. can improve efficiency
		//reduces understandability
		initializeBoard(color1, color2);
		initializeSide(ChessPiece.WHITE);
		initializeSide(ChessPiece.BLACK);
	}
	
	private void initializeBoard(Color color1, Color color2){
		int size = grid.length;
		GridLayout layout = new GridLayout(size, size);
		this.setLayout(layout);
		grid = new Square[size][size];
		Color currentColor = color1;
		for (int row = 0; row < size; row++){
			for (int column = 0; column < size; column++){
				if (((row + column) & 1) == 1)
					currentColor = color1;
				else
					currentColor = color2;
				grid[row][column] = new Square(currentColor);
				grid[row][column].addActionListener(new SquareListener());
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
	
	private class Square extends JButton{
		
		private ChessPiece piece;
		private Color bkg;
		
		public Square(Color color){
			super();
			super.setBackground(color);
			this.bkg = color;
		}
		
		public Square(ChessPiece piece, Color bkg){
			super();
			super.setBackground(bkg);
			setPiece(piece);
			this.bkg = bkg;
		}
		
		public void setPiece(ChessPiece piece){
			super.setIcon(piece.getIcon());
			this.piece = piece;
		}
		
		public ChessPiece getPiece(){
			return this.piece;
		}
		
		public void removePiece(){
			this.piece = null;
		}
		
		public void addPiece(ChessPiece piece){
			if(this.piece == null)
				this.piece = piece;
		}
		
		public void resetBackground(){
			super.setBackground(bkg);
		}
	}

	private class SquareListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			Square square = (Square)e.getSource();
			if(selection == null){
				selection = square;
				square.setBackground(selectColor);
			}
			else if(selection == square){
				selection = null;
				square.resetBackground();
			}
		}
		
	}









}