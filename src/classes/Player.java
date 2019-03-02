package classes;

import java.awt.Color;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Random;

import client.Maze;
import interfaces.BoxMazeInterface;
import interfaces.ClientRepositoryInterface;
import interfaces.PlayerInterface;
import server.ClientRepository;
import simulator.PositionInMaze;
import simulator.VirtualUser;
/**
 * A class for all our {@link Player}'s. Create a new {@link Player} to register
 * with the servers ClientRepository. Callbacks to the {@link Player}'s is done through
 * the {@link PlayerInterface}.
 * 
 * @author allan
 *
 */
public class Player extends UnicastRemoteObject implements PlayerInterface{
	private static final long serialVersionUID = -4719096970403673893L;
	
	/** The {@link Box} from  {@link BoxMazeInterface} */
	private Box[][] boxmaze;
	/** The Color of this {@link Player} */
	private Color color;
	/** The map of all {@link PositionInMaze positions} */
	private HashMap<String, Color> playersLocations = new HashMap<String, Color>();
	/** This {@link Player}'s id at the {@link ClientRepository} server. */
	private Integer playerId;
	/** The {@link BoxMaze}, {@link ClientRepository} and {@link Maze} server connection. */
	@SuppressWarnings("unused")
	private BoxMazeInterface boxMazeInterface;
	private ClientRepositoryInterface clientRepositoryInterface;
	private Maze maze;
	/** The current array of moves left in {@link #PositionInMaze} to perform */
	public PositionInMaze[] moves;
	/** The current position in the {@link #moves} array. */
	private int currentPosInArray;
	/** Go back to random {@link #PositionInMaze} */
	private boolean turn = true;
	/** Our AI {@link #VirtualUser} **/
	private VirtualUser AI;
	
	/**
	 * Constructor for the local {@link Player} implementation. Takes an enabled
	 * {@link BoxMazeInterface} connection, a instance of a {@link Maze} applet and
	 * a instance of a {@link ClientRepositoryInterface}
	 *.
	 * @param rep   The ClientRepository
	 * @param bm	The BoxMaze
	 * @param mz	The maze 
	 * @throws RemoteException
	 */
	public Player(ClientRepositoryInterface rep, Maze mz,BoxMazeInterface bm) throws RemoteException {
		setColor();
		clientRepositoryInterface = rep;
		maze = mz;
		boxMazeInterface = bm;
		boxmaze = bm.getMaze();
		AI = new VirtualUser(boxmaze);
		setMoves();
	}

	/**
	 * Empty constructor
	 * @throws RemoteException
	 */
	public Player() throws RemoteException {
		super();
	}

	/**
	 * Generate a {@link Color} for this {@link Player}
	 * 
	 * @return random {@link Color}
	 */
	private void setColor() {
		Random generator = new Random();
		Color[] array = {Color.cyan, Color.blue, Color.red, Color.pink, Color.green, Color.gray, Color.orange};
		int rnd = generator.nextInt(array.length);
		color = array[rnd];
	}
	
	/**
	 * If we are out of {@link #moves}, we have to generate some. 
	 * If {@link #turn} is true (as it is to begin with) we set it to false, and get a random spot
	 * in the {@link BoxMaze} and the {@link #moves} needed to get out. We then get a route from the 
	 * entrance of the maze and through it.
	 * Then we start over again from a random spot in the {@link BoxMaze}. 
	 */
	public void setMoves(){
		currentPosInArray = 0;
		turn = !turn;
		moves = turn ? AI.getIterationLoop() : AI.getFirstIterationLoop();
	}
	
	/**
	 * Method for letting the server {@link ClientRepository}
	 * fetch this {@link Player} {@link #color}
	 */
	@Override
	public Color getColor(){	return color;	}
	
	/**
	 * Method for letting the server {@link ClientRepository}
	 * fetch this {@link Player} {@link #color} as {@link String}
	 */
	@Override
	public String getColorAsString(){	return color.toString();	}
	
	/**
	 * Method for letting the server {@link ClientRepository}
	 * fetch this {@link Player} {@link #playerId}
	 */
	@Override
	public int getId() throws RemoteException {	return playerId;	}
	
	/**
	 * Method for letting the server {@link ClientRepository}
	 * set this {@link Player} {@link #playerId}
	 */
	@Override
	public void setId(int id) throws RemoteException {	this.playerId = id;	}
	
	/**
	 * Method for letting the server {@link ClientRepository}
	 * fetch this {@link Player} number of given moves
	 */
	@Override
	public int getNumberOfMoves() throws RemoteException {	return moves.length;	}
	
	/**
	 * Method for letting the server {@link ClientRepository}
	 * update this {@link Player} map -> Map with all the other {@link Player}
	 * {@link #playersLocations} and {@link #color}
	 */
	@Override
	public void updateMap(HashMap<String, Color> map) throws RemoteException {
		playersLocations = map;
		maze.repaint();
	
	}

	/**
	 * Method for moving this {@link Player} to the next {@link PositionInMaze}
	 * defined by {@link VirtualUser}. If the {@link PositionInMaze} is taken
	 * we stay put in the spot we are in. This method is made with the simulation
	 * of {@link Player} in mind.
	 */
	@Override
	public void move() throws RemoteException {
		// So long as we have moves left
				if(currentPosInArray < (moves.length - 1)){
					// go to the next move to make
					currentPosInArray++;
				}
				// else we have to set some moves for this player
				else setMoves();
				
				// Update the server with this player current move
				try{
					if(playersLocations.size() > 0){
						// But if this locations is taken we stay put
						if(playersLocations.containsKey(new Integer(moves[currentPosInArray].getXpos()).toString().trim() 
								+ "," + new Integer(moves[currentPosInArray].getYpos()).toString().trim())){
							if(currentPosInArray > 0){
								// Just a little 'push'. It sometimes stopped when Y hit 0.
								// Only happens when we traverse outside the maze to the right on our way back to the entrance
								if(new Integer(moves[currentPosInArray].getYpos()) == 0){
									clientRepositoryInterface.updatePosition(playerId,  moves[currentPosInArray]);
								}
								else{
									currentPosInArray--;
								}
							}
						} 	// Just a little helper. They had a tendency to go stuck in the lover exit since they are not allowed to be placed on top of each other
							// So we check if we are going from x=28 to x=29 and then back to x=28 again. If we do, skip to positions so we don't get trapped by another player
						else if((moves.length - currentPosInArray + 2) > 2){
							if((new Integer(moves[currentPosInArray].getXpos()).toString().trim()+","+new Integer(moves[currentPosInArray].getYpos()).toString().trim()).equals("28,29")){
								if((new Integer(moves[currentPosInArray+1].getXpos()).toString().trim()+","+new Integer(moves[currentPosInArray+1].getYpos()).toString().trim()).equals("29,29")){
									if((new Integer(moves[currentPosInArray+2].getXpos()).toString().trim()+","+new Integer(moves[currentPosInArray+2].getYpos()).toString().trim()).equals("28,29")){
										clientRepositoryInterface.updatePosition(playerId,  moves[currentPosInArray]);
										currentPosInArray = currentPosInArray + 2;
									}
								}
							}
							else{ // Else we move
								clientRepositoryInterface.updatePosition(playerId,  moves[currentPosInArray]);
							}
						}
						else{ // If locations is open, we move.
							clientRepositoryInterface.updatePosition(playerId,  moves[currentPosInArray]);
						}
					}
					else{ // If no locations is registered, we can definitely move
						clientRepositoryInterface.updatePosition(playerId,  moves[currentPosInArray]);
					}
				}catch(RemoteException ex){
					System.out.println("Error on update to server. Exception: " + ex);
				}
		
	}

	/**
	 * Method for returning the updated {@link PositionInMaze} and
	 * {@link Color} for all the {@link Players} to the {@link Maze} applet
	 */
	@Override
	public HashMap<String, Color> getMap() {
		return playersLocations;
	}

}
