

import java.net.Socket;

public class Demo1{
	private long time;
	private ClientThread threadsingle;
	private Socket clientSocket;

	public Demo1(long atime,ClientThread athreadsingle,Socket aclientSocket)
	{
		time=atime;
		threadsingle=athreadsingle;
		clientSocket=aclientSocket;
	}
	public long getTime()
	{
		return time;
	}
	public Socket getSocket()
	{
		return clientSocket;
	}
	public ClientThread getThread()
	{
		return threadsingle;
	}
	public void setThread(ClientThread athreadsingle)
	{
		threadsingle=athreadsingle;
	}
	public void setTime(long atime)
	{
		time=atime;
	}
}

