package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import problemdomain.ClientHandler;

public class ServerDriver {

	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		Socket socket = null;
		ArrayList<Socket> socketList = new ArrayList<Socket>();
		ClientHandler clientHandler = null;
		
		try {
			serverSocket = new ServerSocket(1234);
			System.out.println("Waiting for users");
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		
		while ( true ) {
			try {
				socket = serverSocket.accept();
				socketList.add(socket);
				if ( socketList.size() == 2 ) {
					clientHandler = new ClientHandler(socketList.get(0), socketList.get(1));
					socketList.clear();
				} 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	}


