package com.tyrfing.games.id17.gui;


import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.war.Army;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.graphics.renderer.Camera;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.input.Controller;
import com.tyrlib2.input.IKeyboardEvent;
import com.tyrlib2.input.IKeyboardListener;
import com.tyrlib2.input.IMotionEvent;
import com.tyrlib2.input.InputManager;
import com.tyrlib2.math.Quaternion;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.movement.DirectMovement;
import com.tyrlib2.movement.ITargetProvider;
import com.tyrlib2.movement.Movement;
import com.tyrlib2.movement.Speed;
import com.tyrlib2.movement.TargetPoint;

public class CameraController extends Controller implements IKeyboardListener {
	private Vector2[] lastPoint = new Vector2[2];
	private float lastDistance;
	private SceneNode camNode;
	
	private Vector2 right;
	private Vector2 up;
	
	private float currentZoom;

	private long priority;
	
	private int[] fingerID = new int[2];
	
	private Movement movement;
	public Speed speed = new Speed(30);
	
	private int keyW;
	private int keyA;
	private int keyS;
	private int keyD;
	
	public final static float TIMESTEP = 0.001f;
	private float accTime = 0;
	
	public static final float MOVE_FACTOR = 0.025f;
	public static final Vector3 INIT_LOOK_VECTOR = new Vector3(1,-1,-1).unitVector();
	public static final Quaternion ORT_ROT = Quaternion.rotateTo(INIT_LOOK_VECTOR,new Vector3(0,0,-1));
	public static float ROTATE_SPEED = 30f;
	
	private float lastSpeed = 0;
	private Vector3 lastMove;
	
	private float inertia = 1;
	
	public static float STRATEGIC_VIEW_HEIGHT = 350;
	
	private boolean inStrategicView = false;
	
	public CameraController(SceneNode camNode) {
		this.camNode = camNode;
		fingerID[0] = -1;
		fingerID[1] = -1;
		
		movement = new DirectMovement(camNode, speed);

	}
	
	public void destroy() {
		InputManager.getInstance().removeTouchListener(this);
		InputManager.getInstance().removeKeyboardListener(this);
	}
	
	public void updateAxes() {
		if (right == null && up == null) {
			Camera camera = SceneManager.getInstance().getActiveCamera();
			Vector3 lookDirectionWorld = camera.getWorldLookDirection();
			Vector3 upWorld = camera.getWorldUpVector();
			Vector3 rightWorld = lookDirectionWorld.cross(upWorld);
			
			right = new Vector2(rightWorld.x, rightWorld.y);
			right.normalize();
			up = new Vector2(lookDirectionWorld.x, lookDirectionWorld.y);
			up.normalize();
		}
	}
	
	@Override
	public boolean onTouchMove(Vector2 point, IMotionEvent event, int fingerId) {
		
		updateAxes();
		
		if (fingerID[1] == -1) {
			if (lastPoint[0] != null) {
				Vector2 movement = lastPoint[0].vectorTo(point).multiply(50);
				movement.x *= -1;
				if (movement.multiply(1/50.f).length() > 0.1f) {
					return false;
				}
				lastPoint[0] = new Vector2(point);
				lastMove = new Vector3( right.x * movement.x + up.x * movement.y,  right.y * movement.x + up.y * movement.y, 0);
				camNode.translate(lastMove);
				float speed = lastMove.normalize();
				if (speed > lastSpeed) {
					lastSpeed = speed;
				}
			}
		} else {
			// ZOOM
			if (fingerID[0] == fingerId) {
				lastPoint[0] = new Vector2(point);
			} else {
				lastPoint[1] = new Vector2(point);
			}
			float zoom = lastPoint[0].vectorTo(lastPoint[1]).length() - lastDistance;
			lastDistance = lastPoint[0].vectorTo(lastPoint[1]).length();
			zoom(zoom);
		}
		
		World.getInstance().getMainGUI().pickerGUI.onTouchMove(point, event, fingerId);
		
		return true;
	}
	
	public void zoom(float zoom) {
		updateAxes();
		
		Camera camera = SceneManager.getInstance().getActiveCamera();
		Vector3 lookDirectionWorld = camera.getWorldLookDirection();
		
		currentZoom = (float) Math.pow((camNode.getRelativePos().z/50), 2);
		float zoomSpeed = Math.min(2*currentZoom+13, 300);
		camNode.translate(new Vector3(lookDirectionWorld.x * zoom*zoomSpeed,lookDirectionWorld.y * zoom*zoomSpeed,zoom*zoomSpeed * lookDirectionWorld.z));
		
		Quaternion rot = Quaternion.slerp(Quaternion.IDENTITY, ORT_ROT, Math.max(Math.min(0.99f, currentZoom/ROTATE_SPEED), -0.5f));
		Vector3 newLookDirection = rot.multiply(INIT_LOOK_VECTOR);
		newLookDirection.normalize();
		
		Vector3 offset = camera.getLookDirection().vectorTo(newLookDirection);
		offset.z = 0;
		Vector3.multiply(-camNode.getRelativePos().z, offset);
		
		camera.setLookDirection(newLookDirection);
		camNode.translate(offset);

		
		SceneManager.getInstance().getRenderer().queueEvent(new Runnable() {
			@Override
			public void run() {
				
				for (int i = 0; i < World.getInstance().armies.size(); ++i) {
					Army army = World.getInstance().armies.get(i);
					army.updateHeader();
				}
				
				if (inStrategicView) {
					if (camNode.getRelativePos().z < STRATEGIC_VIEW_HEIGHT) {
						World.getInstance().setDetailVisibility(true);
						World.getInstance().setTerrainStrategic(false);
						inStrategicView = false;
					}
				} else {
					if (camNode.getRelativePos().z >= STRATEGIC_VIEW_HEIGHT) {
						World.getInstance().setDetailVisibility(false);
						World.getInstance().setTerrainStrategic(true);
						inStrategicView = true;
					}
				}
			}
		});
	}

	@Override
	public boolean onTouchDown(Vector2 point, IMotionEvent event, int fingerId) {
		
		lastSpeed = 0;
		
		if (EmpireFrameListener.BUILD_TARGET == EmpireFrameListener.ANDROID_TARGET) {
		
			priority = InputManager.FOCUS_PRIORITY;
			InputManager.getInstance().sort();
			
			if (fingerID[0] == -1 || fingerID[0]== fingerId) {
				lastPoint[0] = new Vector2(point);
				fingerID[0] = fingerId;
			} else if (fingerID[1] == -1){
				fingerID[1] = fingerId;
				lastPoint[1] = new Vector2(point);
				lastDistance = lastPoint[0].vectorTo(lastPoint[1]).length();
			}
			
			World.getInstance().getMainGUI().pickerGUI.onTouchDown(point, event, fingerId);
			
		} else {
			World.getInstance().getMainGUI().pickerGUI.onTouchDown(point, event, fingerId);
		}
		
		return false;
	}
	
	@Override
	public boolean onTouchUp(Vector2 point, IMotionEvent event, int fingerId) {
		
		if (fingerID[0] == -1 || fingerID[0]== fingerId) {
			lastPoint[0] = lastPoint[1];
			fingerID[0] = fingerID[1];
			fingerID[1] = -1;
		} else {
			fingerID[1] = -1;
		}
		
		priority = 0;
		InputManager.getInstance().sort();
		
		World.getInstance().getMainGUI().pickerGUI.onTouchUp(point, event, fingerId);
		
		return false;
	}


	@Override
	public void onUpdate(float time) {	
		movement.onUpdate(time);
		
		if (keyW != 0 || keyA != 0 || keyS != 0 || keyD != 0) {
			if (right == null && up == null) {
				Camera camera = SceneManager.getInstance().getActiveCamera();
				Vector3 lookDirectionWorld = camera.getWorldLookDirection();
				Vector3 upWorld = camera.getWorldUpVector();
				Vector3 rightWorld = lookDirectionWorld.cross(upWorld);
				
				right = new Vector2(rightWorld.x, rightWorld.y);
				right.normalize();
				up = new Vector2(lookDirectionWorld.x, lookDirectionWorld.y);
				up.normalize();
			}
			
			accTime += time;
			while (accTime >= TIMESTEP) {
				float speedF = speed.speed;
				camNode.translate(right.x * (keyA+keyD) * speedF * TIMESTEP
												+ 	up.x * (keyW+keyS) * speedF * TIMESTEP,  
													right.y * (keyA+keyD) * speedF * TIMESTEP 
												+ up.y * (keyW+keyS) * speedF * TIMESTEP, 0 );
				
				accTime -= TIMESTEP;
			
			}
		}
		
		if (lastSpeed > 0) {
			camNode.translate(lastMove.multiply(lastSpeed*time*10));
			lastSpeed -= lastSpeed*inertia*time + inertia*time;
		}
		
	}
	
	@Override
	public boolean isFinished() { return false; }
	
	@Override
	public long getPriority() {
		return priority;
	}
	
	public void focus(Vector3 pos) {
		Camera camera = SceneManager.getInstance().getActiveCamera();
		Vector3 lookDirection = camera.getWorldLookDirection();
		Vector3 camPos = camNode.getCachedAbsolutePosVector();
		float diff = camPos.z - pos.z;
		float factor = diff / lookDirection.z;
		Vector3 target = pos.add(lookDirection.multiply(factor));
		movement.addTarget(new TargetPoint(target));
	}
	
	public void focus(final SceneNode node) {
		movement.clear();
		movement.addTarget(new ITargetProvider() {
			@Override
			public Vector3 getTargetPos() {
				Camera camera = SceneManager.getInstance().getActiveCamera();
				Vector3 lookDirection = camera.getWorldLookDirection();
				Vector3 camPos = camNode.getCachedAbsolutePosVector();
				float diff = camPos.z - node.getCachedAbsolutePos().z;
				float factor = diff / lookDirection.z;
				Vector3 target = node.getCachedAbsolutePos().add(lookDirection.multiply(factor));
				return target;
			}
		});
	}

	@Override
	public boolean onPress(IKeyboardEvent e) {
		if (e.getKeyChar() == 's' || e.getKeyChar() == 'S') {
			keyS = -1;
		} else if (e.getKeyChar() == 'w' || e.getKeyChar() == 'W') {
			keyW = 1;
		} else if (e.getKeyChar() == 'a' || e.getKeyChar() == 'A') {
			keyA = -1;
		} else if (e.getKeyChar() == 'd' || e.getKeyChar() == 'D') {
			keyD = 1;
		}
		
		return true;
	}

	@Override
	public boolean onRelease(IKeyboardEvent e) {
		if (e.getKeyChar() == 's' || e.getKeyChar() == 'S') {
			keyS = 0;
		} else if (e.getKeyChar() == 'w' || e.getKeyChar() == 'W') {
			keyW = 0;
		} else if (e.getKeyChar() == 'a' || e.getKeyChar() == 'A') {
			keyA = 0;
		} else if (e.getKeyChar() == 'd' || e.getKeyChar() == 'D') {
			keyD = 0;
		}
		return false;
	}

}
