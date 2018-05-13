import java.net.*;
import java.io.*;
import java.text.NumberFormat;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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

		// loop until all input parameters are met, then continue
		while(true) {
			System.out.println("Please indicate the website you want, the number of bytes, and timeout");
			String input = scanner.nextLine();

			try {
				startTime = System.nanoTime();
				userInput = input.split(" ");

				// ensure that the user passed three parameters
				if (userInput.length != 3) throw new Exception("Please enter data in the form of URL BYTES TIMEOUT, separated by spaces.");

				// ensure that the second and third parameters are numbers
				Integer.parseInt(userInput[1]);
				Integer.parseInt(userInput[2]);

				// ensure that the first parameter matches the domain regex
				Pattern p = Pattern.compile("(http:\\/\\/[www\\.]*[A-z]+\\.[A-z]+)");
				Matcher m = p.matcher(userInput[0]);

				if (!m.find()) {
					throw new Exception("Please make sure your domain is in the format 'http://yourdomain.tld'!");
				}

				break;
			} catch (Exception error) {
				// catch number format exceptions
				if (error instanceof NumberFormatException) {
					System.out.println("Error! Please make sure that the BYTES and the TIMEOUT parameters are integers!");
				} else {
					System.out.println(error);
				}

			}
		}

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


