package server;

/**
 * <p>Title: </p>
 * RMIServer - En server som kobler seg opp å kjører server-objekter på
 * rmiregistry som startes automagisk.
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
import java.net.*;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;

import classes.BoxMaze;
import classes.Player;
import client.Maze;


/**
 * RMIServer starts execution at the standard entry point "public static void main";
 * It creates an instance of itself and continues processing in the constructor.
 */

public class RMIServer
{
  private final static int DEFAULT_PORT = 9000;
  private final static String DEFAULT_HOST = "undefined";
  public static int    PORT = DEFAULT_PORT;
  private static String HOST_NAME;
  private static InetAddress myAdress = null;
  @SuppressWarnings("unused")
private static RMIServer rmi;

  private static BoxMaze maze;
  public static String MazeName = "Maze";
  /**
   * @todo: Her legger man til andre objekter som skal være på server
  */

  
  public static Player player;
  public static String PlayerName = "Player";
  public static ClientRepository clientRepository;
  public static String ClientRepositoryName = "ClientRepository";

  
  
  
  
  public RMIServer() throws RemoteException, MalformedURLException,
                             NotBoundException, AlreadyBoundException {
    getStaticInfo();
    LocateRegistry.createRegistry(PORT);
    System.out.println( "RMIRegistry created on host computer " + HOST_NAME +
                        " on port " + Integer.toString( PORT) );

    /*
    ** Legger inn labyrinten
    */
    maze = new BoxMaze(Maze.DIM);
    System.out.println( "Remote implementation object for " + MazeName + " created" );
    String urlString = "//" + HOST_NAME + ":" + PORT + "/" +
                       MazeName;

    Naming.rebind( urlString, maze );
    /**
    * @todo: Og her legges andre objekter som også skal være på server inn ....
    */
    
    player = new Player();
    System.out.println( "Remote implementation object for " + PlayerName + " created" );
    String urlString2 = "//" + HOST_NAME + ":" + PORT + "/" +
    		PlayerName;

    Naming.rebind( urlString2, player );
    
    clientRepository = new ClientRepository();
    System.out.println( "Remote implementation object for " + ClientRepositoryName + " created" );
    String urlString3 = "//" + HOST_NAME + ":" + PORT + "/" +
    		ClientRepositoryName;

    Naming.rebind( urlString3, clientRepository );
    
    /*/////////////////////////////////////////////////////////
	Code below came with the exercise - Not changed
	////////////////////////////////////////////////////////*/
    
    System.out.println( "Bindings Finished, waiting for client requests." );
  }

  private static void getStaticInfo() {
    /**
     * Henter hostname på min datamaskin
     */
    if (HOST_NAME == null) HOST_NAME = DEFAULT_HOST;
    if (PORT == 0) PORT = DEFAULT_PORT;
    if (HOST_NAME.equals("undefined")) {
      try {
        myAdress = InetAddress.getLocalHost();
        /*
        ** Merk at kallet under vil kunne gi meldingen :
        **
        ** "Internal errorjava.net.MalformedURLException: invalid authority"
        **
        ** i tilfeller hvor navnen på maskinen ikke tilfredstiller
        ** spesielle krav.
        ** I så tilfelle, bruk "localhost" i stedet.
        **
        ** Meldingen som gis har ingen betydning
        */
//        HOST_NAME = myAdress.getHostName();
        HOST_NAME = "localhost";
      }
      catch (java.net.UnknownHostException e) {
        System.err.println("Klarer ikke å finne egen nettadresse");
        e.printStackTrace(System.err);
      }
    }
    else
      System.out.println("En MazeServer kjører allerede, bruk den");

    System.out.println("Maze server navn: " + HOST_NAME);
    System.out.println("Maze server ip:   " + myAdress.getHostAddress());
  }

  public static int getRMIPort() { return PORT; }
  public static String getHostName() { return HOST_NAME; }
  public static String getHostIP() { return myAdress.getHostAddress(); }

   public static void main ( String[] args ) throws Exception {
      try { rmi = new RMIServer(); }
      catch ( java.rmi.UnknownHostException uhe ) {
         System.out.println( "Maskinnavnet, " + HOST_NAME + " er ikke korrekt." );
      }
      catch ( RemoteException re ) {
         System.out.println( "Error starting service" );
         System.out.println( "" + re );
         re.printStackTrace(System.err);
      }
      catch ( MalformedURLException mURLe )
      {
         System.out.println( "Internal error" + mURLe );
      }
      catch ( NotBoundException nbe )
      {
         System.out.println( "Not Bound" );
         System.out.println( "" + nbe );
      }
      catch ( AlreadyBoundException abe )
      {
         System.out.println( "Already Bound" );
         System.out.println( "" + abe );
      }
      System.out.println("RMIRegistry on " + HOST_NAME + ":" + PORT + "\n----------------------------");
   }  // main
}  // class RMIServer
