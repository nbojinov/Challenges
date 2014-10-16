import java.awt.Graphics;
import java.awt.image.BufferedImage;


public interface IDrawable {
	public void paint(Graphics g,int offset);
	public int getY();
	public void updateDrawable(BufferedImage tile);
}
