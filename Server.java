

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

/*
 * A chat server that allows multiple clients login and respond to command.
 */
public class Server {
  // The server socket.
  private static ServerSocket serverSocket = null;
  // The client socket.
  private static Socket clientSocket = null;

  // This chat server can accept up to maxClientsCount clients' connections.
  private static final int maxClientsCount = 100;
  private static final ClientThread[] threads = new ClientThread[maxClientsCount];
  private static Map<String,String> map=new HashMap<String,String>();
  private static Map<String,Demo> mapWrongLogin= new HashMap<String,Demo>();
  private static Map<String,String> offMmap=new HashMap<String,String>();
  private static Map<String,Demo1> timeOutMap=new HashMap<String,Demo1>();
  //private static monitorThread offThread;
  private static Map<String,ArrayList<String>> groupTalk=new HashMap<String,ArrayList<String>>();
  private static Map<String,Long> logoutTime=new HashMap<String,Long>();


  public static void main(String args[]) throws FileNotFoundException {

    // The default port number.
    int portNumber = 4119;
    if (args.length < 1) {
      System.out.println("Now using port number=" + portNumber);
    } else {
      portNumber = Integer.valueOf(args[0]).intValue();
    }

    /*
     * read username and password from file user_pass.txt
     */
    File inputFile = new File("user_pass.txt");
	Scanner in = new Scanner(inputFile);
	while (in.hasNextLine())
	{
	String line = in.nextLine();
	String[] splitStr = line.split("\\s+");
	map.put(splitStr[0],splitStr[1]);
	}
	in.close();
    /*
     * Open a server socket on the portNumber (default 4119).
     */
    try {
      serverSocket = new ServerSocket(portNumber);
    } catch (IOException e) {
      System.out.println(e);
    }

    /*
     * Create a client socket for each connection and pass it to a new client
     * thread.
     */

    //(offThread=new monitorThread(timeOutMap, maxClientsCount)).start();

    while (true) {
      try {
        clientSocket = serverSocket.accept();
        int i = 0;
        for (i = 0; i < maxClientsCount; i++) {
          if (threads[i] == null) {
            (threads[i] = new ClientThread(clientSocket, threads,map,mapWrongLogin,offMmap,timeOutMap, groupTalk,logoutTime)).start();
            //Socket clientSocket, clientThread[] threads,Map<String,String> amap,Map<String,Demo> bmap,Map<String,String> aoffMmap,Map<String,Long> atimeOffMap, Map<String,ArrayList<String>> agroupTalk)//!!!!Wrong!!!!!!!!!(offThread=new monitorThread(threads, timeOutMap, maxClientsCount)).start();
            break;
          }
        }
        if (i == maxClientsCount) {
          PrintStream os = new PrintStream(clientSocket.getOutputStream());
          os.println("Server too busy. Try later.");
          os.close();
          clientSocket.close();
        }
      } catch (IOException e) {
        System.out.println(e);
      }
    }
  }
}


/*

class monitorThread extends Thread {
	 private long TIME_OUT=30*60*1000;
	 private static Map<String,Demo1> dropMmap=new HashMap<String,Demo1>();
	 private final int maxClientsCount;

	 public monitorThread(Map<String,Demo1> amap,int a)
	 {
		 dropMmap=amap;
		 maxClientsCount=a;
	 }

	 public void run() {

		 while(true){
	 long now= System.currentTimeMillis();

     synchronized (this) {
    	 for (Demo1 element : dropMmap.values()) {
    		 if((now-element.getTime())>TIME_OUT)
    		{
    			 try {
					element.getSocket().close();
				} catch (IOException e) {

					e.printStackTrace();
				}
    				 }
		}

       }
	 }

}
}
*/


