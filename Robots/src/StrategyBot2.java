import java.util.ArrayList;
import java.util.Random;

import robocode.AdvancedRobot;
import robocode.BulletHitEvent;
import robocode.BulletMissedEvent;
import robocode.Event;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.Rules;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;


public class StrategyBot2 extends AdvancedRobot{
	boolean fixed,searching,opponentSearch=false,canMove=true;
	ArrayList<Opponent> opponents;
	Opponent target;
	int missedBullets;
	
	double safeDistance;
	
	public void run(){
		missedBullets=0;
		safeDistance=getBattleFieldWidth()/8;
		setAdjustRadarForGunTurn(true);
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForRobotTurn(true);
		fixed=false;
		searching=true;
		opponents=new ArrayList<Opponent>();
		
		while(true){
			if(searching){
				turnRadarLeft(360);
				searching=false;
				determineTarget();
			} else {
				turnRadarRight(360);
			}
		}
	}
	
	@Override
	public void onHitRobot(HitRobotEvent event) {
		// TODO Auto-generated method stub
		target=getOpponentByName(event.getName());
		setTurnRight(-event.getBearing());
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
		updateOpponent(event);
		if(event.getName().equals(target.getName())){

			updateOpponent(event);
			if(event.getDistance()<10){
				turnGunRight(getFixedAngle(getHeading()+target.getBearing()-getGunHeading()));
				fire(4);
				//return;
			}
			setTurnGunRight(getFixedAngle(getHeading()+target.getBearing()-getGunHeading()));
			setTurnRadarRight(getFixedAngle(getHeading()+target.getBearing()-getRadarHeading()));
			setTurnRight(event.getBearing()+90);
			if(getEnergy()<20){
				setBack(50);
			}else if(event.getDistance()>150)
				setAhead(150);
			if(event.getDistance()>getBattleFieldWidth()/3 && getOthers()==1)
				return;
			if(event.getDistance()<200 && getEnergy()>50){
				setFire(4);
			} else if(getEnergy()>20){
				setFire(2);
			} else {
				setFire(1);
			}
		} 
	}
	
	@Override
	public void onBulletMissed(BulletMissedEvent event) {
		// TODO Auto-generated method stub
		determineTarget();
	}
	
	private void determineTarget(){
		if(target==null)
			target=opponents.get(0);
		for(Opponent opponent:opponents){
			if(target.getEnergy()>opponent.getEnergy()+30)
				target=opponent;
		}
		Point myPoint=new Point(getX(),getY());
		for(Opponent opponent:opponents)
			if(getDistance(myPoint, opponent.getPoint())<safeDistance){
					target=opponent;
					out.println("Focus:"+target.getName());
			}
	}

	
	
	

	private void updateOpponent(ScannedRobotEvent event) {
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
	
	private double getFixedAngle(double angle) {
		// TODO Auto-generated method stub
		if(angle<-180){
			angle+=360;
		}else if(angle>180){
			angle-=360;
		}
		return angle;
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
		if(target.getName().equals(event.getName()))
			target=null;
		searching=true;
		fixed=false;
		opponentSearch=false;
	}
	
	@Override
	public void onBulletHit(BulletHitEvent event) {
		setTurnRight(target.getBearing());
		setAhead(200);
		execute();
	}
	
	@Override
	public void onHitByBullet(HitByBulletEvent event) {
		// TODO Auto-generated method stub
		if(target.getEnergy()<30)
			return;
		target=getOpponentByName(event.getBullet().getName());
		Random rand=new Random();
		setAhead(rand.nextInt(400));
		setTurnRight(rand.nextInt(180)-90);
	}
	
	@Override
	public void onHitWall(HitWallEvent event) {
		// TODO Auto-generated method stub
		out.println(event.getBearing()+" "+target.getBearing());
		turnRight(-event.getBearing());
		Random rand=new Random();
		setAhead(200+rand.nextInt(400));
		execute();
		
	}
	
	private double getDistance(Point A, Point B) {
		double distance=Math.sqrt(Math.pow(B.getX()-A.getX(),2)+Math.pow(B.getY()-A.getY(),2));
		return distance;
	}
}
