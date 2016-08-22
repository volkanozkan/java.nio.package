package client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Random;

public class MainClassClient {
	
	private static int responseNumber;
	private static int[] numbersToSend;
	
	public static void main(String[] args) throws IOException, InterruptedException {

		// Open New Connection.
		try (SocketChannel client = SocketChannel.open()) {
			client.connect(new InetSocketAddress("localhost", 3333));
			System.out.println("Client Succesfully Started.");

			numbersToSend = new int[3];
			for (int i = 0; i < numbersToSend.length; i++) {
				// Generate Random Integer With Range 0-9 To Send Server.
				numbersToSend[i] = new Random().nextInt(9);

				// Send Number To Server
				byte[] message = new String(String.valueOf(numbersToSend[i])).getBytes();
				ByteBuffer byteBuffer = ByteBuffer.wrap(message);
				client.write(byteBuffer);
				System.out.println(numbersToSend[i] + " was sent.");
				ByteBuffer buffer = ByteBuffer.allocate(8192);
				
				// Get Response Text
				responseNumber = client.read(buffer);
				byte[] byteDate = new byte[responseNumber];
				System.arraycopy(buffer.array(), 0, byteDate, 0, responseNumber);
				System.out.println("Response: " + new String(byteDate));
				byteBuffer.clear();
				Thread.sleep(3000);
			}

		}
	}
}
