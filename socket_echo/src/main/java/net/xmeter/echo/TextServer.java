package net.xmeter.echo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class TextServer {
	public static AtomicInteger sessions = new AtomicInteger(0);
	
	public void handleRequest(final Socket socket) {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		
		executor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					PrintWriter os = new PrintWriter(socket.getOutputStream());
					while(true) {
						String line = is.readLine();
						if(line == null) {
							System.out.println("Probably the client side closed the connection, now close me as well.");
							socket.close();
							break;
						}
						System.out.println("Received message: " + line);
						os.println("Echo: " + line);
						os.flush();
						if("bye".equals(line)) {
							break;
						}
					}
				} catch(Exception ex) {
					ex.printStackTrace();
				} finally {
					try {
						socket.close();
						int num = sessions.decrementAndGet();
						System.out.println("Now totally has " + num + " of conn.");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
		});
		
	}
	
	public static void main(String[] args) {
		try {
			ServerSocket server = new ServerSocket(4700);
			while(true) {
				Socket socket = server.accept();
				TextServer srv = new TextServer();
				srv.handleRequest(socket);	
				int num = sessions.incrementAndGet();
				System.out.println("Received new conn, now totally has " + num + " of conn.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
