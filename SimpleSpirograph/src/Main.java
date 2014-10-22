//formulas and insights into SpiroGraphs http://www.personal.psu.edu/dpl14/java/parametricequations/spirograph/SpiroGraph1.0/index.html

import java.applet.Applet;
import java.awt.Button;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Main extends Applet implements ActionListener,ChangeListener{
	
	static final int HEIGHT=700;
	static final int WIDTH=500;
	static final int CANVAS_HEIGHT=500;
	static final int CANVAS_WIDTH=500;
	
	MyCanvas canvas;
	Button speedUp,speedDown,start;
	JSlider RSlider,rSlider,OSlider;
	Label RLabel,rLabel,OLabel;
	boolean setPlaceOfComponents,runThread;
	int waitTime;
	Thread thread;
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		this.setSize(WIDTH, HEIGHT);
		canvas=new MyCanvas(this,CANVAS_WIDTH,CANVAS_HEIGHT);
		
		speedUp=new Button("Speed +");
		speedDown=new Button("Speed -");
		start=new Button("Start");
		
		speedUp.addActionListener(this);
		speedDown.addActionListener(this);
		start.addActionListener(this);
		
		RSlider = new JSlider(JSlider.HORIZONTAL,
                0, 150, 75);
		RSlider.setMajorTickSpacing(30);
		RSlider.setMinorTickSpacing(10);
		RSlider.setPaintTicks(true);
		RSlider.setPaintLabels(true);
		RSlider.addChangeListener(this);
		
		rSlider = new JSlider(JSlider.HORIZONTAL,
                -100, 100, 20);
		rSlider.setMajorTickSpacing(40);
		rSlider.setMinorTickSpacing(10);
		rSlider.setPaintTicks(true);
		rSlider.setPaintLabels(true);
		rSlider.addChangeListener(this);
		
		OSlider = new JSlider(JSlider.HORIZONTAL,
                -100, 100, 10);
		OSlider.setMajorTickSpacing(40);
		OSlider.setMinorTickSpacing(10);
		OSlider.setPaintTicks(true);
		OSlider.setPaintLabels(true);
		OSlider.addChangeListener(this);
		
		RLabel=new Label("R=100");
		rLabel=new Label("r=20");
		OLabel=new Label("O=10");
		
		//this.add(canvas);
		this.add(speedUp);
		this.add(speedDown);
		this.add(start);
		this.add(RSlider);
		this.add(rSlider);
		this.add(OSlider);
		this.add(RLabel);
		this.add(rLabel);
		this.add(OLabel);
		
		setPlaceOfComponents=false;
		waitTime=10;
		runThread=false;
		
		thread=setThread();
		Timer timer=new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				repaint();
			}
		}, 100);
	}
	
	public Thread setThread(){
		return new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(runThread){
					long start=System.currentTimeMillis();
					if(canvas.isPainting()){
						repaint();
					}
					long end=System.currentTimeMillis();
					if(waitTime-(end-start)>0)
						try {
							Thread.sleep(waitTime-(end-start));
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
			}
		});
	}
	
	@Override
	public void update(Graphics g) {
		// TODO Auto-generated method stub
		System.out.println("update");
		if(!setPlaceOfComponents){
			setPlaceComponents();
			setPlaceOfComponents=true;
		}
		canvas.update(g);
	}
	
	private void setPlaceComponents() {
		// TODO Auto-generated method stub
		speedUp.setLocation(10, 540);
		speedDown.setLocation(10, 610);
		RLabel.setLocation(190,510);
		RSlider.setLocation(80, 540);
		rLabel.setLocation(190,585);
		rSlider.setLocation(80, 610);
		OLabel.setLocation(400,510);
		OSlider.setLocation(300, 540);
		start.setLocation(400,610);
	}

	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==speedUp && waitTime>0){
			speedDown.setEnabled(true);
			waitTime-=2;
			if(waitTime==0)
				speedUp.setEnabled(false);
		}else if(e.getSource()==speedDown && waitTime<20){
			speedUp.setEnabled(true);
			waitTime+=2;
			if(waitTime==20)
				speedDown.setEnabled(false);
		} else if(e.getSource()==start){
			start.setEnabled(false);
			runThread=true;
			canvas.start(RSlider.getValue(), rSlider.getValue(), OSlider.getValue());
			thread.start();
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==RSlider){
			RLabel.setText("R="+RSlider.getValue());
		} else if(e.getSource()==rSlider){
			rLabel.setText("r="+rSlider.getValue());
		} else if(e.getSource()==OSlider){
			OLabel.setText("O="+OSlider.getValue());
		}
	}

	public void finishedDrawing() {
		// TODO Auto-generated method stub
		runThread=false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		thread=setThread();
		start.setEnabled(true);
	}
	

	
}