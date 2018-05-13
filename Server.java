import java.net.*;
import java.nio.charset.StandardCharsets;
import java.io.*;


public class Server
{	

	public static void main(String[] args) throws IOException
	{
		DatagramSocket socket = new DatagramSocket(12321);
		byte[] input = new byte[2000];
		byte[] output = new byte[2000];
		//String inputFromClient;
		String sendToClient;
		
		DatagramPacket packetFromClient = null;
		DatagramPacket packetToClient = null;
		int bytesReceived = 0;
		
		while(true)
		{
			packetFromClient = new DatagramPacket(input, input.length);
			socket.receive(packetFromClient);
			bytesReceived += packetFromClient.getLength();
			String inputFromClient = new String(input, StandardCharsets.UTF_8);
			if(inputFromClient == "Done")
			{
				break;
			}
						
			System.out.println("Client:-" + inputFromClient);
			sendToClient = "ACK " + bytesReceived;
			System.out.println(sendToClient);
			
			InetAddress address = packetFromClient.getAddress();
            int port = packetFromClient.getPort();
            
			output = sendToClient.getBytes();
			packetToClient = new DatagramPacket(output, output.length, address, port);
			socket.send(packetToClient);
			
			
			
		}
		
		socket.close();
	}
}
