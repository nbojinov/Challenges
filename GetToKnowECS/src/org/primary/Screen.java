package org.primary;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Screen extends JPanel{
	
	private BufferedImage img;
	private ArrayList<Person> people, chosenPeople, nextChosenPeople, wronglyGuessedPeople;
	private ArrayList<JRadioButton> buttons;
	private JLabel picture;
	private int chosen,nextChosen,correct,current;
	private JButton button;
	private boolean waiting,first;
	private Main parent;
	
	public Screen(Main p){
		super();
		parent=p;
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		correct=0;
		current=1;
		
		JRadioButton button1 = new JRadioButton();
		JRadioButton button2 = new JRadioButton();
		JRadioButton button3 = new JRadioButton();
		
		first=true;
		waiting=false;
		
        ButtonGroup group=new ButtonGroup();
        group.add(button1);
        group.add(button2);
        group.add(button3);
        
        buttons=new ArrayList<JRadioButton>();
        buttons.add(button1);
        buttons.add(button2);
        buttons.add(button3);
		
		picture=new JLabel(); 
		picture.setPreferredSize(new Dimension(Main.MAX_IMAGE_LENGTH,Main.MAX_IMAGE_LENGTH));
		
		button=new JButton("Next!");
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int i=-1;
				for(int j=0;j<buttons.size();j++)
					if(buttons.get(j).isSelected())
						i=j;
				if(chosen==i)
					correct++;
				else
					wronglyGuessedPeople.add(chosenPeople.get(chosen));
				current++;
				if(current==Main.NUMBER_OF_QUESTIONS+1)
					exitScreen();
				else
					updateScreen();
			}
		});
		
		add(picture);
		add(button1);
		add(button2);
		add(button3);
		add(button);
		
		System.out.println("Starting extraction...");
		Extractor extractor = new Extractor("http://www.ecs.soton.ac.uk/people/search?role=&group=&nameq=&picsize=pic&submitted=1&Submit=%C2%A0go%C2%A0");
		people = extractor.getPeople();
		
		chosenPeople=new ArrayList<Person>();
		wronglyGuessedPeople=new ArrayList<Person>();
		nextChosenPeople=new ArrayList<Person>();
		System.out.println("Extraction finished!");
		
		if(people == null){
			System.out.println("Problem");
			System.exit(0);
		}
		
		updateScreen();
	}
	
	private void updateScreen(){
		chosenPeople=new ArrayList<Person>();
		if (nextChosenPeople.size()==0){
			chooseNextChosenPeople();
		}
		chosenPeople=nextChosenPeople;
		chosen=nextChosen;
		for(int i=0;i<buttons.size();i++){
			buttons.get(i).setText(chosenPeople.get(i).getName());
		}
		buttons.get(0).setSelected(true);
		addPictureToScreen(chosenPeople.get(chosen).getImageUrl());
		parent.pack();
		if(current!=Main.NUMBER_OF_QUESTIONS){
			nextChosenPeople=new ArrayList<Person>();
			chooseNextChosenPeople();
		}
		Thread thread=new Thread(){
			
			@Override
			public void run(){
				waiting=true;
				System.out.println(nextChosenPeople.get(nextChosen).getImageUrl());
				img=getPicture(nextChosenPeople.get(nextChosen).getImageUrl());
				waiting=false;
				System.out.println("Got it");
			}
		};
		thread.start();
	}

	private void chooseNextChosenPeople() {

		Random rand=new Random();
		for(int i=0;i<3;i++){
			int index=rand.nextInt(people.size());
			Person p=people.get(index);
			p.getName().replace("&quot;", "\"");
			nextChosenPeople.add(p);
			people.remove(index);
		}

		nextChosen=rand.nextInt(3);
	}
	
	private void addPictureToScreen(String url){
		if(first){
			img=getPicture(url);
			first=false;
		} else {
			while (waiting){
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		ImageIcon icon=new ImageIcon(img);
		picture.setIcon(icon);
	}

	private BufferedImage getPicture(String url) {
		BufferedImage temp=null;
		try {
			temp=ImageIO.read(new URL(url));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		
		int height,width;
		if(temp.getHeight(null)>temp.getWidth(null)){
			height=Main.MAX_IMAGE_LENGTH;
			width=(int)((double)temp.getWidth(null)*height/temp.getHeight(null));
		} else {
			width=Main.MAX_IMAGE_LENGTH;
			height=(int)((double)temp.getHeight(null)*width/temp.getWidth(null));
		}
		
		int type = temp.getType() == 0? BufferedImage.TYPE_INT_ARGB : temp.getType();
		BufferedImage resizedImage = new BufferedImage(width, height, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(temp, 0, 0, width, height, null);
		g.dispose();
		
		return resizedImage;
	}
	
	private void exitScreen(){
		String message="You got "+correct+" correct!";
		if(wronglyGuessedPeople.size()>0){
			message+="\nYou might want to look up:";
			for(Person person: wronglyGuessedPeople){
				message+="\n"+person.getName();
			}
		}
		JOptionPane.showMessageDialog(null,message);
		System.exit(0);
	}
}
