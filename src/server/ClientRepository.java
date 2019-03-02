package server;

import java.awt.Color;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import classes.Player;
import interfaces.ClientRepositoryInterface;
import interfaces.PlayerInterface;
import simulator.PositionInMaze;


/**
 * A class for all our {@link Player}'s to register with the server through. 
 * {@link ClientRepository} lets {@link Player}'s register for updates about all the other {@link Player}'s locations.
 * The callbacks are done through the {@link ClientRepositoryInterface}.
 * 
 * @author allan
 *
 */
public class ClientRepository extends UnicastRemoteObject implements ClientRepositoryInterface{
	private static final long serialVersionUID = 7700380034463227011L;
	/** {@link Player} id **/
	private int playerId = 0;
	/** All {@link messagesRecived} from the {@link Player}'s **/
	private int messagesRecived = 0;
	/** All the {@link messagesSent} sent to the {@link Player}'s **/
	private int messagesSent = 0;
	/** All the{@link Player}'s who have disconnected in total **/
	private int playersWhoLeft = 0;
	/** All registered {@link Player}'s who have connected **/
	private HashMap<Integer, PlayerInterface> playersRepo = new HashMap<Integer, PlayerInterface>();
	/** All registered {@link Player}s {@link Color}'s **/
	private HashMap<Integer, Color> playersColors = new HashMap<Integer, Color>();
	/** All registered {@link Player}s {@link PositionInMaze} **/
	private HashMap<Integer, PositionInMaze> playersPos = new HashMap<Integer, PositionInMaze>();
	/** All registered {@link Player}'s who have diconnected **/
	private ArrayList<Integer> playersDisconnected = new ArrayList<Integer>();
	/** Update frequency in milliseconds **/
	private int updateFrequency = 75;
	/** The map to be returned to all the {@link Player}'s **/
	private HashMap<String, Color> map;
	
	
	/**
	 * Constructor -> Empty.
	 * Just implement the Remote Object and start the 
	 * {@link #UpdaterThread()}. Await {@link Player}'s connection
	 * @throws RemoteException
	 */
	public ClientRepository() throws RemoteException {
		super();
		startUpdaterThread();
	}
	
	/**
	 * Start the {@link UpdaterThread} Thread
	 */
	private void startUpdaterThread() {
		UpdaterThread s = new UpdaterThread();
		s.setDaemon(true);
		s.start();
	}
	
	/**
	 * A Thread to periodically update {@link Player}s about the current
	 * {@link #playersPos} map and drop {@link #playersDisconnected} {@link Player}s.
	 */
	private class UpdaterThread extends Thread {
		@Override
		public void run() {
			try {
				while (true) {
					sleep(updateFrequency);
					removePlayers();
					if (playersRepo.size() > 0)
						createMap();
				}
			} catch (InterruptedException ie) {
				// In case the sleep blows up.
				System.out.println("Goddam! Something woke me up!!");
			}
		}
	}
	
	/**
	 * Add a new {@link Player} to the game. And return this {@link Player}'s {@link #playerId}
	 * 
	 */
	public int addPlayer(PlayerInterface player)throws RemoteException{
		synchronized (playersRepo) {
			playerId++;
			messagesRecived++;
			messagesSent++;
			playersRepo.put(playerId, player);
			playersColors.put(playerId, player.getColor());
			player.setId(playerId);
			System.out.println("Spiller med id " + playerId + " koblet til!");
			System.out.println("Totalt " + playersRepo.size() + " er tilkoblet.");
			return playerId;
		}
	}
	/**
	 *  Make a HashMap with all {@link Player}'s {@link playersPos} and
	 *  {@link #playersColors}.
	 */
	public void createMap(){
		synchronized (playersPos) {
			map = new HashMap<String, Color>();

			// Prepare our position/color map.
			Set<Integer> keys = playersPos.keySet();
			Iterator<Integer> it = keys.iterator();
			while (it.hasNext()) {
				try{
					Integer temp = it.next();
					PositionInMaze pos = playersPos.get(temp);
					Color c = playersColors.get(temp);
					map.put(new Integer(pos.getXpos()).toString().trim() + "," + new Integer(pos.getYpos()).toString().trim(), c);
				}catch(ConcurrentModificationException e){
					System.out.println("Snap! Something went wrong..\n " + e);
				}
				
			}
			sendUpdate();	
		}
	}
	/**
	 * Then send the updated map to all connected {@link Player}'s
	 * Mark {@link Player}'s we can't connect to as disconnected and to be {@link #removePlayers()}
	 */
	public synchronized void sendUpdate() {
		synchronized (playersRepo) {
			// Send map to all current players.
			Set<Integer> ids = playersRepo.keySet();
			Iterator<Integer> usr = ids.iterator();
			while (usr.hasNext()) {
				int id = usr.next();
				PlayerInterface player = playersRepo.get(id);
				try {
					player.updateMap(map);
					messagesSent++;
				} catch (RemoteException e) {
					playersDisconnected.add(id);
				}
			}
		
		}
	}
	
	/**
	 * Remove {@link Player}'s who have disconnected
	 */
	public void removePlayers() {
		synchronized (playersRepo) {
			synchronized (playersPos) {
				for (int i = 0; i < playersDisconnected.size(); i++) {
					playersRepo.remove(playersDisconnected.get(i));
					playersPos.remove(playersDisconnected.get(i));
					playersColors.remove(playersDisconnected.get(i));
					playersWhoLeft++;
					System.out.println("Spiller med id " + playersDisconnected.get(i) + " koblet fra.\n"
							+"Det er " + playersRepo.size() + " spiller(e) tilkoblet ");
				}
				playersDisconnected = new ArrayList<Integer>();
			}
		}
	}

	
	/**
	 * Return the total number of {@link Player}'s connected
	 */
	@Override
	public String getTotPlayers() throws RemoteException {
		messagesRecived++;
		messagesSent++;
		return new Integer(playersRepo.size()).toString();
	}

	/**
	 * Return {@link #messagesRecived} + {@link #messagesSent}
	 */
	@Override
	public String getTotMessages() throws RemoteException {
		messagesRecived++;
		messagesSent++;
		return new Integer(messagesRecived+messagesSent).toString();
	}

	/**
	 * Return {@link #messagesSent}
	 */
	@Override
	public String getMessagesSent() throws RemoteException {
		messagesRecived++;
		messagesSent++;
		return new Integer(messagesSent).toString();
	}
	/**
	 * Return {@link #messagesRecived}
	 */
	@Override
	public String getMessagesRecived() throws RemoteException {
		messagesRecived++;
		messagesSent++;
		return new Integer(messagesRecived).toString();
	}
	/**
	 * Return {@link #playersWhoLeft}
	 */
	@Override
	public String getTotalNumberOfPlayerWhoLeft() throws RemoteException {
		messagesRecived++;
		messagesSent++;
		return new Integer(playersWhoLeft).toString();
	}
	
	/**
	 * Get all the known positions taken in the maze 
	 */
	@Override
	public HashMap<Integer, PositionInMaze> getPlayerMap() throws RemoteException {
		// TODO Auto-generated method stub
		return playersPos;
	}

	
	/**
	 * Save the {@link Player}'s new location to the {link {@link #playersPos} HashMap
	 */
	@Override
	public void updatePosition(Integer playerId, PositionInMaze positionInMaze) throws RemoteException {
		messagesRecived++;
		playersPos.put(playerId,positionInMaze);

	}

	

	
}
