

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ClientThread extends Thread{
	private String clientName = null;
	  private long startTime;
	  private DataInputStream is = null;
	  private PrintStream os = null;
	  private Socket clientSocket = null;
	  private final ClientThread[] threads;
	  private int maxClientsCount;
	  private Map<String,String> map1;
	  private long BLOCK_TIME=60*1000;
	  private long LAST_HOUR=60*60*1000;
	  private boolean block;
	  private String blockBy;
	  private long TIME_OUT=30*60*1000;
	  private long lastQuesTime;
	  private long currentQuesTime;
	  private Map<String,String> offMmap=new HashMap<String,String>();
	  private Map<String,Demo> bmapWrongLogin=new HashMap<String,Demo>();
	  private Map<String,Demo1> btimeoffMap=new HashMap<String,Demo1>();
	  private String fileName="user_pass.txt";
	  private Map<String,ArrayList<String>> groupTalk=new HashMap<String,ArrayList<String>>();
	  private boolean running;
	  private Map<String,Long> logoutti=new HashMap<String,Long>();
	  //private String line="12";

	  public ClientThread(Socket clientSocket, ClientThread[] threads,Map<String,String> amap,Map<String,Demo> bmap,Map<String,String> aoffMmap,Map<String,Demo1> atimeOffMap, Map<String,ArrayList<String>> agroupTalk,Map<String,Long> alogoutti) {
	    this.clientSocket = clientSocket;
	    this.threads = threads;
	    maxClientsCount = threads.length;
	    map1=amap;
	    bmapWrongLogin=bmap;
	    offMmap=aoffMmap;
	    btimeoffMap=atimeOffMap;
	    groupTalk=agroupTalk;
	    logoutti=alogoutti;
	  }

	  public String getname()
	  {
		  return clientName;
	  }

	  public void run() {
	    int maxClientsCount = this.maxClientsCount;
	    ClientThread[] threads = this.threads;
	    running=true;
	    try {
	      /*
	       * Create input and output streams for this client.
	       */
	      is = new DataInputStream(clientSocket.getInputStream());
	      os = new PrintStream(clientSocket.getOutputStream());
	      /*
	       * Check name.
	       */
	      boolean cont=true;
	      String name;
	      int nametime=1;

	      while (true) {
	        os.println("Username: ");
	        name = is.readLine().trim();

		      //os.print(online);
	        long now= System.currentTimeMillis();
	        if (map1.containsKey(name)) {
	        	//os.print(bmapWrongLogin.containsKey(name));
	        	if(bmapWrongLogin.containsKey(name) && (now-bmapWrongLogin.get(name).getTime())<BLOCK_TIME)
	        	{
	        		os.println("you have to wait for " +BLOCK_TIME/1000 +" sec!!!!");
	        		cont=false;
	        		break;
	        	}
		        boolean online=false;
		        ArrayList <String> groupName1=new ArrayList <String>();
	        	synchronized (this) {
	                for (int i = 0; i < threads.length; i++)
	                {

	                  if (threads[i] != null && threads[i] != this)
	                      //&&threads[i].clientName != null)
	                  {
						 groupName1.add(threads[i].clientName);
	                  }
	                }
	        	}
	        	if(groupName1.contains(name))
	        	{
	        		online=true;
	        		//break;
	        	}
			     /* synchronized (this) {
				        for (int i = 0; i < maxClientsCount; i++) {
				          if (threads[i] != null && threads[i] != this && threads[i].clientName.equalsIgnoreCase(name)) {
				            online=true;
				            break;
				          }
				        }
				      }*/
	        	if(online){
	        		cont=false;
	        		os.println("you are already online and can not log in from another console!!!");
	        		break;
	        	}


	        	else{
	          break;}
	        } else {
	        	if(nametime<3){
	          os.println("Wrong username!");
	          nametime++;
	          }
	        	else{
	        		os.println("bye");
	        	}
	        }
	      }

	      /*
	       * Check password.
	       */
	      if(cont)
	      {
	      int passtime=1;
	      String apassword;
	      while (true) {
	        os.println("Password: ");
	        apassword = is.readLine().trim();
	        //System.out.println(map1.get(name));
	        if (map1.get(name).equalsIgnoreCase(apassword)) {
	          break;
	        } else {
	        	if(passtime<3){
	          os.println("Wrong password!");
	          passtime++;
	          }
	        	else{
	        		long time=System.currentTimeMillis();
	        		InetAddress remotIP=clientSocket.getInetAddress();
	        		//os.println(remotIP);
	        		Demo d1=new Demo(time,remotIP);
	        		 synchronized (this) {
	        		bmapWrongLogin.put(name, d1);
	        		 }
	        		os.println("bye");
	        	}
	        }
	      }
	      /* Welcome the new the client. */
	      os.println("Welcome " + name
	          + " to our simple chat roomm. \rCommand: ");
	      synchronized (this) {
	        for (int i = 0; i < maxClientsCount; i++) {
	          if (threads[i] != null && threads[i] == this) {
	            clientName =name;
	            startTime = System.currentTimeMillis();
	            this.block=true;
	            //os.println("start block is "+block);
	            break;
	          }
	        }
	      }

	      /* Print offline message
	       *
	       */
	      synchronized (this) {
	          for (int i = 0; i < maxClientsCount; i++) {
	            if (threads[i] != null && threads[i].offMmap.containsKey(clientName)) {

	              os.println(threads[i].offMmap.get(clientName));
	              offMmap.remove(clientName);
	            }
	          }
	        }

	      /* Start the conversation.
	       * whoelse
	       */
	      int n=0;
	      while (running) {

	    	  try{

	        String line = is.readLine();

	        String[] splitline = line.split("\\s+");
	        currentQuesTime = System.currentTimeMillis();
	        Demo1 element=new Demo1(currentQuesTime,this,clientSocket);

	        synchronized (this) {
	        btimeoffMap.put(clientName, element);}


	        if((currentQuesTime-lastQuesTime)>=TIME_OUT && n>0)
	        {
	        	os.println("It has been a long time since your last command, please log in again!!!!!! ");
	        	line="logout";
	        }

	        if(line.equalsIgnoreCase("whoelse"))
	        	{
					ArrayList <String> groupName=new ArrayList <String>();
	        	synchronized (this) {
	                for (int i = 0; i < threads.length; i++)
	                {

	                  if (threads[i] != null && threads[i] != this&&threads[i].clientName != null)

	                  {
						 groupName.add(threads[i].clientName);
	                  }
	                }
	                if(groupName.size()==0)
	                	os.println("No other clinet, only you!");
	                else
	                {
	                	String names="";
	                	for (int i = 0; i < groupName.size(); i++) {
	                		names=names+groupName.get(i)+ "\r";
						}
	                	//System.out.println("single name is: " +names);
	                   os.println(names);
	                }
	              }
	           }
	        /* add a group to talk*/
	        else if(line.contains("add"))
	        {
	        	String[] splitStr6 = line.split("\\s+");
	        	if(splitStr6.length<2)
	        	{
	        		os.println("Check your input!!");
	        	}
	        	else{
	        	ArrayList<String> groups=new ArrayList<String>();
	        	for(int i=1;i<splitStr6.length;i++)
	        	{
	        		groups.add(splitStr6[i]);
	        	}
	        	groups.add(clientName);
	        	synchronized (this) {
	        	groupTalk.put(clientName, groups);}
	        	os.println("group member succesfully added!");}

	        }
	        else if(line.contains("sent"))
	        {
	        	String[] splitStr6 = line.split("\\s+");
	        	for (ArrayList<String> a : groupTalk.values()) {
					//os.println(a.contains(clientName));
					for (String string : a) {
						//os.println(string);

					}
	        		if(a.contains(clientName))
	        		{

	        			a.remove(clientName);
	        			synchronized (this) {
	                        for (int i = 0; i < maxClientsCount; i++) {
	                          if (threads[i] != null && a.contains(threads[i].clientName)) {

	                        	  threads[i].os.println(clientName+" :"+line.replace("sent", ""));
	                          }
	                        }
	                      }
	        			a.add(clientName);
	        		}
	        }
	        }

	        /* change password*/
	        else if(line.contains("password"))
	        {
	        	String[] splitStr5 = line.split("\\s+");
	        	if(!splitStr5[1].isEmpty()&&splitStr5[1]!=null)
	        	{

	        	os.println("You have successfully changed your password!");

	        	BufferedReader file = new BufferedReader(new FileReader(fileName));
	            String line5;
	            String input = "";

	            while ((line5 = file.readLine()) != null)
	            	input += line5 + '\n';

	            //os.println(input); // check that it's inputed right

	            // this if structure determines whether or not to replace "0" or "1"

	            String new1=input.replace(map1.get(clientName),splitStr5[1]);
	            //os.println(map1.get(clientName));
	            //os.println(splitStr5[1]);
	            // check if the new input is right
	            //os.println("----------------------------------"  + '\n' + new1);

	            // write the new String with the replaced line OVER the same file
	            FileOutputStream File = new FileOutputStream(fileName);
	            File.write(new1.getBytes());
	            synchronized (this) {
	            	map1.put(clientName, splitStr5[1]);}}
	        	else
	        	{
	        		os.println("Change password failed! Change your input");
	        	}
	        }

	        /* block the <user> from sending any message. If user is self dispaly error*/
	        else if(splitline[0].equalsIgnoreCase("block"))
	        {
	        	String[] splitStr4 = line.split("\\s+");
	        	if(splitStr4[1].equalsIgnoreCase(clientName))
	        	{
	        		os.println("Error! you can not block yourself!");
	        	}
	        	else
	        	{
	            	synchronized (this) {
	                    for (int i = 0; i < maxClientsCount; i++) {
	                      if (threads[i] != null && threads[i].clientName!=null&&!threads[i].clientName.isEmpty()&& threads[i].clientName.equals(splitStr4[1]) && threads[i].block==true) {
	                        //threads[i].os.println(threads[i].clientName + " was blocked " );
	                        threads[i].block=false;
	                        threads[i].blockBy=this.clientName;
	                        //os.println(threads[i].clientName+" is blocked"+threads[i].block +"blocked by " +threads[i].blockBy);
	                        os.println("You have successfully blocked "+threads[i].clientName+" from sending you messages.");
	                      }
	                    }
	                  }
	        	}
	        }

	        else if(splitline[0].equalsIgnoreCase("unblock"))
	        {
	        	String[] splitStr4 = line.split("\\s+");
	        	//os.println(splitStr4[1]);
	            	synchronized (this) {
	                    for (int i = 0; i < maxClientsCount; i++) {
	                      if (threads[i] != null && threads[i].clientName!=null&&!threads[i].clientName.isEmpty()&& threads[i].clientName.equals(splitStr4[1])){
	                    	  //os.println("current state is : ");
	                    	  //os.println(threads[i].block);
	                    	  if( threads[i].block)
	                    	  {
	                    		  os.println("unblock error: "+threads[i].clientName +" is already unblocked!!!!");}
	                    	  else
	                    	  { threads[i].block=true;
	                    	  os.println("you have successfully unblocked "+threads[i].clientName);}
	                      }
	                    }
	                  }
	        	}


	        /* The message is public, broadcast it to all other clients. */
	        else if(line.contains("broadcast"))
	        {
	        	String[] splitStr1 = line.split("\\s+");
	        	String names1="";
	        	for (int i = 1; i < splitStr1.length; i++) {
	        		names1=names1+splitStr1[i]+ " ";
				}
	        	synchronized (this) {
	                for (int i = 0; i < maxClientsCount; i++) {
	                  if (threads[i] != null && threads[i].clientName != null && threads[i] != this) {
	                    threads[i].os.println(clientName + ": " + names1);
	                  }
	                }
	              }
	        }

	        /* The message is private, pass it to a specific client. */

	        else if(line.contains("message"))
	        {
	        	String[] splitStr2 = line.split("\\s+");

	        	if(splitStr2.length<3 || !map1.containsKey(splitStr2[1]) )
	        	{
	        		os.println("Check the client name and conntant!");
	        	}
	        	else
	        	{
	            	String names3="";
	            	boolean find=false;
	            	for (int i = 2; i < splitStr2.length; i++) {
	            		names3=names3+splitStr2[i]+ " ";
	    			}



	            	if(splitStr2[1].equalsIgnoreCase(blockBy) && block==false)
	            	{
	            		//os.println("you are blocked by "+splitStr2[1] +"!!!");
	            		os.println("you cannot send any message to "+splitStr2[1] +"You have blocked by the user.");
	            	}
	            	else{

	            	synchronized (this) {
	                    for (int i = 0; i < maxClientsCount; i++) {
	                      if (threads[i] != null && threads[i].clientName!=null&&!threads[i].clientName.isEmpty() &&threads[i].clientName.equalsIgnoreCase(splitStr2[1])) {
	                    	  threads[i].os.println(clientName + ": " + names3);
	                    	  find=true;
	                      }
	                    }
	                  }
	            	}

	            	if(!find && map1.containsKey(splitStr2[1]))
	            	{
	            		if(offMmap.containsKey(splitStr2[1]))
	            		{
	            			String content=offMmap.get(splitStr2[1]);
	            			content+="\r"+clientName + " sending message: " + names3;
	            			offMmap.put(splitStr2[1], content);
	            		}
	            		else
	            		{offMmap.put(splitStr2[1], clientName + " sending message: " + names3);}

	            	}

	        	}
	        }

	        /* Display name of only those users that connected within the last hour. */

	        else if(line.equalsIgnoreCase("wholasthr"))
	    	{
	        	long endTime=System.currentTimeMillis();
				ArrayList <String> groupName2=new ArrayList <String>();
				//System.out.println("comes to whoelse");
	    	synchronized (this) {
				//System.out.println("comes to synchronize");
	            for (int i = 0; i < threads.length; i++)
	            {
	              if (threads[i] != null && threads[i] != this &&threads[i].clientName != null)

	              {
					 //System.out.println("comes to print wholeelse"+threads[i].clientName);
					 groupName2.add(threads[i].clientName);
	              }
	            }
	            for (Long l : logoutti.values()) {
	            	if((endTime-l)<=LAST_HOUR)
	            	{
	            		for (Entry<String,Long> entry : logoutti.entrySet()) {
	            			if(l==entry.getValue())
	            			{
	            				groupName2.add(entry.getKey());
	            			}

						}
	            	}

				}
	    	}
	            //System.out.println(groupName2.size());
	            if(groupName2.size()==0)
	            	os.println("No other clinet, only you!");
	            else
	            {
	            	String names="";
	            	for (int i = 0; i < groupName2.size(); i++) {
	            		names=names+groupName2.get(i)+ "\r";
					}
	               os.println(names);
	            }

	       }

	        else if (line.contains("logout")) {

	        	long logout= System.currentTimeMillis();

	        	synchronized (this) {
	        	logoutti.put(clientName, logout);}
	        	os.println("bye ");
	          break;
	        }
	        else
	        {
	        	os.println("Please doubl check your command, system can not recognize!!!!!!" );
	        }
	        lastQuesTime=currentQuesTime;
	        n++;}
	    	  catch(IOException e){
	    		  e.printStackTrace();
	    		  break;
	    	  }
	        //String line = is.readLine();
	      }
	      os.println("!!!!!bye " + name + "!!!!!, see you next time");

	      /*
	       * Clean up. Set the current thread variable to null so that a new client
	       * could be accepted by the server.
	       */
	      synchronized (this) {
	        for (int i = 0; i < maxClientsCount; i++) {
	          if (threads[i] == this) {
	            threads[i] = null;
	          }
	        }
	      }
	      }
	      /*
	       * Close the output stream, close the input stream, close the socket.
	       */
	      is.close();
	      os.close();
	      clientSocket.close();
	    } catch (IOException e) {
	    }
	  }

	  public void stopRunning()
	  {
		  running=false;
	  }




}

