package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

import classes.Player;
import server.ClientRepository;
import simulator.PositionInMaze;
/**
 * The interface that lets the {@link Player}'s send callbacks to the {@link ClientRepository}'s
 * @author allan
 *
 */
public interface ClientRepositoryInterface extends Remote{
	/**
	 * Add a new {@link Player} to the {@link ClientRepositoryInterface}
	 * @param {@link Player}
	 * @return player id
	 * @throws RemoteException
	 */
	int addPlayer(PlayerInterface player) throws RemoteException;
	/**
	 * Return the total number of {@link Player}'s connected to {@link ClientRepositoryInterface}
	 * @return total number of players (int)
	 * @throws RemoteException
	 */
	String getTotPlayers() throws RemoteException;
	/**
	 * Return the total number of messages sent to and from the server
	 * @return total number of messages
	 * @throws RemoteException
	 */
	String getTotMessages() throws RemoteException;
	/**
	 * Return the total number of messages sent from the server
	 * @return total number of messages sent
	 * @throws RemoteException
	 */
	String getMessagesSent() throws RemoteException;
	/**
	 * Return the total number of messages sent to the server
	 * @return total number of messages received
	 * @throws RemoteException
	 */
	String getMessagesRecived() throws RemoteException;
	/**
	 * Get the {@link Player} map with all occupied positions
	 * @return players map
	 * @throws RemoteException
	 */
	HashMap<Integer, PositionInMaze> getPlayerMap() throws RemoteException;
	/**
	 * Push the new location for the {@link Player} to {@link ClientRepository}
	 * @param playerId
	 * @param positionInMaze
	 * @throws RemoteException
	 */
	void updatePosition(Integer playerId, PositionInMaze positionInMaze) throws RemoteException;
	/**
	 * Return the total number of {@link Player}'s who have disconnected from the game
	 * @return total number of messages received
	 * @throws RemoteException
	 */
	String getTotalNumberOfPlayerWhoLeft() throws RemoteException;
}
