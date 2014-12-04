import java.util.ArrayList;

import robocode.AdvancedRobot;
import robocode.Robot;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;


public class StrategyBot extends Robot{

	boolean fixed,searching;
	ArrayList<Opponent> opponents;
	Opponent target;
	
	public void run(){
		setAdjustRadarForGunTurn(true);
		setAdjustGunForRobotTurn(true);
		fixed=false;
		searching=true;
		opponents=new ArrayList<Opponent>();
		
		while(true){
			if(searching){
				out.println(getX()+" "+getY());
				turnRadarLeft(360);
				searching=false;
				out.println(opponents.size());
				target=opponents.get(0);
			} else {
				turnGunRight(getHeading()-getGunHeading()+target.getBearing());
				fire(4);
			}
		}
		
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
		if(searching){
			double h=this.getHeading()+event.getBearing();
			if(h<0){
				h+=360.0;
			}
			h=Math.toRadians(h);
			double x=getX()+Math.sin(h)*event.getDistance();
			double y=getY()+Math.cos(h)*event.getDistance();
			Opponent opponent=new Opponent(x,y,event.getName(),event.getEnergy(),event.getBearing());
			if(!opponents.contains(opponent)){
				opponents.add(opponent);
			} else {
				opponent=getOpponentByName(event.getName());
				opponent.setX(x);
				opponent.setY(y);
				opponent.setEnergy(event.getEnergy());
				opponent.setBearing(event.getBearing());
			}
			return;
		}
	}
	
	private Opponent getOpponentByName(String name) {
		for(int i=0;i<opponents.size();i++){
			if(opponents.get(i).getName().equals(name)){
				return opponents.get(i);
			}
		}
		return null;
	}

	@Override
	public void onRobotDeath(RobotDeathEvent event) {
		// TODO Auto-generated method stub
		opponents.remove(getOpponentByName(event.getName()));
		searching=true;
		fixed=false;

		out.println(opponents.size());
	}
}
