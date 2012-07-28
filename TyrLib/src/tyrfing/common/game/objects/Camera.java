package tyrfing.common.game.objects;

import tyrfing.common.math.Vector2;
import tyrfing.common.struct.Node;

public class Camera extends GameObject {

	protected Movement movement;
	protected float speed;
	
	public Camera(Node node, float speed) {
		super(node);
		movement = new Movement(node, speed);
	}

	@Override
	public void onUpdate(float time) {
		while (time > 0)
		{
			if (movement.isFinished())
			{
				this.setChanged();
				this.notifyObservers();
				time = 0;
			}
			else
			{
				movement.onUpdate(time);
				time = movement.getRemainingTime();
			}
		}
	}

	@Override
	public float getWidth() {
		return 0;
	}

	@Override
	public float getHeight() {
		return 0;
	}
	
	public Node getNode()
	{
		return node;
	}
	
	public void addPoint(Vector2 point)
	{
		movement.addPoint(point);
	}

}
