
import java.net.*;
import java.util.*;

public class HServer extends Thread
{
	private int port;
	private ServerSocket server;
	private boolean contListening=true;

	public HServer(int port)
	{
		this.port = port;
		System.out.println("HServer initialized call start, to start the server");
	}
	@Override
	public void run()
	{
		try
		{
			server = new ServerSocket(port);
			System.out.println("server created on port: " + port);
			while (contListening)
			{
				Socket s=server.accept();
				new ClientHandler(s).start();
			}
		}
		catch (Exception e)
		{
			System.out.println("error :" + e);
		}
	}
}
