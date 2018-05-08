import java.net.*;
import java.io.*;
import java.util.Scanner;


public class Client
{
	
	public static void main(String args[]) throws Exception
	{
		Scanner scanner = new Scanner(System.in);
		DatagramSocket socket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getLocalHost();
		byte buffer[] = null;
		byte[] ACKFromServer = new byte[2000];
		String[] userInput = null;
		String httpGet;
		int z;
		int total = 0;
		boolean receiveACK = false;
		DatagramPacket packetFromServer = null;
		int failure;
		long startTime;
		long endTime;
		long timeElapsed;
		
		
		System.out.println("Please indicate the website you want, the number of bytes, and timeout");
		String input = scanner.nextLine();
		startTime = System.nanoTime();
		userInput = input.split(" ");
		for(int x = 0; x <3; x++)
		{
			System.out.println(userInput[x]);
		}
			
		z = Integer.parseInt(userInput[1]);
		if (z > 1460)
		{
			z = 1460;
		}
			
		buffer = new byte[z];
		StringBuilder results = new StringBuilder();
		String temp;
		URL inputURL = new URL(userInput[0]);
		HttpURLConnection connection = (HttpURLConnection) inputURL.openConnection();
		connection.setRequestMethod("GET");
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		socket.setSoTimeout(Integer.parseInt(userInput[2]));
		while ((temp = reader.readLine()) != null)
		{
			results.append(temp);
			if((httpGet = results.toString()).getBytes().length >= z )
			{
				failure = 0;
				receiveACK = false;
				clientThread thread = new Client().new clientThread(httpGet);
				thread.start();
				
				buffer = httpGet.getBytes();
				DatagramPacket sendToServer = new DatagramPacket(buffer, buffer.length, IPAddress, 12321);
				socket.send(sendToServer);
					
					
				while(receiveACK == false && failure < 3)
				{
					failure++;
					try
					{
						packetFromServer = new DatagramPacket(ACKFromServer, ACKFromServer.length);
						
						socket.receive(packetFromServer);
							
						receiveACK = true;
						System.out.println("ACK received from server");
							
						total += buffer.length;
					}
						
					catch (SocketTimeoutException e)
					{

						socket.send(sendToServer);
							
							
					}
				}
					
				if(failure == 3)
				{
					httpGet = null;
					httpGet = "DONE";
					buffer = null;
					buffer = httpGet.getBytes();
					sendToServer = new DatagramPacket(buffer, buffer.length, IPAddress, 12321);
					return;
				}
					
				results = new StringBuilder();
			}
		}
			
		httpGet = results.toString();
		clientThread thread = new Client().new clientThread(httpGet);
		thread.start();
		buffer = httpGet.getBytes();
		DatagramPacket sendToServer = new DatagramPacket(buffer, buffer.length, IPAddress, 12321);
		total += buffer.length;
		socket.send(sendToServer);
		failure = 0;
		receiveACK = false;
		while(receiveACK == false && failure < 3)
		{
			failure++;
			try
			{
				packetFromServer = new DatagramPacket(ACKFromServer, ACKFromServer.length);
				
				socket.receive(packetFromServer);
					
				receiveACK = true;
				System.out.println("ACK received from server");
					
				total += buffer.length;
			}
				
			catch (SocketTimeoutException e)
			{

				socket.send(sendToServer);
					
					
			}
		}
			
		if(failure == 3)
		{
			httpGet = null;
			httpGet = "DONE";
			buffer = null;
			buffer = httpGet.getBytes();
			sendToServer = new DatagramPacket(buffer, buffer.length, IPAddress, 12321);
			return;
		}
		
		endTime = System.nanoTime();
		timeElapsed = endTime -startTime;
		Thread.sleep(10);	
		System.out.println("Done\nTotal bytes sent: " + total);
		System.out.println("Total execution time: " + timeElapsed + " nano seconds");
		reader.close();		
		scanner.close();
		socket.close();	
	}
	

	//http://www.google.com 2000 200
	
	public class clientThread extends Thread
	{
		String parameter;
		public clientThread(String httpInfo)
		{
			 this.parameter = httpInfo;
		}
		public void run()
		{
			System.out.println(parameter);
		}

	}
	
}


