

import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable {

  // client socket
  private static Socket clientSocket = null;
  // output stream
  private static PrintStream cout = null;
  //input stream
  private static DataInputStream cin = null;

  private static BufferedReader inputLine = null;
  private static boolean closed = false;
  private static InetAddress addr;

  public static void main(String[] args) throws UnknownHostException {

    // The default port.
    int portNumber = 4119;
    // The default host.
    String host = "localhost";
    //String host = "160.39.154.174";
    if (args.length < 2) {
      System.out
          .println("Connect to default host and prot: "
              + "Now using host=" + host + ", portNumber=" + portNumber);
      addr=InetAddress.getByName(host);
    } else {
      host = args[0];
      portNumber = Integer.valueOf(args[1]).intValue();
      addr=InetAddress.getByName(host);
    }

    /*
     * Open a socket on a given host and port. Open input and output streams.
     */
    try {
      clientSocket = new Socket(addr, portNumber);
      inputLine = new BufferedReader(new InputStreamReader(System.in));
      cout = new PrintStream(clientSocket.getOutputStream());
      cin = new DataInputStream(clientSocket.getInputStream());
    } catch (UnknownHostException e) {
      System.err.println("Don't know about host " + host);
    } catch (IOException e) {
      System.err.println("Couldn't get I/O for the connection to the host "
          + host);
    }

    /*write data to the socket.
     */
    if (clientSocket != null && cout != null && cin != null) {
      try {
        /* Create a thread to read from the server. */
        new Thread(new Client()).start();
        while (!closed) {
          //cout.println(inputLine.readLine().trim());
	        String read=inputLine.readLine().trim();
	          cout.println(read);
	          if(read.contains("logout"))
	          {
	        	  System.out.println("You are logged out!");
	        	  break;
	          }
        }
        /*
         * Close the output stream, close the input stream, close the socket.
         */
        cout.close();
        cin.close();
        clientSocket.close();
      } catch (IOException e) {
        //System.err.println("IOException:  " + e);
      }
    }
  }

  /*
   * Create a thread to read from the server.
   */
  public void run() {
    /*
     * Keep on reading from the socket until we receive "bye" from the
     * server. Once we received we break.
     */
    String responseLine;
    try {
      while ((responseLine = cin.readLine()) != null) {
        System.out.println(responseLine);
        if (responseLine.indexOf("bye") != -1)
          break;
      }
      closed = true;
    } catch (IOException e) {
      //System.err.println("IOException:  " + e);
    }
  }
}