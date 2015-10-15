package Version7;

import java.awt.*;
import java.util.*;

/* This class holds the current instance of the Tetris game. When requested, it sends to TetrisPanel
 * the current configuration of the board excluding the current piece in motion, the window displaying
 * the next piece, and the window displaying the held piece. It is responsible for storing the current
 * level and number of rows cleared but not for incrementing the speed (TetrisPanel does that). */

public class Tetris {
	
	public static final int height = 22;	//Size of the grid
	public static final int width = 10;
	public static final int windowSize = 6;	//Size of next and hold window grids
	
	private int level = 0;	//Level of the current game
	private int rowsCleared = 0;	//Number of rows cleared in the current game
	
	public int[][] board;	//array for the board
	public int[][] nextWindow;	//array for the window containing the next piece
	public int[][] holdWindow;	//array for the window containing the currently held piece
	
	public Tetris()	//Creates an instance of Tetris
	{
		board = new int[height][width];
		nextWindow = new int[windowSize][windowSize];
		holdWindow = new int[windowSize][windowSize];
	}
	
	public int getLevel()	//Gets the current level
	{
		return level;
	}
	
	public int getRowsCleared()	//Gets the number of rows cleared
	{
		return rowsCleared;
	}

	public void displayBoard(Graphics g, int w, int h)	//draws the current configuration of the board array with Graphics g
	{	
		for (int r = 2; r < height; r++)
		{
			for (int c = 0; c < width; c++)
			{
				switch (board[r][c])	//find the color of the square
				{
					case 0:
						g.setColor(Color.BLACK);
						break;
					case 1:
						g.setColor(Color.CYAN);
						break;
					case 2:
						g.setColor(Color.BLUE);
						break;
					case 3:
						g.setColor(new Color(16737792));
						break;
					case 4:
						g.setColor(Color.YELLOW);
						break;
					case 5:
						g.setColor(Color.GREEN);
						break;
					case 6:
						g.setColor(Color.MAGENTA);
						break;
					case 7:
						g.setColor(Color.RED);
						break;
				}
				
				g.fillRect(c*(h/25)+ w/2-width/2*(h/25), r*(h/25)+h/50, h/25, h/25);	//Fill the square
			}
		}
	}
	
	public void drawLines(Graphics g, int w, int h)	//Draw the outside border and grid for pieces
	{
		g.setColor(Color.WHITE);
		g.drawLine(w/2 - width/2*(h/25), h/10, w/2 - width/2*(h/25), h/10 + 20*(h/25));
		g.drawLine(w/2 + width/2*(h/25), h/10, w/2 + width/2*(h/25), h/10 + 20*(h/25));
		g.drawLine(w/2 - width/2*(h/25), h/10, w/2 + width/2*(h/25) , h/10);
		g.drawLine(w/2 - width/2*(h/25), h/10 + (height-2)*(h/25), w/2 + width/2*(h/25), h/10 + (height-2)*(h/25));
		for (int r = 2; r < 22; r++)
		{
			for (int c = 0; c < 10; c++)
			{
				if (board[r][c] == 0)
					continue;
				g.drawRect(c*(h/25)+ w/2-5*(h/25), r*(h/25)+h/50, h/25, h/25);
			}
		}
	}
	
	public void clearRows()	//Checks to see if a row is filled all the way, clears it, drops higher rows down
	{
		boolean[] rowsToClear = new boolean[23];
		boolean moreRows = checkClear(rowsToClear);
		while (moreRows)
		{
			for (int r = 2; r < 22; r++)
				if (rowsToClear[r])
				{
					for (int c = 0; c < 10; c++)
						board[r][c] = 0;
					for (int rw = r; rw >= 2; rw--)
					{
						for (int c = 0; c < 10; c++)
							board[rw][c] = board[rw-1][c];
					}
					rowsCleared++;
				}
			moreRows = checkClear(rowsToClear);
		}
		levelUp();
	}
	
	private void levelUp()	//Increases the level
	{
		if (rowsCleared >= 10 && level <4)	//If you pass a certain number of rows cleared, increase the speed
		{
			level++;
			rowsCleared -= 10;
			System.out.println("Level " + (level+1));
		}	
	}
	
	private boolean checkClear(boolean[] rows)	//Checks if a row is filled all the way across
	{
		boolean haveMoreRows = false;
		rows[0] = true;
		rows[1] = true;
		rows[22] = true;
		for (int r = 2; r < 22; r++)
		{
			rows[r] = true;
			for (int c = 0; c < 10; c++)
				if (board[r][c] == 0)
				{
					rows[r] = false;
					break;
				}
			if (rows[r])
				haveMoreRows = true;
		}
		
		return haveMoreRows;
	}
	
	public boolean lostGame()	//Checks if this instance of Tetris is over
	{
		for (int c = 0; c < 10; c++)
		{
			if (board[0][c] != 0 || board[1][c] != 0)
				return true;
		}
		return false;
	}
	
	public void displayNextWindow(Graphics g, int type, int w, int h)	//Draw the window showing the next piece
	{
		fillNextWindow(type);
		for (int r = 0; r < windowSize; r++)
		{
			for (int c = 0; c < windowSize; c++)
			{
				switch (nextWindow[r][c])	//find the color of the square
				{
					case 0:
						g.setColor(Color.BLACK);
						break;
					case 1:
						g.setColor(Color.CYAN);
						break;
					case 2:
						g.setColor(Color.BLUE);
						break;
					case 3:
						g.setColor(new Color(16737792));
						break;
					case 4:
						g.setColor(Color.YELLOW);
						break;
					case 5:
						g.setColor(Color.GREEN);
						break;
					case 6:
						g.setColor(Color.MAGENTA);
						break;
					case 7:
						g.setColor(Color.RED);
						break;
				}
				g.fillRect(c*(h/50)+ (w/2) + width/2*(h/25), r*(h/50) + h/10, h/50, h/50);	//Fill the square
			}
		}
		g.setColor(Color.WHITE);
		for (int r = 0; r < windowSize; r++)
			for (int c = 0; c < windowSize; c++)
				if (nextWindow[r][c] !=0)
				{
					g.drawRect(c*(h/50)+ (w/2) + width/2*(h/25), r*(h/50)+ h/10, h/50, h/50);
				}
		g.drawRect(width/2*(h/25)+ (w/2), h/10, 6*h/50, 6*h/50);
	}
		
	private void fillNextWindow(int type)	//Copies a type of piece into the next window array
	{
		int[][] config = TetrisPiece.configurations.get(new Integer(type*10));
		for (int r = 1; r < 5; r++)
		{
			for (int c = 1; c < 5; c++)
				nextWindow[r][c] = config[r-1][c-1];
		}
	}
	
	public void displayHoldWindow(Graphics g, int type, int w, int h)	//Draw the hold window
	{
		fillHoldWindow(type);
		for (int r = 0; r < windowSize; r++)
		{
			for (int c = 0; c < windowSize; c++)
			{
				switch (holdWindow[r][c])	//find the color of the square
				{
					case 0:
						g.setColor(Color.BLACK);
						break;
					case 1:
						g.setColor(Color.CYAN);
						break;
					case 2:
						g.setColor(Color.BLUE);
						break;
					case 3:
						g.setColor(new Color(16737792));
						break;
					case 4:
						g.setColor(Color.YELLOW);
						break;
					case 5:
						g.setColor(Color.GREEN);
						break;
					case 6:
						g.setColor(Color.MAGENTA);
						break;
					case 7:
						g.setColor(Color.RED);
						break;
				}
				g.fillRect(c*(h/50)+ (w/2) + width/2*(h/25), r*(h/50) + h/10 + width/2*(h/25), h/50, h/50);	//Fill the square
			}
		}
		g.setColor(Color.WHITE);
		for (int r = 0; r < windowSize; r++)
			for (int c = 0; c < windowSize; c++)
				if (holdWindow[r][c] !=0)
				{
					g.drawRect(c*(h/50)+ (w/2) + width/2*(h/25), r*(h/50)+ h/10 + width/2*(h/25), h/50, h/50);
				}
		g.drawRect(width/2*(h/25)+ (w/2), width/2*(h/25)+ h/10, 6*h/50, 6*h/50);
	}
	
	private void fillHoldWindow(int type)	//Copies an image of a piece type to the hold window array
	{
		if (type != -1)
		{
			int [][] config = TetrisPiece.configurations.get(new Integer(type*10));
			for (int r = 1; r < 5; r++)
			{
				for (int c = 1; c < 5; c++)
					holdWindow[r][c] = config[r-1][c-1];
			}
		}
	}
}
