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

	private class MenuListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String sourceName = ((JMenuItem)e.getSource()).getText();
			if (sourceName.equals("Undo"))
				board.undo();
			else if (sourceName.equals("Save"))
				System.out.println("Saving.");
			else if (sourceName.equals("New Game")){
				System.out.println("New Game.");
			}
		}
	}

	private class ChessMenu extends JMenuBar{
		private static final long serialVersionUID = 1L;
		
		private JMenu menu = new JMenu("File");
		private JMenuItem undo = new JMenuItem("Undo");
		private JMenuItem save = new JMenuItem("Save");
		private JMenuItem newGame = new JMenuItem("New Game");
		private MenuListener listener = new MenuListener();
		
		protected ChessMenu(){
			add(menu);
			
			menu.add(undo);
			undo.setAccelerator(KeyStroke.
					getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
			undo.addActionListener(listener);
			
			menu.add(save);
			save.setAccelerator(KeyStroke.
					getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
			save.addActionListener(listener);
			
			menu.add(newGame);
			newGame.setAccelerator(KeyStroke.
					getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
			newGame.addActionListener(listener);
		}
	}

}
