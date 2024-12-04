
import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler extends Thread
{
	private Socket socket;
	private File home;
	public ClientHandler(Socket s)
	{
		this.socket = s;
		this.home = new File("/sdcard/documents/page/");
	}

	@Override
	public void run()
	{
		try
		{
			System.out.println("————————————————————————————————————————————");
			System.out.println("+++ new client connected ::" + socket.getInetAddress());
			InputStream is=socket.getInputStream();
			byte[] b=new byte[1024];
			boolean cont=true;
			StringBuilder header=new StringBuilder();
			while (cont)
			{
				is.read(b);
				String s=new String(b);
				if (s.contains("\r\n\r\n"))
					cont = false;
				header.append(s.trim());
			}
			Header hdr=parseHeader(header.toString().trim());
			handleTransmission(hdr);
			System.out.println("... Transmissiom completed.");
		}
		catch (Exception e)
		{
			System.out.println("error on client handler :" + e);
		}
	}
	public Header parseHeader(String hdr)
	{
		Header h=new Header();
		if (hdr.trim().length() == 0)
			return h;
		String[] lns=hdr.split("\n");
		int pos=0;
		for (String ln:lns)
		{
			if (ln.trim().length() == 0)
				continue;
			if (pos == 0) // handle request(Get,Post,Put,Delete,...);
			{
				String tp=ln.substring(0, ln.indexOf(" ")).trim(); // request type
				String loc=ln.substring(ln.indexOf(" "), ln.lastIndexOf(" ")).trim(); // location 
				String httpV=ln.substring(ln.lastIndexOf(" ")).trim(); // http version.
				h.type = tp;
				h.location = loc;
				h.httpVer = httpV;
			}
			else
			{ // add key an vakue to HashMap.headers.
				String k=ln.substring(0, ln.indexOf(":")).trim();
				String v=ln.substring(ln.indexOf(":") + 1).trim();
				h.infos.put(k, v);
			}
			pos++;
		}
		return h;
	}
	public void handleTransmission(Header h)
	{
		System.out.println("handling transmission");
		System.out.println(h);
		if (h.type.equals("GET"))
		{
			if (h.location.equals("/"))
				h.location = "/index.html";
			parseData(h.location);
		}
		else
			System.out.println("unknown header type :" + h.type);
	}
	public void parseData(String path)
	{
		System.out.println("parsing data");
		File d=new File(home, path);
		if (!d.exists())
		{
			sendNoFound(path);
			return;
		}
		String ftp=getMimeType(path);
		sendFile(d, ftp);
	}
	public String getMimeType(String p)
	{
		String tp=p.substring(p.lastIndexOf(".") + 1).trim();
		String type="*/*";
		switch (tp)
		{
			case "html":
				type = "text/html";
				break;
			case "ico":
				type = "image/*";
				break;
		}
		System.out.println("detected mime type for :" + tp + " ::" + type);
		return type;
	}
	public void sendFile(File d, String mm)
	{
		try
		{
			System.out.println("sending file :" + d + " with mimeType :" + mm);
			OutputStream os=socket.getOutputStream();
			FileInputStream fis=new FileInputStream(d);
			String hdr=
				"HTTP/1.1 200 OK\r\n" +
				"Content-Type: " + mm + "\r\n" +
				"Date: " + new Date().toGMTString() + "\r\n" +
				"Connection: keep-alive\r\n" +
				"Content-Length: " + fis.available() + "\r\n\r\n";
			os.write(hdr.getBytes());
			byte[] b=new byte[1024];
			int rd=0;
			while ((rd = fis.read(b)) != -1)
			{
				System.out.print(".");
				os.write(b, 0, rd);
			}
			os.close();
			System.out.println("\nfile sent");
		}
		catch (Exception e)
		{
			System.out.println("error sending file :" + e);
			e.printStackTrace();
		}
	}
	public void sendNoFound(String p)
	{
		try
		{
			System.out.println("sending not found message");
			String txt="<p>web pages are in progress</p>";
			String hdr=
				"HTTP/1.1 200 OK\r\n" +
				"Content-Type: text/html\r\n" +
				"Date: " + new Date().toGMTString() + "\r\n" +
				"Connection: keep-alive\r\n" +
				"Content-Length: " + txt.length() + "\r\n\r\n" +
				txt + "\r\n";
			OutputStream os=socket.getOutputStream();
			os.write(hdr.getBytes());
			os.flush();
			os.close();
			System.out.println("not found sent.");
		}
		catch (Exception e)
		{
			System.out.println("error on notFoundHandlee :" + e);
		}
	}
	public class Header
	{
		private HashMap<String,String> infos=new HashMap<>();

		public String type="";
		public String location="";
		public String httpVer="";

		public String get(String k)
		{
			String s= infos.get(k);
			if (s == null)
				s = "";
			return s;
		}
		@Override
		public String toString()
		{
			return ">> (." + type + ". ." + location + ". ." + httpVer + ".)" + infos;
		}
	}
}
