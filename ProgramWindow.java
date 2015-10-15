package Version7;

import java.awt.*;

public class ProgramWindow extends Frame{
	
	TetrisPanel panel = new TetrisPanel();
	public ProgramWindow()
	{
		setTitle("Tetris");
		setSize(800, 1000);
		setMinimumSize(new Dimension(400, 500));
		setLocation(100,100);
		setResizable(true);
		add(panel);
		setVisible(true);
	}
}