import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class ChessGame {

	static Chessboard board = new Chessboard();

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		ChessGame game = new ChessGame();
	}
	
	public ChessGame(){
		JFrame chessFrame = new JFrame();
		
		chessFrame.setJMenuBar(new ChessMenu());
		chessFrame.add(board);
		chessFrame.pack();
		chessFrame.setTitle("Leif Raptis Firth - Simple Chess Game");
		chessFrame.setVisible(true);
		chessFrame.setMinimumSize(new Dimension(600, 600));
		chessFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		chessFrame.setLocationRelativeTo(null);
	}

	private class UndoListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			board.undo();
		}
	}

	private class ChessMenu extends JMenuBar{
		private static final long serialVersionUID = 1L;
		
		private JMenu menu = new JMenu("File");
		private JMenuItem undo = new JMenuItem("Undo");
		
		protected ChessMenu(){
			add(menu);
			menu.add(undo);
			undo.setAccelerator(KeyStroke.
					getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
			undo.addActionListener(new UndoListener());
		}
	}

}
