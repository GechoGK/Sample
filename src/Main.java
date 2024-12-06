import java.util.*;
import java.net.*;
import java.io.*;

public class Main
{
	public static void main(String[] args) throws Exception
	{
     // start the server test function.
		new Main().server();
	
	}
	void server() throws Exception
	{
     // get up address from wlan0
		NetworkInterface inf= NetworkInterface.getByName("wlan0");
		NetworkInterface inf2=NetworkInterface.getByName("p2p-wlan0-0");
		if (inf != null)
			System.out.println("adrs " + inf.getInterfaceAddresses());
		if (inf2 != null)
			System.out.println("adrs2 " + inf2.getInterfaceAddresses());

     // start the server on port 1233.
		HServer server=new HServer(1234);
		server.start();


	}
}
