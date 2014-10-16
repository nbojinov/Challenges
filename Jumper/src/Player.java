import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.BitSet;

import javax.imageio.ImageIO;


public class Player implements IDrawable{
	
	private final int LENGTH_SIDE=40;
	private final int JUMP_VELOCITY=-15;
	private final double INCREASE_X_VELOCITY=2;
	private final double DECREASE_X_VELOCITY=1.5;
	private final double MAX_X_VELOCITY=9;
	private final double GRAVITY=1.2;
	
	private double x,y,prevX,prevY;
	
	private int row,frame;
	
	private final int ROW_FLY=0;
	private final int ROW_LEFT=1;
	private final int ROW_RIGHT=2;

	private boolean onPlatform,acceleratingX,moving;
	double velocityY=0,velocityX=0;
	
	BufferedImage run;
	
	
	Main2 parent;
	private IObstacle currentPlatform;
	
	public Player(Main2 parent,IObstacle originalPlatform){
		this.parent=parent;
		currentPlatform=originalPlatform;
		
		onPlatform=true;
		acceleratingX=false;
		moving=false;
		x=0;
		y=parent.getHEIGHT()-LENGTH_SIDE-1;
		frame=0;
		try {
			run=ImageIO.read(new File("images/angelsanctuary_alexiel.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void iteration(BitSet bitSet){
		moving=false;
		determineVelocity(bitSet);
		prevX=x;
		prevY=y;
		calculateCoords();
		if((y+parent.getOffset())<parent.getHEIGHT()/2){
			parent.setOffset((int)(parent.getOffset()+(parent.getHEIGHT()/2-(y+parent.getOffset()))));
		}
		if (prevX!=x || prevY!=y)
			moving=true;
	}

	private void determineVelocity(BitSet bitSet) {
		if(!bitSet.isEmpty()){
			if(bitSet.get(KeyEvent.VK_UP) && onPlatform){
				velocityY=JUMP_VELOCITY;
				onPlatform=false;
			} 
			if(bitSet.get(KeyEvent.VK_RIGHT) && velocityX<MAX_X_VELOCITY){
				velocityX+=INCREASE_X_VELOCITY;
				acceleratingX=true;
			} 
			if(bitSet.get(KeyEvent.VK_LEFT) && velocityX>-MAX_X_VELOCITY){
				velocityX-=INCREASE_X_VELOCITY;
				acceleratingX=true;
			}
		}
	}
	
	private void calculateCoords() {
		if(parent.isStarted() && (y+velocityY+ parent.getOffset()>parent.getHEIGHT())){
			System.exit(0);
		}
		if(onPlatform){
			if(!acceleratingX){
				if(velocityX>0){
					velocityX-=DECREASE_X_VELOCITY;
				} else if (velocityX<0){
					velocityX+=DECREASE_X_VELOCITY;
				}
				if(Math.abs(velocityX)<DECREASE_X_VELOCITY)
					velocityX=0;
			}
			acceleratingX=false;
			if(!(currentPlatform.getX()<=x+velocityX+3*LENGTH_SIDE/4
					&& ((currentPlatform.getX()+currentPlatform.getWidth())>=(x+velocityX+LENGTH_SIDE/4)))){
				onPlatform=false;
				y-=1;
			}
				
		} else {
			IObstacle obstacle=null;
			for(IObstacle obst:parent.getObstacles()){
				if(y+LENGTH_SIDE<=obst.getY() && y+velocityY+LENGTH_SIDE>=obst.getY() && (obst.getX()<=x+velocityX+3*LENGTH_SIDE/4 
						&& obst.getX()+obst.getWidth()>=x+velocityX+LENGTH_SIDE/4)){
					obstacle=obst;
					break;
				}
			}
			if(obstacle!=null){
				currentPlatform=obstacle;
				currentPlatform.stepped();
				onPlatform=true;
				y=obstacle.getY()-LENGTH_SIDE-1;
				velocityY=0;
			} else {
				y+=velocityY;
				velocityY+=GRAVITY;
			}
		}
		if(x+velocityX>=0 && x+velocityX+LENGTH_SIDE<parent.getWIDTH()){
			x+=velocityX;
		} else {
			if(x+velocityX<=0){
				x=0;
			} else {
				x=parent.getWIDTH() - LENGTH_SIDE-1;
			}
			velocityX=0;
		}
	}

	@Override
	public void paint(Graphics g,int offset) {
		// TODO Auto-generated method stub
		//g.drawRect((int)x, (int)y+offset, LENGTH_SIDE, LENGTH_SIDE);
		//g.drawImage(run, x, y, observer);
		frame++;
		//System.out.println(moving);
		if(!moving){
			frame=0;
			row=ROW_FLY;
		} else {
			if(!onPlatform)
				row=ROW_FLY;
			else if (velocityX>0)
				row=ROW_RIGHT;
			else
				row=ROW_LEFT;
		}
		g.drawImage(run, (int)x, (int)y+offset, (int) x +LENGTH_SIDE,(int)y + offset+LENGTH_SIDE, (frame%4)*48, row*48, (frame%4+1)*48, (row+1)*48-1, null);
	} 
	
	@Override
	public int getY() {
		// TODO Auto-generated method stub
		return (int)y;
	}

	@Override
	public void updateDrawable(BufferedImage tile) {
		// TODO Auto-generated method stub
		
	}
	
	public IObstacle returnCurrentPlatform(){
		return currentPlatform;
	}
}
