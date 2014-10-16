package org.primary;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.swing.JFrame;

public class Main extends JFrame{

	public static final int WIDTH=640;
	public static final int HEIGHT=480;
	public static final int MAX_IMAGE_LENGTH=300;
	public static final int NUMBER_OF_QUESTIONS=7;
	
	public Main() throws MalformedURLException, IOException{
		setTitle("GetToKnowECS");
		add(new Screen(this));
		//setSize(640,480);
		pack();
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
		});
	}
	
	public static void main(String[] args) throws MalformedURLException, IOException{
		JFrame prog=new Main();
		prog.show();
	}
	
}
