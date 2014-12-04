
public class Opponent {

	private double x,y;
	private String name;
	private double energy;
	private double bearing;
	
	public Opponent(double x,double y,String name,double energy,double bearing){
		this.x=x;
		this.y=y;
		this.name=name;
		this.setEnergy(energy);
		this.setBearing(bearing);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (!(obj instanceof Opponent))
			return false;
		Opponent other=(Opponent) obj;
		return name.equals(other.getName());
	}

	public double getEnergy() {
		return energy;
	}

	public void setEnergy(double energy) {
		this.energy = energy;
	}

	public double getBearing() {
		return bearing;
	}

	public void setBearing(double bearing) {
		this.bearing = bearing;
	}
	
	public Point getPoint() {
		// TODO Auto-generated method stub
		return new Point(x,y);
	}
}
