import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JTextPane;


public class ServerTalkThread implements Runnable{

	ServerSocket serverSocket;
	DataInputStream in;
	DataOutputStream out;
	int port;
	JTextPane textPane;
	
	public ServerTalkThread(JTextPane textPane){
		this.textPane=textPane;
		reset();
	}
	
	public int getPort(){
		return port;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			Socket clientSocket=serverSocket.accept();
			in=new DataInputStream(clientSocket.getInputStream());
			out=new DataOutputStream(clientSocket.getOutputStream());
		
		
		while(true){
			String line=in.readUTF();
			
			if(line.equals("finish")){
				clientSocket.close();
				return;
			}
			textPane.setText(textPane.getText()+"\n"+line);
		}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeOut(String text,boolean end) {
		// TODO Auto-generated method stub
		try {
			if(out!=null){
				out.writeUTF(text);
				if(end){
					serverSocket.close();
					out=null;
					in=null;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void reset() {
		// TODO Auto-generated method stub
		try {
			serverSocket=new ServerSocket(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		port=serverSocket.getLocalPort();
		System.out.println("here "+port);
	}

}
