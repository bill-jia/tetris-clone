package Version7;

import java.awt.*;
import java.util.*;

/*
 * This class is responsible for holding information about the different types of pieces in Tetris
 * and the information for an individual piece in motion. It checks collisions every time the piece
 * moves and sends the result back to TetrisPanel. It responds to the user's requests to move or
 * rotate the current falling piece via TetrisPanel. It is responsible for telling a piece when
 * to lock into the board of the current instance of Tetris.
 */

public class TetrisPiece {
	private int type;	//Tetramino shape
	private int row, col;	//the row and column of the top-left corner of the grid 
	private int pieceID;	//Piece ID number
	private boolean finalLock = false;	//Is the piece locked into the Tetris game
	private Timer lockTimer;	//Timer for refreshing the lock
	
	public static HashMap<Integer, int[][]> configurations = new HashMap<Integer, int[][]>();	//static hashmap used as reference for all classes in this program. Since each piece type has a unique colour, the integers for types are also referenced for colour.
	static {	//Hard code the types of pieces and their rotations
		configurations.put(new Integer(10), new int[][] {{0,0,0,0}, {1,1,1,1}, {0,0,0,0}, {0,0,0,0}});
			configurations.put(new Integer(11), new int[][] {{0,0,1,0}, {0,0,1,0}, {0,0,1,0}, {0,0,1,0}});
			configurations.put(new Integer(12), new int[][] {{0,0,0,0}, {0,0,0,0}, {1,1,1,1}, {0,0,0,0}});
			configurations.put(new Integer(13), new int[][] {{0,1,0,0}, {0,1,0,0}, {0,1,0,0}, {0,1,0,0}});
		
		configurations.put(new Integer(20), new int[][] {{0,0,0,0}, {2,0,0,0}, {2,2,2,0}, {0,0,0,0}});
			configurations.put(new Integer(21), new int[][] {{0,0,0,0}, {0,2,2,0}, {0,2,0,0}, {0,2,0,0}});
			configurations.put(new Integer(22), new int[][] {{0,0,0,0}, {0,0,0,0}, {2,2,2,0}, {0,0,2,0}});
			configurations.put(new Integer(23), new int[][] {{0,0,0,0}, {0,2,0,0}, {0,2,0,0}, {2,2,0,0}});
		
		configurations.put(new Integer(30), new int[][] {{0,0,0,0}, {0,0,3,0}, {3,3,3,0}, {0,0,0,0}});
			configurations.put(new Integer(31), new int[][] {{0,0,0,0}, {0,3,0,0}, {0,3,0,0}, {0,3,3,0}});
			configurations.put(new Integer(32), new int[][] {{0,0,0,0}, {0,0,0,0}, {3,3,3,0}, {3,0,0,0}});
			configurations.put(new Integer(33), new int[][] {{0,0,0,0}, {3,3,0,0}, {0,3,0,0}, {0,3,0,0}});
		
		configurations.put(new Integer(40), new int[][] {{0,0,0,0}, {0,4,4,0}, {0,4,4,0}, {0,0,0,0}});
			configurations.put(new Integer(41), new int[][] {{0,0,0,0}, {0,4,4,0}, {0,4,4,0}, {0,0,0,0}});
			configurations.put(new Integer(42), new int[][] {{0,0,0,0}, {0,4,4,0}, {0,4,4,0}, {0,0,0,0}});
			configurations.put(new Integer(43), new int[][] {{0,0,0,0}, {0,4,4,0}, {0,4,4,0}, {0,0,0,0}});
			
		configurations.put(new Integer(50), new int[][] {{0,0,0,0}, {0,5,5,0}, {5,5,0,0},{0,0,0,0}});
			configurations.put(new Integer(51), new int[][] {{0,0,0,0}, {0,5,0,0}, {0,5,5,0}, {0,0,5,0}});
			configurations.put(new Integer(52), new int[][] {{0,0,0,0}, {0,5,5,0}, {5,5,0,0},{0,0,0,0}});
			configurations.put(new Integer(53), new int[][] {{0,0,0,0}, {0,5,0,0}, {0,5,5,0}, {0,0,5,0}});
			
		configurations.put(new Integer(60), new int[][] {{0,0,0,0}, {0,6,0,0}, {6,6,6,0}, {0,0,0,0}});
			configurations.put(new Integer(61), new int[][] {{0,0,0,0}, {0,6,0,0}, {0,6,6,0}, {0,6,0,0}});
			configurations.put(new Integer(62), new int[][] {{0,0,0,0}, {0,0,0,0}, {6,6,6,0}, {0,6,0,0}});
			configurations.put(new Integer(63), new int[][] {{0,0,0,0}, {0,6,0,0}, {6,6,0,0}, {0,6,0,0}});
		
		configurations.put(new Integer(70), new int[][] {{0,0,0,0}, {7,7,0,0}, {0,7,7,0}, {0,0,0,0}});
			configurations.put(new Integer(71), new int[][] {{0,0,0,0}, {0,0,7,0}, {0,7,7,0}, {0,7,0,0}});
			configurations.put(new Integer(72), new int[][] {{0,0,0,0}, {7,7,0,0}, {0,7,7,0}, {0,0,0,0}});
			configurations.put(new Integer(73), new int[][] {{0,0,0,0}, {0,0,7,0}, {0,7,7,0}, {0,7,0,0}});
	}
	
	public TetrisPiece (int t, int id, int o)	//Constructor
	{
		type = t*10 + o;
		row = -1;
		col = 3;
		pieceID = id;
		lockTimer = new Timer();
	}
	
	public void fall(int [][] board)	//Fall based on time
	{
		if (noCollisions(board, type, row + 1, col))
			row++;
	}
	
	public void shiftL(int [][] board)	//Move one unit to the left
	{
		if (noCollisions(board, type, row, col - 1))
		{
			col--;
			lockTimer.cancel();	//Upon successful movement, reset lock timer
			lockTimer = new Timer();
		}
	}
	public void shiftR(int [][] board)	//Move one unit to the right
	{
		if (noCollisions(board, type, row, col + 1))
		{
			col++;
			lockTimer.cancel();	//Upon successful movement, reset lock timer
			lockTimer = new Timer();
		}
	}
	public void hardDrop(int [][] board)	//Hard drop to the point where it first reaches a collision
	{
		for (int a = row; a < 22; a++)
		{
			if (noCollisions(board, type, row + 1, col))
				row++;
		}
		finalLock = true;	//Lock immediately
	}
	
	public void firmDrop(int [][] board)	//Hard drop to the point where it first reaches a collision
	{
		for (int a = row; a < 22; a++)
		{
			if (noCollisions(board, type, row + 1, col))
				row++;
		}
	}
	
	public void rotateCW(int [][] board)	// Rotate clockwise, allows for wall kick by checking one to the left and one to the right
	{
		
		boolean successFlag = true;
		if (noCollisions(board, (type/10)*10 + (type%10 + 1)%4, row, col))
		{	
			type = (type/10)*10 + (type%10 + 1)%4;
		}
		else if (noCollisions(board, (type/10)*10 + (type%10 + 1)%4, row, col - 1))
		{
			type = (type/10)*10 + (type%10 + 1)%4;
			col--;
		}
		else if (noCollisions(board, (type/10)*10 + (type%10 + 1)%4, row, col + 1))
		{
			type = (type/10)*10 + (type%10 + 1)%4;
			col++;
		}
		else
			successFlag = false;
		if (successFlag)	//Upon successful rotation, reset the lock timer
		{
			lockTimer.cancel();
			lockTimer = new Timer();
		}
	}
	
	public void rotateCCW(int [][] board)	//Rotate counterclockwise, allows for wall kick by checking one to the left and one to the right
	{
		boolean successFlag = true;
		if (noCollisions(board, (type/10)*10 + (4+type%10-1)%4, row, col))
		{
			type = (type/10)*10 + (4+type%10-1)%4;
		}
		else if (noCollisions(board, (type/10)*10 + (4+type%10-1)%4, row, col+1))
		{
			type = (type/10)*10 + (4+type%10-1)%4;
			col++;
		}
		else if (noCollisions(board, (type/10)*10 + (4+type%10-1)%4, row, col-1))
		{
			type = (type/10)*10 + (4+type%10-1)%4;
			col--;
		}
		else
			successFlag = false;
		if (successFlag)	//Upon successful rotation, reset the lock timer
		{
			lockTimer.cancel();
			lockTimer = new Timer();
		}
	}
	
	public void softDrop(int [][] board)	//Drop the piece by one row
	{
		if (noCollisions(board, type, row + 1, col))
			row++;
	}
	
	public int getRow()	//Returns row
	{
		return row;
	}
	
	public int getCol()	//Returns column
	{
		return col;
	}
	
	public int getType()
	{
		return type;
	}
	
	public boolean display (Graphics g, int[][] board, int w, int h)	//Displays the piece onto graphics g
	{
		int[][] conf = configurations.get(new Integer(type));	//Get the configuration of the piece
		for (int r = 0; r < 4; r++)
			for (int c = 0; c < 4; c++)
			{
				if (conf[r][c] == 0)	//Ignore grey squares
					continue;
				switch (conf[r][c])	//Get the color of the square
				{
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
				if (row+r >=2)	//Fill only if contained inside the grid
					g.fillRect(w/2 - 5*(h/25) + (col+c)*(h/25), h/50+(row+r)*(h/25), (h/25), (h/25));
			}
		g.setColor(Color.WHITE);
		for (int r = 0; r < 4; r++)
			for (int c = 0; c < 4; c++)
			{
				if (conf[r][c] == 0 || row + r < 2)
					continue;
				else
					g.drawRect(w/2 - 5*(h/25) + (col+c)*(h/25), h/50+(row+r)*(h/25), (h/25), (h/25));
			}
		if (noCollisions(board, type, row + 1, col))	//If there are no collisions on the bottom side, keep going
		{	
			displayGhost(g, board, w, h);
			return false;
		}
		else	//If there are collisions, lock the piece into the grid and make a new piece
		{
			if (finalLock)
			{
				for (int r = 0; r < 4; r++)
					for (int c = 0; c < 4; c++)
					{
//						System.out.println(row-(3-r) + " " + (col+c) + " " + conf[r][c]);
						if (row+r < Tetris.height && row+r >=0 && col+c < Tetris.width && col+c >= 0 && conf[r][c] != 0)
							board[row+r][col+c] = conf[r][c];
					}
				return true;
			}
			else
			{
				lockTimer.schedule(new TimerTask()	//400 milliseconds to reset the lock timer
				{
					public void run()
					{
						finalLock = true;
					}
				}, 400);
				return false;
			}
		}
	}
	
	private void displayGhost(Graphics g, int[][] board, int w, int h)	//Displays where the piece would land if it kept falling
	{
		g.setColor(Color.LIGHT_GRAY);
		for (int ghostRow = 0; ghostRow < Tetris.height; ghostRow++)
		{
			if (!noCollisions(board, type, ghostRow+1, col))
			{
				int[][] conf = configurations.get(new Integer(type));	//Get the configuration of the piece
				for (int r = 0; r < 4; r++)
					for (int c = 0; c < 4; c++)
					{
						if (conf[r][c] == 0)	//Ignore grey squares
							continue;
						if (ghostRow+r >=2)	//Fill only if contained inside the grid
							g.fillRect(w/2 - 5*(h/25) +(col+c)*(h/25), 20+(ghostRow+r)*(h/25), (h/25), (h/25));
					}
				break;
			}
		}
	}
	
	private boolean noCollisions(int[][] board, int t, int r, int c)	//Check collisions
	{
		int[][] conf = configurations.get(t);
		
		if (r < -1)
		{
			System.out.println("Row < 0");
			return false;	
		}
			
		else
		{
			for (int a = 0; a < 4; a++)
			{
				for (int b = 0; b < 4; b++)
				{
					if (conf[a][b] != 0)
					{
						if (r+a >= Tetris.height || r+a < 0)	//Checks if row is outside the board
						{
							return false;
						}
						
						if (c+b < 0 || c+b >= Tetris.width)	//Checks if column is outside the board
						{
							return false;
						}
						if (board[r+a][c+b] != 0)	//Checks if piece collides with existing structures on board
						{
							return false;	
						}
					}
				}
			}
		}
		return true;
	}
}
