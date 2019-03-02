package simulator;

import java.io.Serializable;

/*/////////////////////////////////////////////////////////
Code below came with the exercise - Not changed
////////////////////////////////////////////////////////*/

public class PositionInMaze implements Serializable{
	private static final long serialVersionUID = -3275691301441215733L;
	
	private int xpos, ypos;
	
	public PositionInMaze(int xp, int yp) {
		xpos = xp;
		ypos = yp;
	}

	public int getXpos() {
		return xpos;
	}

	public int getYpos() {
		return ypos;
	}
	
	public String toString() {
		return "xpos: " + xpos + "\typos: " + ypos;
	}
}
