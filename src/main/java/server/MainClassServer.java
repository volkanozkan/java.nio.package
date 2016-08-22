package server;

import java.io.IOException;

public class MainClassServer {
	
	public static void main(String[] args) throws Exception {
		// Run Server On 3333 Port.
		Runnable runnableServer = new Runnable() {
			@Override
			public void run() {
				try {
					new Server("localhost", 3333).startServer();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(runnableServer).start();
	}
}
