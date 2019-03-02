package client;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.applet.*;

/**
 *
 * <p>Title: Maze</p>
 *
 * <p>Description: En enkel applet som viser den randomiserte labyrinten</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import classes.Box;
import classes.Player;
import interfaces.BoxMazeInterface;
import interfaces.ClientRepositoryInterface;
import interfaces.PlayerInterface;
import server.RMIServer;
import simulator.PositionInMaze;
import simulator.VirtualUser;

import java.rmi.NotBoundException;
/**
 * Tegner opp maze i en applet, basert på definisjon som man finner på RMIServer
 * RMIServer på sin side  henter størrelsen fra definisjonen i Maze
 * @author asd
 *
 */
@SuppressWarnings("serial")
public class Maze extends Applet implements KeyListener{

	private BoxMazeInterface bm;
	private Box[][] maze;
	public static int DIM = 30;
	private int dim = DIM;

	static int xp;
	static int yp;
	static boolean found = false;

	private String server_hostname;
	private int server_portnumber;


	/******************************/
	@SuppressWarnings("unused")
	private PlayerInterface playerInterface;
	private ClientRepositoryInterface clientRepositoryInterface;
	@SuppressWarnings("unused")
	private Integer playerId;
	private PlayerInterface newPlayer;
	
	// For simulations 
	/** If true we are simulating {@link Player}'s AND our own player
	 * If you want to simulate players and control your own, start one
	 * Maze with ARE_WE_SIMULATING set to false, then another with it set to true.
	 * Remember to set the number of {@link Player}'s to simulate
	 **/
	private boolean ARE_WE_SIMULATING = true;
	/** How many {@link Player}'s to simulate. If zero we only move our {@link Player} **/
	private int playersToSimulate = 50;
	
	
	
	/**
	 * Henter labyrinten fra RMIServer
	 */
	public void init() {
		/** Set a size to the applet that fits our maze **/
		setSize(583,410);
		/** If we are simulating we don't activate the {@link KeyListener} **/
		if(!ARE_WE_SIMULATING){
			addKeyListener(this);
		}
		 
		/*
		 ** Kobler opp mot RMIServer, under forutsetning av at disse
		 ** kjører på samme maskin. Hvis ikke må oppkoblingen
		 ** skrives om slik at dette passer med virkeligheten.
		 */
		if (server_hostname == null)
			server_hostname = RMIServer.getHostName();
		if (server_portnumber == 0)
			server_portnumber = RMIServer.getRMIPort();
		try {
			java.rmi.registry.Registry r = java.rmi.registry.LocateRegistry.
			getRegistry(server_hostname,
					server_portnumber);

			/*
			 ** Henter inn referansen til Labyrinten (ROR)
			 */
			bm = (BoxMazeInterface) r.lookup(RMIServer.MazeName);
			maze = bm.getMaze();
			
			playerInterface = (PlayerInterface) r.lookup(RMIServer.PlayerName);
			
			clientRepositoryInterface = (ClientRepositoryInterface) r.lookup(RMIServer.ClientRepositoryName);
			
			newPlayer = new Player(clientRepositoryInterface, this, bm);
			
			playerId = clientRepositoryInterface.addPlayer(newPlayer);
			
			if(ARE_WE_SIMULATING){
				startWorkerThread();
			}
			
/*			
** Finner løsningene ut av maze - se forøvrig kildekode for VirtualMaze for ytterligere
** kommentarer. Løsningen er implementert med backtracking-algoritme
*
			VirtualUser vu = new VirtualUser(maze);
			PositionInMaze [] pos;
/*			pos = vu.getFirstIterationLoop();

			for (int i = 0; i < pos.length; i++)
				System.out.println(pos[i]);
*
			pos = vu.getIterationLoop();
			for (int i = 0; i < pos.length; i++)
				System.out.println(pos[i]);
/**/			
		}
		catch (RemoteException e) {
			System.err.println("Remote Exception: " + e.getMessage());
			System.exit(0);
		}
		catch (NotBoundException f) {
			/*
			 ** En exception her er en indikasjon på at man ved oppslag (lookup())
			 ** ikke finner det objektet som man søker.
			 ** Årsaken til at dette skjer kan være mange, men vær oppmerksom på
			 ** at hvis hostname ikke er OK (RMIServer gir da feilmelding under
			 ** oppstart) kan være en årsak.
			 */
			System.err.println("Not Bound Exception: " + f.getMessage());
			System.exit(0);
		}
	}
	
	/**
	 * This is where your controls should be set. If w, move up. If d, move right and so on.
	 * Also perform checks first to see if what ways you can go. Is there a wall that way,
	 * (check witch side of your box don't have walls) or is there another player already in that position. 
	 * If you only have walls one one side, you are on the outside. And so on..
	 * This is not implemented to save time. I am well overdue with this project, so I concentrated 
	 * on the part I saw as most important. The communication between the server and the players.
	 * There is a simple check implemented in the current {@link #move} function for simulation 
	 * that hinders two or more {@link Player}'s to occupy the same space.
	 * The same function is available here if you turn off simulation. Only used during development.
	 * Responds to any key.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		try {
			
			newPlayer.move();
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
		
	}
	
	/**
	 * Fill in information in the window about how many {@link Player}'s are connected 
	 * and how many messages have been sent between {@link Player}'s and the server 
	 * ({@link ClientRepositoryInterface} and {@link PlayerInterface})
	 * 
	 * @param Graphics g
	 * @throws RemoteException 
	 */
	private void fillInInfo(Graphics g) throws RemoteException{
		g.drawString("Antall spillere tilkoblet: " + clientRepositoryInterface.getTotPlayers(), DIM * 10, 25);
		g.drawString("Antall Spillere frakoblet: " + clientRepositoryInterface.getTotalNumberOfPlayerWhoLeft(), DIM * 10, 45);
		g.drawString("Antall beskjeder sendt til server: " + clientRepositoryInterface.getMessagesRecived(), DIM * 10, 65);
		g.drawString("Antall beskjeder sendt fra server: " + clientRepositoryInterface.getMessagesSent(), DIM * 10, 85);
		g.drawString("Antall beskjeder totalt: " + clientRepositoryInterface.getTotMessages(), DIM * 10, 105);
	}
	
	/**
	 * Draw the {@link Player}'s on the map based on their {@link PositionInMaze}
	 * @param Graphics g
	 * @throws RemoteException 
	 */
	private void drawMap(Graphics g) throws RemoteException {
		// Get map from self.
		HashMap<String, Color> map = newPlayer.getMap();
		// Iterate over keys in the given map.
		if(map == null) return;
		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			// Parse the x and y positions.
			String[] pos = key.split(",");
			int x = new Integer(pos[0].trim());
			int y = new Integer(pos[1].trim());
			// Set the correct Color.
			g.setColor(map.get(key));
			// Draw the VirtualUser position.
			g.fillOval((x * 10) + 2, (y * 10) + 2, 5, 5);
		}
		// Reset the default Color.
		g.setColor(Color.black);	
		
	}
	/**
	 * Start the {@link Worker}
	 */
	public void startWorkerThread(){
		Worker pl = new Worker(clientRepositoryInterface, this, bm, newPlayer);
		pl.setDaemon(true);
		pl.start();
	}
	
	/**
	 * Run the {@link Worker} Thread, performing {@link Player} moves
	 * eternally. If we are simulating -> Start some {@link #SimulatorThread()}'s.
	 * If we are only simulation our self, just move this {@link Player} and don't 
	 * spawn any more {@link Player}'s
	 */
	private class Worker extends Thread {

		private Maze maze;
		private ClientRepositoryInterface clientRepositoryInterface;
		private PlayerInterface player;
		/**
		 * {@link Worker} thread constructor. Takes the {@link ClientRepositoryInterface},
		 * the {@link Player}, the {@link Maze} and the {@link BoxMazeInterface}.
		 * We use the {@link Player} to simulate moves's for our own player.
		 * We use the {@link ClientRepositoryInterface} to send updates to the server for 
		 * both our {@link Player} and other {@link Player}'s we are simulating
		 *
		 * @param {@link ClientRepositoryInterface} cPI
		 * @param {@link Maze} m
		 * @param {@link BoxMazeInterface} bm
		 * @param {@link Player} our New Player (The one you are playing with)
		 */
		public Worker(ClientRepositoryInterface cPI, Maze m, BoxMazeInterface bm, PlayerInterface newPlayer) {
			maze = m;
			clientRepositoryInterface = cPI;
			player = newPlayer;
		}
		
		@Override
		public void run() {
			try {
				Random r = new Random();
				int l = 500;
				int h = 5000;
				// For every player to simulate, sleep for a random set time, then spawn the player.
				for(int i=0;i<maze.playersToSimulate;i++){
					sleep(r.nextInt(h-l) + l);
					SimThread pl = new SimThread(clientRepositoryInterface, maze, bm);
					pl.setDaemon(true);
					pl.start();		
				}
				// Sleep for a random set time, them move
				while(true){
					player.move();
					sleep(r.nextInt(h-l) + l);
				}
			} catch (InterruptedException ex) {
				// In case the sleep blows up.
				System.out.println("Goddam!! Somethong woke Worker up.. ");
			} catch (RemoteException e) {
				// In case of a RemoteException, we have a problem with the
				// connection.
				System.out
						.println("Connection to server failed. Try to restart the server and the Maze applet.");
			}
		}
	}
	
	/**
	 * Run the {@link #SimulatorThread()}'s, generating some {@link Player}'s
	 * for us. Register them to the {@link ClientRepositoryInterface} and 
	 * start moving them around.
	 */
	private class SimThread extends Thread{
		
		private Maze maze;
		private ClientRepositoryInterface clientRepositoryInterface;
		/**
		 * Constructor. Takes {@link ClientRepositoryInterface}, {@link Maze}
		 * and the {@link BoxMazeInterface} and generate some {@link Player}'s
		 * for us, and randomly move them through the {@link Maze} based on
		 * preset moves by our AI {@link VirtualUser}.
		 * @param {@link ClientRepositoryInterface} cPI
		 * @param {@link Maze} m
		 * @param {@link BoxMazeInterface} bm
		 */
		public SimThread(ClientRepositoryInterface cPI, Maze m, BoxMazeInterface bm){
			maze = m;
			clientRepositoryInterface = cPI;
		}
		@Override
		public void run() {
			try {
				// Create a new player
				PlayerInterface player = new Player(clientRepositoryInterface, maze, bm);
				// Register at the server
				clientRepositoryInterface.addPlayer(player);
				// Then sleep for a random set time and then move.. Forever!
				Random r = new Random();
				int l = 250;
				int h = 1000;
				while(true){
					sleep(r.nextInt(h-l) + l);
					player.move();
				}
			} catch (RemoteException | InterruptedException e) {
				System.out.println("Goddam!! Somethong woke SimThread up.. " + e);
			}
		}
	}
	
	
	
	/*/////////////////////////////////////////////////////////
	Code below came with the exercise - Not changed
	//
	  if (newPlayer != null){
	 
			try {
				drawMap(g);
				// Draw the updated number of players and messages sent
				fillInInfo(g);
			} catch (RemoteException e) {
				System.out.println("Paint, drawMap() e: " + e);
			}
			
		}
		That is added to the paint method. Just to start add in the
		statistics and the other players positions. 
	////////////////////////////////////////////////////////*/
	//Get a parameter value
	public String getParameter(String key, String def) {
		return getParameter(key) != null ? getParameter(key) : def;
	}
	//Get Applet information
	public String getAppletInfo() {
		return "Applet Information";
	}
	//Get parameter info
	public String[][] getParameterInfo() {
		java.lang.String[][] pinfo = { {"Size", "int", ""},
		};
		return pinfo;
	}
	/**
	 * Viser labyrinten / tegner den i applet
	 * @param g Graphics
	 */
	public void paint (Graphics g) {
		int x, y;

		// Tegner baser på box-definisjonene ....

		for (x = 1; x < (dim - 1); ++x)
			for (y = 1; y < (dim - 1); ++y) {
				if (maze[x][y].getUp() == null)
					g.drawLine(x * 10, y * 10, x * 10 + 10, y * 10);
				if (maze[x][y].getDown() == null)
					g.drawLine(x * 10, y * 10 + 10, x * 10 + 10, y * 10 + 10);
				if (maze[x][y].getLeft() == null)
					g.drawLine(x * 10, y * 10, x * 10, y * 10 + 10);
				if (maze[x][y].getRight() == null)
					g.drawLine(x * 10 + 10, y * 10, x * 10 + 10, y * 10 + 10);
			}
		if (newPlayer != null){
			try {
				drawMap(g);
				// Draw the updated number of players and messages sent
				fillInInfo(g);
			} catch (RemoteException e) {
				System.out.println("Paint, drawMap() e: " + e);
			}
			
		}
	}

}

