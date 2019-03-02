package interfaces;

import java.rmi.*;

import classes.Box;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

/*/////////////////////////////////////////////////////////
Code below came with the exercise - Not changed
////////////////////////////////////////////////////////*/

public interface BoxMazeInterface extends Remote {
    public Box [][] getMaze() throws RemoteException;
}
