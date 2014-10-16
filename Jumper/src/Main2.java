import java.applet.Applet;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.BitSet;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;


public class Main2 extends Applet implements KeyListener{
	
	private final int FRAME_TIME=33;
	private final int TILES=5;
	
	private double WIDTH,HEIGHT;

	private long startTime,endTime;
	
	private Date timeDate;
	private Timer timer;
	private Random rand;
	
	private int offset,nextPlatform,createdPlatforms;
	private boolean gameStarted,canCreatePlatforms;
	
	private int step;
	
	private BitSet bitSet;
	private CopyOnWriteArrayList<IDrawable> drawables;
	private CopyOnWriteArrayList<IObstacle> obstacles;
	BufferedImage tile;
	BufferedImage[] tiles;
	
	private Player player;
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		super.init();
		this.setSize(300, 320);
		Dimension dim=this.getSize();
		WIDTH=dim.getWidth();
		HEIGHT=dim.getHeight();
		offset=0;
		step=0;
		
		this.addKeyListener(this);
		timeDate=new Date();
		bitSet=new BitSet(256);
		rand=new Random();
		
		drawables=new CopyOnWriteArrayList<IDrawable>();
		obstacles=new CopyOnWriteArrayList<IObstacle>();
		
		nextPlatform=(int)HEIGHT-50;
		rand=new Random();
		createdPlatforms=0;
		canCreatePlatforms=true;
		
		loadTiles();
		changeTileAndIncreaseSpeed();
		
		for(int i=0;i<10;i++){
			createPlatform();
		}
		
		gameStarted=false;
		
		Platform ground=new Platform(this,tile,0,(int)HEIGHT,(int)WIDTH/10,0);
		obstacles.add(ground);
		player=new Player(this,ground);
		
		Thread thread=new Thread(){
			
			@Override
			public void run() {
				// TODO Reduce booleans
				super.run();
				while(true){
					startTime=timeDate.getTime();
					
					player.iteration(bitSet);
					repaint();
					endTime=timeDate.getTime();
					if(FRAME_TIME-(endTime-startTime)>0){
						try {
							Thread.sleep(FRAME_TIME-(endTime-startTime));
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					
				}
			}

			
		};
		thread.start();
	}

	private void loadTiles() {
		tiles=new BufferedImage[TILES];
		try {
			tiles[0] = ImageIO.read(new File("images/tile.png"));
			tiles[1] = ImageIO.read(new File("images/liquidWater.png"));
			tiles[2] = ImageIO.read(new File("images/snowCenter.png"));
			tiles[3] = ImageIO.read(new File("images/stoneWall.png"));
			tiles[4] = ImageIO.read(new File("images/liquidLava.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void createPlatform() {
		createdPlatforms++;
		Platform obstacle=null;
		if(createdPlatforms%25==0){
			boolean winning=false;
			if(createdPlatforms%(TILES*25)==0)
				winning=true;
			obstacle=new SpecialPlatform(this,tile,0,nextPlatform,(int)WIDTH/10,createdPlatforms,winning);
		} else
			obstacle=new Platform(this,tile,rand.nextInt((int)WIDTH-100),nextPlatform,rand.nextInt(5)+5,createdPlatforms);
		drawables.add(obstacle);
		obstacles.add(obstacle);
		nextPlatform-=75;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		int key=e.getKeyCode();
		bitSet.set(key);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		int key=e.getKeyCode();
		bitSet.clear(key);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	 private Image offScreenImage;
	 private Dimension offScreenSize;
	 private Graphics offScreenGraphics;
	 public final synchronized void update (Graphics g) {
	Dimension d = this.getSize();
	if((offScreenImage == null) || (d.width != offScreenSize.width) || (d.height != offScreenSize.height)) {
	 offScreenImage = createImage(d.width, d.height);
	 offScreenSize = d;
	 offScreenGraphics = offScreenImage.getGraphics();
	}
	offScreenGraphics.clearRect(0, 0, d.width, d.height);
	paint(offScreenGraphics);
	g.drawImage(offScreenImage, 0, 0, this);
	 }
	
	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		for(IDrawable drawable:drawables){
			if(drawable.getY()+offset>HEIGHT){
				drawables.remove(drawable);
				obstacles.remove((IObstacle) drawable);
				if(canCreatePlatforms)
					createPlatform();
			} else
				drawable.paint(g,offset);
		}
		
		player.paint(g,offset);
		g.drawString("Platform:"+player.returnCurrentPlatform().getID(), 0, 10);
	}

	public CopyOnWriteArrayList<IObstacle> getObstacles() {
		// TODO Auto-generated method stub
		return obstacles;
	}
	
	public double getWIDTH() {
		return WIDTH;
	}

	public double getHEIGHT() {
		return HEIGHT;
	}
	
	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		if(!gameStarted){
			gameStarted=true;
			timer=new Timer();
			timer.scheduleAtFixedRate(new TimerTask(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					setOffset(getOffset()+step);
				}}, 0, 2*FRAME_TIME);
		}
		this.offset = offset;
	}

	public boolean isStarted(){
		return gameStarted;
	}

	public void forbidCreationOfPlatforms() {
		// TODO Auto-generated method stub
		canCreatePlatforms=false;
	}

	public void hasWon() {
		// TODO Auto-generated method stub
		System.exit(0);
	}

	public void changeTileAndIncreaseSpeed() {
		// TODO Auto-generated method stub
		tile=tiles[step];
		for(IDrawable drawable: drawables){
			drawable.updateDrawable(tile);
		}
		step++;
	}
}