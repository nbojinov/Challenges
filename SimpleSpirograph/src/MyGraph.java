import java.awt.Graphics;
import java.awt.Rectangle;


public class MyGraph {
	
	int R,r,O,t;
	double x,y,prevX,prevY,originX,originY;
	boolean finished;
	
	public MyGraph(int R,int r,int O){
		this.R=R;
		this.r=r;
		this.O=O;
		
		x=(R+r)*Math.cos(0)+O*Math.cos(0);
		y=(R+r)*Math.sin(0)+O*Math.sin(0);
		
		originX=x;
		originY=y;
		t=0;
		
		finished=false;
	}
	
	public void paint(Graphics g){
		if(!finished){
			t++;
			prevX=x;
			prevY=y;
			x=(R+r)*Math.cos(t*Math.PI/180)+O*Math.cos(((R+r)*t/r)*Math.PI/180);
			y=(R+r)*Math.sin(t*Math.PI/180)+O*Math.sin(((R+r)*t/r)*Math.PI/180);
			g.drawLine((int)(Main.CANVAS_WIDTH/2+prevX),(int)(Main.CANVAS_HEIGHT/2+prevY),(int)(Main.CANVAS_WIDTH/2+x),(int)(Main.CANVAS_HEIGHT/2+y));
			//System.out.println("line "+(t-1)+"->"+t+" x1="+(int)(Main.WIDTH/2+prevX)+" y1="+(int)(Main.HEIGHT/2+prevY)+" \nx2="+(int)(Main.WIDTH/2+x)+" y2="+(int)(Main.HEIGHT/2+y));
			
		}
		if(Math.abs(x-originX)<0.00001 && Math.abs(y-originY)<0.00001)
			finished=true;
	}
	
	public boolean isFinished(){
		return finished;
	}
}
