package interfaces;

import java.awt.Color;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

import classes.Player;
import server.ClientRepository;
import simulator.PositionInMaze;
/**
 * The interface that lets the {@link ClientRepository} send callbacks to the {@link Player}'s
 * @author allan
 *
 */
public interface PlayerInterface extends Remote{
	/**
	 * Get this {@link Player}'s color as {@link String}
	 * @return {@link Player} as {@link String}
	 * @throws RemoteException
	 */
	public String getColorAsString() throws RemoteException;
	/**
	 * Get this {@link Player}'s {@link Color}
	 * @return {@link Player} {@link Color}
	 * @throws RemoteException
	 */
	public Color getColor() throws RemoteException;
	/**
	 * Get this {@link Player}'s id
	 * @return {@link Player} id
	 * @throws RemoteException
	 */
	public int getId() throws RemoteException;
	/**
	 * Get this {@link Player} number of moves
	 * @return {@link Player} moves, int
	 * @throws RemoteException
	 */
	public int getNumberOfMoves() throws RemoteException;
	/**
	 * Set this {@link Player}'s id
	 * @param id
	 * @throws RemoteException
	 */
	public void setId(int id) throws RemoteException;
	/**
	 * Update this {@link Player}'s map
	 * In the form:
	 * String: Xpos,Ypos
	 * Color: player color
	 * @param HashMap<String,Color>
	 * @throws RemoteException
	 */
	public void updateMap(HashMap<String, Color> map) throws RemoteException;
	/**
	 * Move this {@link Player} to the next {@link PositionInMaze} in 
	 * the predefined moved by our AI - {@link VirutaulUser}
	 * @throws RemoteException
	 */
	public void move() throws RemoteException;
	/**
	 * Return the updated {@link PositionInMaze} map with the {@link Player}'s
	 * {@link Color}'s
	 * @return
	 */
	public HashMap<String, Color> getMap() throws RemoteException;
}
