import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;


public class ClientThread implements Runnable{

	ChatServer parent;
	Socket socket;
	String name;
	DataInputStream in;
	DataOutputStream out;
	
	public ClientThread(ChatServer parent,Socket socket){
		this.parent=parent;
		this.socket=socket;
		name="";
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		try {
			in=new DataInputStream(socket.getInputStream());
			out=new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
		String firstLine=in.readUTF();
		System.out.println(firstLine);
		if(!firstLine.split(" ")[0].equals("new")){
			out.writeUTF("improper definition");
			socket.close();
			return;
		} else {
				out.writeUTF("success");
		}
		
		while(true){
			String line;
			try {
				line = in.readUTF();
				if(line.split(" ")[0].equals("give")){
					ArrayList<String> people=parent.getPeople();
					String resultStringPeople="result   ";
					for(String person: people){
						if(!person.equals(name))
							resultStringPeople+=person+"   ";
					}
					out.writeUTF(resultStringPeople);
				} else if(line.split(" ")[0].equals("name")){
					String name=line.split(" ")[1];
					if (!parent.getPeople().contains(name)){
						parent.addClient(name, socket.getPort());
						this.name=name;
						out.writeUTF("good name");
					} else {
						out.writeUTF("bad name");
					}
				} else if(line.split(" ")[0].equals("server")){
					parent.connectToThread(line.split(" ")[1], line.split(" ")[2], line.split(" ")[3]);
				}
			} catch (EOFException e) {
				// TODO Auto-generated catch block
				System.out.println("Enough read!");
				break;
			}
		}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void initiateConnection(String address,String port){
		try {
			out.writeUTF("client "+address+" "+port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
