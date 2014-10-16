import java.awt.image.BufferedImage;


public class SpecialPlatform extends Platform implements IObstacle,IDrawable{

	private boolean steppedOn;
	private boolean winning;
	
	public SpecialPlatform(Main2 parent, BufferedImage tile, int x, int y, int width,int id,boolean winning) {
		super(parent, tile, x, y, width,id);
		// TODO Auto-generated constructor stub
		if(winning)
			parent.forbidCreationOfPlatforms();
		steppedOn=false;
		this.winning=winning;
	}
	
	@Override
	public void stepped() {
		// TODO Auto-generated method stub
		if(!steppedOn){
			if(winning){
				parent.hasWon();
			} else {
				parent.changeTileAndIncreaseSpeed();
			}
			steppedOn=true;
		}
	}

}
