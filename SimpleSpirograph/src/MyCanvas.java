import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;


public class MyCanvas extends Canvas{

	Main parent;
	boolean allow,painting;
	
	Image offscreen;
	Graphics gr;
	MyGraph graph;
	
	public MyCanvas(Main parent,int width,int height){
		this.parent=parent;
		this.setSize(width, height);
		painting=false;
	}
	
	public void start(int R,int r,int O){
		graph=new MyGraph(R,r,O);
		
		allow=false;
		painting=true;
		offscreen=parent.createImage(this.getWidth(),this.getHeight());
		gr=offscreen.getGraphics();
		gr.drawOval((int)(Main.CANVAS_WIDTH/2-R),(int) (Main.CANVAS_HEIGHT/2-R),(int) (2*R),(int) (2*R));
		
	}
	
	@Override
	public void update(Graphics g) {
		// TODO Auto-generated method stub
		System.out.println("here");
		if(!allow)
			allow=true;
		paint(gr);
		g.drawImage(offscreen, 0, 0, this);
	}
	
	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		if(g==gr){
			if(painting){
				if(!graph.isFinished()){
					graph.paint(g);
				} else {
					painting=false;
					parent.finishedDrawing();
				}
			}
		}
	}
	
	public boolean isPainting(){
		return painting;
	}
}
