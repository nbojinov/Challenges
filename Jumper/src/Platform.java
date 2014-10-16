import java.awt.Graphics;
import java.awt.image.BufferedImage;


public class Platform implements IDrawable,IObstacle{
	
	private final int height=10;
	
	private int x,y,width;
	private BufferedImage tile;
	protected Main2 parent;
	private int id;
	
	public Platform(Main2 parent, BufferedImage tile,int x,int y,int width,int id){
		this.parent=parent;
		this.x=x;
		this.y=y;
		this.width=width;
		this.tile=tile;
		this.id=id;
	}

	@Override
	public void paint(Graphics g,int offset) {
		// TODO Auto-generated method stub
		for(int i=0;i<width;i++){
			g.drawImage(tile,x+i*10,y+offset,height,height, null);
		}
	}

	@Override
	public int getX() {
		// TODO Auto-generated method stub
		return x;
	}

	@Override
	public int getY() {
		// TODO Auto-generated method stub
		return y;
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return width*10;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return height;
	}

	@Override
	public void stepped() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateDrawable(BufferedImage tile) {
		// TODO Auto-generated method stub
		this.tile=tile;
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return id;
	}

}
