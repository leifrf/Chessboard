import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class NQueens_Hill {
	
	private static int operations = 0;
	
	private static int FAIL_MAX = 2;
	
	private static final boolean PLACE = true;
	private static final boolean REMOVE = false;
	
	private final static String outputFolder = "C:/Users/Kevin/Documents/eclipse-standard-luna-R-win32-x86_64/eclipse/NQueens_Hill_Results";
	
	static int[][] board;
	static int[] queens;
	
	static int failCount;
	
	public static void main(String[] args){

		completeIterations(10, 300);
		
//		continueSolving();
		
//		ArrayList<Integer> choices = parsePlacementsSolution(new File("C:/Users/Kevin/Documents/eclipse-standard-luna-R-win32-x86_64/eclipse/NQueens_Hill_Results/Board_8.txt"));
//		verifySolutions();
		
	}
	
	private static void continueSolving(){
		File folder = new File(outputFolder);
		File[] solutions = folder.listFiles();
		int latest = 4;
		for (int i = 0; i < solutions.length; i++){
			String fileName = solutions[i].getName();
			int value = Integer.parseInt(fileName.substring(fileName.indexOf('_')+1,fileName.indexOf('.')));
			if (value > latest)
				latest = value;
		}
		completeIterations(latest + 1, latest + 101);
	}
	
	private static void completeIterations(int current, int max){
		while (current <= max){
			System.out.print("Starting board: " + current + " ... ");
			long elapsed = System.currentTimeMillis();
			
			boolean solutionFound = false;
			while (!solutionFound){
				initializeBoard(current);
				solutionFound = algorithm();
			}
			
			elapsed = System.currentTimeMillis() - elapsed;
			writePlacements(new File(outputFolder + "/Board_"+current+".txt"), elapsed);
			System.out.println(" ... " + "Finished board " + current + ", Operations: " + operations + ", Seconds: " + elapsed / 1000);
			current += 1;
			operations = 0;
		}
	}
	
	private static void initializeBoard(int size){
		board = new int[size][size];
		queens = new int[size];
		Random rand = new Random();
		for (int row = 0; row < board.length; row++){
//			printGrid(board);
//			sleep();
			queens[row] = rand.nextInt(board.length);
			updateConflicts(row, queens[row], PLACE);
		}
	}
	
	// Don't keep this name
	private static boolean algorithm(){
		
		while (sumConflicts() != 0 && failCount != FAIL_MAX){
			moveQueen();
		}
//		System.out.println("Algorithm complete. Conflicts: " + sumConflicts());
//		printGrid(board);
//		sleep(1);
		failCount = 0;
		if (sumConflicts() != 0)
			return false;
		else
			return true;
	}
	
	private static int sumConflicts(){
		int sumConflicts = 0;
		for (int i = 0; i < queens.length; i++)
			sumConflicts += board[i][queens[i]];
		return sumConflicts;
	}
	
	static int previousConflicts = 0;
	
	private static void moveQueen(){
		
		int[] rowColumn = weightedSelection();
		int row = rowColumn[0];
		int column = rowColumn[1];
		
//		System.out.println("Remove Index: [" + row + "][" + column + "]");
		
		// Remove that queen
		updateConflicts(row, column, REMOVE);

		// Find the minimum conflicting spot in that row
		int minConflicts = board[row][0];
		for (int i = 1; i < queens.length; i++){
//			System.out.println("row value: " + board[row][i]);
			if (board[row][i] < minConflicts){
				minConflicts = board[row][i];
				column = i;
			}
		}
		
//		System.out.println("Place Index: [" + row + "][" + column + "]");
		// Place it in the best spot in that row
		updateConflicts(row, column, PLACE);
		queens[row] = column;
		

//		sleep(1);
//		System.out.println();
//		printGrid(board);
		
		if (previousConflicts == sumConflicts())
			failCount++;
		previousConflicts = sumConflicts();
	}
	
	/**
	 * Each row's queen has a certain number of conflicts
	 * give each queen a probability of being chosen based on the conflict count
	 */
	private static int[] weightedSelection(){
		int conflicts = sumConflicts();
		int[] probabilities = new int[queens.length];
		probabilities[0] = 0;
		for (int i = 1; i < board.length; i++){
			probabilities[i] = probabilities[i-1] + board[i-1][queens[i-1]];
		}

//		System.out.println("Conflicts: " + conflicts);
//		System.out.println(Arrays.toString(probabilities));
		
		Random rand = new Random();
		int value = rand.nextInt(conflicts);
//		System.out.println("Value: " + value + ", Conflicts: " + conflicts);
//		System.out.println(Arrays.toString(probabilities));
		for (int i = 1; i < probabilities.length; i++){
			if (value <= probabilities[i])
				return new int[]{i - 1, queens[i - 1]};
		}
		
		// this line should never be reached
		return new int[]{queens.length - 1,queens[queens.length-1]};
	}
	
	private static void sleep(){
		sleep(1);
	}
	
	private static void sleep(int seconds){
		try {
			Thread.sleep(1000 * seconds);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * This needs to both increment and decrement
	 * realistically, this should be two separate methods 
	 * @param row
	 * @param column
	 */
	private static void updateConflicts(int row, int column, boolean placed){
		
//		printGrid(grid);
//		System.out.println();
		operations++;
		
		int amount = 1;
		if (!placed)
			amount = - 1;
		
		// Update row
		for (int i = 0; i < board.length; i++)
			board[i][column] += amount;
		// Update column
		for (int i = 0; i < board.length; i++)
			board[row][i] += amount;
		// Update first diagonal
		int position = Math.min(board.length - row - 1, column);
		int newRow = row + position;
		int newColumn = column - position;
//		System.out.println("Row: " + row + " Column: " + column + " Position: " + position);
		while(newRow != -1 && newRow != board.length &&
				newColumn != board.length && newColumn != - 1){
//			System.out.println("newRow: " + newRow + " newColumn: " + newColumn);
			board[newRow--][newColumn++] += amount;
		}
		// Update second diagonal
		position = Math.min(row, column);
		newRow = row - position;
		newColumn = column - position;
//		System.out.println("Row: " + row + " Column: " + column + " Position: " + position);
		while(newRow != board.length && newColumn != board.length){
//			System.out.println("newRow: " + newRow + " newColumn: " + newColumn);
			board[newRow++][newColumn++] += amount;
		}
		
		// Decrement 4 for each update
//			System.out.println("decrementing");
			board[row][column] -= 4 * amount;

	}
	
	public static int[][] parseBoardSolution(File f){
		
		return null;
	}
	
	public static ArrayList<Integer> parsePlacementsSolution(File f){
		ArrayList<Integer> placements = new ArrayList<Integer>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			String line = reader.readLine();
			line = reader.readLine();
			String num;
			int column;
			while(line.length() != 0){
				num = line.substring(0, line.indexOf("\t"));
				column = Integer.parseInt(num);
				placements.add(column);
				line = line.substring(line.indexOf("\t") + 1);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return placements;
	}
	
	static final int QUEEN = 2;
	static final int INVALID = 1;
	static final int VALID = 0;
	
	private static boolean verifySolution(ArrayList<Integer> placements){
		
		int[][] grid = new int[placements.size()][placements.size()];
		int i = 0;
		while(updateGrid(grid, i, placements.get(i))){
			if (i == placements.size() - 1){
//				System.out.println("Solution for board " + placements.size() + " verified.");
				return true;
			}
			i++;
		}
		System.out.println("Solution for board " + placements.size() + " is wrong.");
		return false;
	}
	
	public static void verifySolutions(){
		File folder = new File(outputFolder);
		File[] solutions = folder.listFiles();
		ArrayList<Integer> solution;
		int errors = 0;
		for (int i = 0; i < solutions.length; i++){
			solution = parsePlacementsSolution(solutions[i]);
			if(!verifySolution(solution))
				errors++;
		}
		System.out.println("Verification complete with " + errors + " incorrect solutions.");
	}
	
	private static boolean updateGrid(int[][] grid, int row, int column){

		//Check current square
		if (grid[row][column] == INVALID){
			System.out.println("Trying to place Queen on invalid square.");
			return false;
		}
		//If vacant, place a queen here
		else grid[row][column] = QUEEN;

		//Iterator variables
		int updateRow;
		int updateColumn;		
		
		//Update horizontal
		updateRow = row + 1;
		updateRow %= grid.length;
		while (updateRow != row){
			//Conflict found
			if (grid[updateRow][column] == QUEEN){
				System.out.println("Looking at position: (" + updateRow + ", " + column + ")");
				System.out.println("Queen encountered on horizontal axis.");
				return false;
			}
			//No conflict - update square as invalid
			grid[updateRow][column] = INVALID;
			
			updateRow+= 1;
			updateRow %= grid.length;
		}
		
		
		//Update vertical
		updateColumn = column + 1;
		updateColumn %= grid.length;
		while (updateColumn != column){
			//Conflict found
			if (grid[row][updateColumn] == QUEEN){
				System.out.println("Queen encountered on vertical axis.");
				return false;
			}
			//No conflict - update square as invalid
			grid[row][updateColumn] = INVALID;
			updateColumn++;
			updateColumn %= grid.length;
		}
		
		
		//Update 0,0 to n,n diagonal
		//Row is used as the terminating condition
		//Column would be equally valid
		updateRow = row + 1;
		updateColumn = column + 1;
		
		//Handling off-by-one error
		if (updateRow == grid.length || updateColumn == grid.length){
			int temp = updateRow - Math.min(updateRow, updateColumn);
			updateColumn -= Math.min(updateRow, updateColumn);
			updateRow = temp;
		}
		
		while (updateRow != row){
		
			//Conflict found
			if (grid[updateRow][updateColumn] == QUEEN){
				System.out.println("Queen encountered on top->down diagonal.");
				return false;
			}
			//No conflict - update square as invalid
			grid[updateRow][updateColumn] = INVALID;
			
			//Iterate
			updateRow += 1;
			updateColumn += 1;
			
			//Handling off-by-one error			
			if (updateRow == grid.length || updateColumn == grid.length){
				int temp = updateRow - Math.min(updateRow, updateColumn);
				updateColumn -= Math.min(updateRow, updateColumn);
				updateRow = temp;
			}			
			
		}
		
		
		 
		//Update n,0 to 0,n diagonal
		//Row is used as the terminating condition
		//Column would be equally valid
		updateRow = row + 1;
		updateColumn = column - 1;
		//Handling off-by-one error
		if (updateRow == grid.length || updateColumn == -1){ 
			int temp = updateRow - 1;
			updateColumn+= 1;
			updateRow = updateColumn;
			updateColumn = temp;
		}
		
		while (updateRow != row){
			
			//Conflict found
			if (grid[updateRow][updateColumn] == QUEEN){
				System.out.println("Queen encountered on down->top diagonal.");				
				return false;
			}
			grid[updateRow][updateColumn] = INVALID;

			//Iterate
			updateRow += 1;
			updateColumn -= 1;
			
			//Handling off-by-one error
			if (updateRow == grid.length || updateColumn == -1){ 
				int temp = updateRow - 1;
				updateColumn+= 1;
				updateRow = updateColumn;
				updateColumn = temp;
			}
		}
		
		return true;
	}
	
	public static int[][] copy2D(int[][] grid){
		int[][] newGrid = new int[grid.length][grid[0].length];
		
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[0].length; j++)
				newGrid[i][j] = grid[i][j];
		
		return newGrid;
	}
	
	public static int[][] randomGrid(int dimension){
		int[][] grid = new int[dimension][dimension];
		Random rand = new Random();
		for (int i = 0; i < dimension; i++)
			for (int j = 0; j < dimension; j++)
				grid[i][j] = rand.nextInt(dimension);
		
		return grid;
	}
	
	public static void printGrid(int[][] grid){
		
		for (int i = 0; i < grid.length; i++){
			System.out.print("[");
			for (int j = 0; j < grid[0].length - 1; j++)
				System.out.print(grid[i][j] + ", ");
			System.out.println(grid[i][grid[0].length - 1] + "]");				
		}
				
		
	}
	
	public static void printGridCSV(int[][] grid){
		
		for (int i = 0; i < grid.length; i++){
			for (int j = 0; j < grid[0].length; j++)
				System.out.print(grid[i][j] + "\t");
			System.out.println();				
		}
				
		
	}


	public static void write2DCSV(int[][] grid, File f){
		try {
			PrintWriter writer = new PrintWriter(f);
			for (int i = 0; i < grid.length; i++){
				for (int j = 0; j < grid.length; j++){
					writer.print(grid[i][j] + "\t");
					System.out.print(grid[i][j] + "\t");
				}
				writer.println();
				System.out.println();
			}
			System.out.println("----------------");
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	
	public static void writeBoard(File f, long elapsedTime){

		try {
			PrintWriter writer = new PrintWriter(f);
			writer.println("Board Size: " + queens.length + " Operations: " + operations + " Time: " + elapsedTime);
			for (int i = 0; i < board.length; i++){
				for (int j = 0; j < board.length; j++){
					writer.print(board[i][j] + "\t");
				}
				writer.println();
			}
			writer.println();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void writePlacements(File f, long elapsedTime){

		try {
			PrintWriter writer = new PrintWriter(f);
			writer.println("Board Size: " + queens.length + " Operations: " + operations + " Time: " + elapsedTime);
			for (int i = 0; i < queens.length; i++){
				writer.print(queens[i] + "\t");
			}
			writer.println();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	
}