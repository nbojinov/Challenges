import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListModel;


public class ClientWindow extends JFrame {

	private JPanel contentPane;
	ChatClient myClient;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientWindow frame = new ClientWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ClientWindow() {
		final JTextPane textPane = new JTextPane();
		textPane.setEditable(false);
		myClient=new ChatClient("localhost",4321,textPane);
		
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setBounds(100, 100, 792, 593);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		String message="Username:";
		while(true){
			String input =  JOptionPane.showInputDialog(null 
				       ,message);
			System.out.println(input);
			if(input!=null && myClient.tryUsername(input)){
				
				this.setTitle(input);
				break;
			}else
				message="Another username(used):";
		}
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(12, 520, 768, 33);
		contentPane.add(scrollPane_1);
		
		textField = new JTextField();
		scrollPane_1.setViewportView(textField);
		textField.setColumns(10);
		
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 12, 572, 499);
		contentPane.add(scrollPane);
		
		
		scrollPane.setViewportView(textPane);
		
		final JList list = new JList();
		
		Timer timer=new Timer();
		timer.scheduleAtFixedRate(new TimerTask(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String selected=(String)list.getSelectedValue();
				list.clearSelection();
				String s=myClient.getPeople().trim();
				int i=0;
				boolean found=false;
				for(String person:s.split("   ")){
					if (person.equals(selected)){
						found=true;
						break;
					}
					i++;
				}
				list.setListData(s.split("   "));
				if(found){
					list.setSelectedIndex(i);
				} 
			} }, 0, 1000*10);
		
		JScrollPane scrollPane_2 = new JScrollPane(list);
		scrollPane_2.setBounds(591, 12, 189, 499);
		contentPane.add(scrollPane_2);
		
		
		final ServerTalkThread talk=new ServerTalkThread(textPane);
		list.addListSelectionListener(new ListSelectionListener() {
			String selected=null;
			
			Thread thread;
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				// TODO Auto-generated method stub
				if(list.getSelectedValue()!=null && !((String)(list.getSelectedValue())).equals(selected)){
					if(thread!=null){
						talk.writeOut("finish",true);
						thread.stop();
						textPane.setText("");
					}
					selected=(String)list.getSelectedValue();
					System.out.println(selected);
					talk.reset();
					thread = new Thread(talk);
					thread.start();
					myClient.sendServerPortAndAddress(selected,"localhost",talk.getPort());
				}
			}
			
			
		});
		textField.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				talk.writeOut(textField.getText(), false);
			}
		});
		
	}
}
