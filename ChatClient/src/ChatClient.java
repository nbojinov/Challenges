import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JTextPane;


public class ChatClient {
	
	String address;
	int PORT;
	Socket socketToServer;
	DataInputStream in;
	DataOutputStream out;
	JTextPane textPane;
	
	public ChatClient(String address, int port,JTextPane textPane){
		this.textPane=textPane;
		this.address=address;
		PORT=port;
		set();
	}
	
	public void set(){
		try {
			socketToServer=new Socket(address,PORT);
			in= new DataInputStream(socketToServer.getInputStream());
			out=new DataOutputStream(socketToServer.getOutputStream());
			out.writeUTF("new "+socketToServer.getLocalPort());
			in.readUTF();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public String getPeople(){
		String result="";
		try {
			out.writeUTF("give");
			result=checkForIncoming(in.readUTF());
			
			if(!result.split("   ")[0].equals("result")){
				result="Error from server!";
			} else {
				result=result.substring(6);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result="Error in fetching!";
		}
		return result;
	}

	private String checkForIncoming(String result) {
		// TODO Auto-generated method stub
		if(result.split(" ")[0].equals("client")){
			ClientTalkThread talk=new ClientTalkThread(result.split(" ")[1],result.split(" ")[2],textPane);
			Thread thread = new Thread(talk);
			thread.start();
			try {
				return in.readUTF();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	public boolean tryUsername(String input) {
		// TODO Auto-generated method stub
		try {
			out.writeUTF("name "+input);
		if(checkForIncoming(in.readUTF()).equals("good name"))
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	

	public void sendServerPortAndAddress(String selected, String address,
			int port) {
		// TODO Auto-generated method stub
		try {
			out.writeUTF("server "+selected+" "+address+" "+port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
