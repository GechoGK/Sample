import java.util.*;
import java.net.*;
import java.io.*;

public class Main
{
	public static void main(String[] args) throws Exception
	{
		new Main().server();
		// new Main().cl();
		// Date d=new Date();
		// System.out.println( d.toGMTString());

	}
	void cl() throws IOException
	{
		Socket ss=new Socket("", 6789);
		System.out.println("connected");
		DataOutputStream dos=new DataOutputStream(ss.getOutputStream());
		dos.writeBytes("Get /dhdb http/1.1");
		dos.writeBytes("\r\n\r\n");
		System.out.println("request sent. waiting for incomin msg");
		InputStream is=ss.getInputStream();
		byte[] b=new byte[1024];
		int rd=0;
		while ((rd = is.read(b)) != 0 && rd != -1)
			System.out.println(new String(b));

		System.out.println("-------- done! --------");

	}
	void server() throws Exception
	{
		NetworkInterface inf= NetworkInterface.getByName("wlan0");
		NetworkInterface inf2=NetworkInterface.getByName("p2p-wlan0-0");
		if (inf != null)
			System.out.println("adrs " + inf.getInterfaceAddresses());
		if (inf2 != null)
			System.out.println("adrs2 " + inf2.getInterfaceAddresses());

		HServer server=new HServer(1234);
		server.start();


	}
}
