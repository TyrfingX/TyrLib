package tyrfing.common.game.objects;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import tyrfing.common.math.Vector2;
import tyrfing.common.struct.Node;

public class Movement implements IUpdateable {

	private Node node;
	private Queue<Vector2> path;
	private Speed speed;
	private Vector2 lastPoint;
	
	private float remainingTime = 0;
	
	private List<IMovementListener> movementListeners;
	
	public Movement(Node node, float speed)
	{
		this(node, new Speed(speed));
	}
	
	public Movement(Node node, Speed speed)
	{
		this.node = node;
		this.speed = speed;
		path = new LinkedList<Vector2>();
		lastPoint = node.getAbsolutePos();
		movementListeners = new ArrayList<IMovementListener>();
	}
	
	public float getSpeed()
	{
		return speed.getSpeed();
	}
	
	public void setSpeed(float speed)
	{
		this.speed.setSpeed(speed);
	}
	
	@Override
	public void onUpdate(float time) {
		if (!path.isEmpty())
		{
			Vector2 dest = path.peek();
			if (dest != null)
			{
				Vector2 move = node.getAbsolutePos().vectorTo(dest);
				float distance = move.normalize();
				if (distance <= speed.getSpeed() * time)
				{
					lastPoint = dest;
					path.poll();
					Node parent = node.getParent();
					Vector2 relDest = dest;
							
					if (parent != null){
						Vector2 parentPos = parent.getAbsolutePos();
						relDest.x -= parentPos.x;
						relDest.y -= parentPos.y;
					}
					
					node.setPos(relDest);
					remainingTime = distance / speed.getSpeed();
					
					if (path.isEmpty())
					{
						for (IMovementListener movementListener : movementListeners)
						{
							if (movementListener.isListening())
							{
								movementListener.onFinishMovement();
							}
						}
					}
				}
				else
				{
					node.translate(move.multiply(speed.getSpeed()*time));
					remainingTime = 0;
				}
				
			}
			else
			{
				remainingTime = 0;
			}
		}
		else
		{
			remainingTime = 0;
		}
	}
	
	public void translatePath(Vector2 translation)
	{
		for (Vector2 point : path)
		{
			point.x = point.x + translation.x;
			point.y = point.y + translation.y;
		}
	}
	
	public void addPoint(Vector2 point)
	{
		path.add(point);
	}
	
	public void clearPath()
	{
		path.clear();
		lastPoint = node.getAbsolutePos();
		
	}
	
	public boolean isFinished()
	{
		return path.isEmpty();
	}
	
	public Vector2 getLastPoint()
	{
		return lastPoint;
	}
	
	public Vector2 getNextPoint()
	{
		if (!path.isEmpty()) return path.peek();
		return null;
	}
	
	public float getRemainingTime()
	{
		return remainingTime;
	}
	
	public void addMovementListener(IMovementListener movementListener)
	{
		movementListeners.add(movementListener);
	}
	
	public IMovementListener getMovementListener(int id)
	{
		return movementListeners.get(id);
	}
	
	public Queue<Vector2> getPath() {
		return path;
	}
	
	public void setPath(Queue<Vector2> path) {
		this.path = path;
	}
	
}
