

import java.net.InetAddress;


public class Demo {
	private long time;
	private InetAddress IP;

	public Demo(long atime,InetAddress aIP)
	{
		time=atime;
		IP=aIP;
	}
	public long getTime()
	{
		return time;
	}
	public InetAddress getIP()
	{
		return IP;
	}
}