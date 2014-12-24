import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

public class NQueens_BFI {
	
//	private final static int n = 8;
	private final static int QUEEN = 2;
	private final static int VALID = 0;
	private final static int INVALID = 1;

	private static int operations = 0;
	
	private final static String outputFolder = "C:/Users/Kevin/Documents/eclipse-standard-luna-R-win32-x86_64/eclipse/NQueens_BFI_Results";
	
	static ArrayList<int[][]> boards = new ArrayList<int[][]>();
	
	public static void main(String[] args){

		completeIterations(3, 100);

	}
		
	private static void completeIterations(int i, int n){
		int[][] grid;
		while (i <= n){
			System.out.print("Starting board: " + i + " ... ");
			long elapsed = System.currentTimeMillis();
			grid = new int[i][i];
			// Change this line for difference between BFI & Hill
			updateBFI(grid, 0);
			writeBoards(new File(outputFolder + "/Board_"+i+".txt"));
			elapsed = System.currentTimeMillis() - elapsed;
			System.out.println(" ... " + "Finished board " + i + ", Operations: " + operations + ", Seconds: " + elapsed / 1000);
			i += 1;
			boards = new ArrayList<int[][]>();
			solutionFound = false;
			operations = 0;
		}
	}
	
	private static boolean solutionFound = false;
	private static void updateBFI(int[][] grid, int row){
		
		//Successful placement of n queens
		if (row == grid.length){
			boards.add(grid);
			
			//Stops at first solution
			solutionFound = true;
			return;
		}
		
		//Propagating instances
		int[][] grid2;
		for (int column = 0 ; column < grid.length && !solutionFound; column++){
			grid2 = copy2D(grid);
			if (grid2[row][column] == VALID){
				grid2 = placeQueen(grid2, row, column);
				//Conflict found upon queen placement				
				if (grid2 == null){
					// --- this line is never reached ---
					// need to incorporate this to remove the necessity of pruning
					continue;	//Continue on to the next possible placement
				}
				updateBFI(grid2, row + 1);
			}
		}
		
		//Iteration failed		
	}
	
	/**Assumes that there is not already a Queen in this square
	 * follows row-based execution of overall algorithm.
	 * 
	 * @param grid
	 * @param row
	 * @param column
	 * @return
	 */
	private static int[][] placeQueen(int[][] grid, int row, int column){
		
//		printGrid(grid);
//		System.out.println();
		operations++;
		
		//Check current square
		if (grid[row][column] == INVALID){
			System.out.println("Trying to place Queen on invalid square.");
			return null;
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
				return null;
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
				return null;
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
				return null;
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
				return null;
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
		
		return grid;
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

	private static boolean sumCheck(int[][] grid){
		int sum = 0;
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid.length; j++)
				sum += grid[i][j];
		int properSum = INVALID * grid.length * grid.length + (QUEEN - INVALID) * grid.length;
		if (sum != properSum)
			return false;
		else
			return true;
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

	public static void writeBoards(File f){

		try {
			PrintWriter writer = new PrintWriter(f);
			for (int[][] grid : boards){
				for (int i = 0; i < grid.length; i++){
					for (int j = 0; j < grid.length; j++){
						writer.print(grid[i][j] + "\t");
					}
					writer.println();
				}
			writer.println();
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}