import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;


public class ChatServer {
	
	ServerSocket serverSocket;
	HashMap<String,Integer> clients;
	HashMap<Integer,ClientThread> threads;
	
	public ChatServer(int port){
		try {
			serverSocket=new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		clients=new HashMap<String,Integer>();
		threads=new HashMap<Integer,ClientThread>();
	}
	
	public void start(){
		
		
		while(true){
			Socket clientSocket=null;
			try {
				clientSocket=serverSocket.accept();
				ClientThread clientThread=new ClientThread(this,clientSocket);
				Thread thread=new Thread(clientThread);
				thread.start();
				threads.put(clientSocket.getPort(), clientThread);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}

	
	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		serverSocket.close();
	}

	public synchronized void writeToConsole(String line) {
		// TODO Auto-generated method stub
		System.out.println(line);
	}

	public void addClient(String user,Integer port) {
		// TODO Auto-generated method stub
		clients.put(user,port);
	}

	public ArrayList<String> getPeople() {
		// TODO Auto-generated method stub
		return new ArrayList<String>(clients.keySet());
	}
	
	public void connectToThread(String selected,String address,String port){
		threads.get(clients.get(selected)).initiateConnection(address, port);
	}
}
