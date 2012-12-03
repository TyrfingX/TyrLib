package tyrfing.common.game.objects;

public class Speed implements IUpdateable {
	private float speed;
	private float accel;
	
	public Speed(float speed) {
		this(speed, 0);
	}
	
	public Speed(float speed, float accel) {
		this.speed = speed;
		this.accel = accel;
	}
	
	@Override
	public void onUpdate(float time) {
		speed += accel * time;
	}
	
	@Override
	public boolean isFinished() {
		return false;
	}
	
	public float getSpeed() {
		return speed;
	}
	
	public float getAccel() {
		return accel;
	}
	
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	public void setAccel(float accel) {
		this.accel = accel;
	}
	
	
}
