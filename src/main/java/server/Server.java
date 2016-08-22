package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class Server {
	
	private int responseNumber = 0;
	private byte[] message;

	private Selector selector;
	private Map<SocketChannel, List<byte[]>> dataMapper;
	private InetSocketAddress socketAdress;

	public Server(String address, int port) throws IOException {
		socketAdress = new InetSocketAddress(address, port);
		dataMapper = new HashMap<SocketChannel, List<byte[]>>();

		// Random Number With Range 0-9.
		responseNumber = new Random().nextInt(9);
	}

	// Start Server
	public void startServer() throws IOException {
		this.selector = Selector.open();
		ServerSocketChannel serverChannel = ServerSocketChannel.open();
		serverChannel.configureBlocking(false);

		// Bind To Port.
		serverChannel.socket().bind(socketAdress);
		serverChannel.register(this.selector, SelectionKey.OP_ACCEPT);
		System.out.println("Server Succesfully Started.");

		// Infinite Loop.
		while (true) {
			this.selector.select();
			Iterator<SelectionKey> keys = this.selector.selectedKeys().iterator();
			while (keys.hasNext()) {
				SelectionKey key = (SelectionKey) keys.next();
				// If Same Key Came Up.
				keys.remove();
				if (!key.isValid()) {
					continue;
				}
				if (key.isAcceptable()) {
					this.acceptConnection(key);
				} else if (key.isReadable()) {
					this.read(key);
				}
			}
		}
	}

	// Accept Connection 
	private void acceptConnection(SelectionKey key) throws IOException {
		ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
		SocketChannel channel = serverChannel.accept();
		channel.configureBlocking(false);

		dataMapper.put(channel, new ArrayList<byte[]>());
		channel.register(this.selector, SelectionKey.OP_READ);
	}

	// Read. If No Read Close Channel. Then Call Compare Method.
	private void read(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(8192);
		int numberToRead = -1;
		numberToRead = channel.read(buffer);
		if (numberToRead == -1) {
			this.dataMapper.remove(channel);
			channel.close();
			key.cancel();
			return;
		}
		byte[] byteData = new byte[numberToRead];
		System.arraycopy(buffer.array(), 0, byteData, 0, numberToRead);
		compareNumber(Integer.valueOf(new String(byteData)), channel);
	}

	// Compare Two Numbers And Send Response.
	private void compareNumber(int guess, SocketChannel channel) throws IOException {
		if (responseNumber == guess) {
			message = new String("Congratulations").getBytes();
		} else if (responseNumber > guess) {
			message = new String("Correct Value: " + responseNumber + " * Try bigger than " + guess).getBytes();
		} else {
			message = new String("Correct Value: " + responseNumber + " * Try less than " + guess).getBytes();
		}
		ByteBuffer buffer = ByteBuffer.wrap(message);
		channel.write(buffer);
	}
}