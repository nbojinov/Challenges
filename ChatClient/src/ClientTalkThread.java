
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JTextPane;


public class ClientTalkThread implements Runnable{

	Socket socket;
	DataInputStream in;
	DataOutputStream out;
	JTextPane textPane;
	
	public ClientTalkThread(String address, String port, JTextPane textPane){
		this.textPane=textPane;
		System.out.println(port);
		Integer portInt=Integer.parseInt(port);
		try {
			socket=new Socket(address,portInt);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			in=new DataInputStream(socket.getInputStream());
			out=new DataOutputStream(socket.getOutputStream());
		
		
		while(true){
			String line=in.readUTF();
			if(line.equals("finish")){
				socket.close();
			}
			textPane.setText(textPane.getText()+"\n"+line);
		}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeOut(String text) {
		// TODO Auto-generated method stub
		try {
			out.writeUTF(text);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
