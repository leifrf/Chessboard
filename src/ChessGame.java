import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class ChessGame {

	
	public static void main(String[] args){
		System.out.println("in ChessGame");
		JFrame chessFrame = new JFrame();
		Chessboard board = new Chessboard();
		
		JPanel panel = new JPanel();
		panel.add(new JButton("TEST BUTTON"));
		panel.setBackground(Color.DARK_GRAY);
		
		chessFrame.setTitle("Chessboard");
		chessFrame.setLayout(new BorderLayout());
		chessFrame.add(board, BorderLayout.CENTER);
		chessFrame.add(panel,BorderLayout.EAST);
		chessFrame.setVisible(true);
//		chessFrame.setSize(800,800);
		chessFrame.pack();
		chessFrame.setMinimumSize(new Dimension(600, 600));
		chessFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		chessFrame.setLocationRelativeTo(null);
		
	}
}
