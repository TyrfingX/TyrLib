package com.tyrfing.games.id17;
import com.tyrlib2.graphics.renderer.Camera;
import com.tyrlib2.graphics.renderer.IFrameListener;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.input.IMoveListener;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.math.Vector3;


public class ScrollListener implements IMoveListener, IFrameListener {
	
	public static final Vector2[] SCROLL_DIMS = {
		new Vector2(0.0f, 0.0f), // UP
		new Vector2(1.0f, 0.005f), 
		
		new Vector2(0.0f, 0.0f), // LEFT
		new Vector2(0.01f, 1.0f),
		
		new Vector2(0.0f, 0.995f), // DOWN
		new Vector2(1.0f, 0.995f),
		
		new Vector2(0.995f, 0.0f), // RIGHT
		new Vector2(0.005f, 1.0f),	
	};
	
	public static final float SCROLL_SPEED = 1.0f;
	
	private boolean[] scrolling = new boolean[4];
	
	private float accTime;
	
	private boolean inScreen = true;

	@Override
	public boolean onMove(Vector2 point) {

		for (int i = 0; i < 4; ++i) {
			scrolling[i] = pointInRectangle(SCROLL_DIMS[2*i], SCROLL_DIMS[2*i+1], point);
		}
		
		return false;
	}

	private boolean pointInRectangle(Vector2 pos, Vector2 size, Vector2 point) {
		if (point.x >= pos.x && point.x <= pos.x + size.x) {
			if (point.y >= pos.y && point.y <= pos.y + size.y) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public void onSurfaceCreated() {
	}

	@Override
	public void onSurfaceChanged() {
	}

	@Override
	public void onFrameRendered(float time) {
		
		if (!inScreen) return;
		
		Camera camera = SceneManager.getInstance().getActiveCamera();
		Vector3 lookDirectionWorld = camera.getWorldLookDirection();
		Vector3 upWorld = camera.getWorldUpVector();
		Vector3 rightWorld = lookDirectionWorld.cross(upWorld);
		
		Vector2 right = new Vector2(rightWorld.x, rightWorld.y);
		right.normalize();
		Vector2 up = new Vector2(lookDirectionWorld.x, lookDirectionWorld.y);
		up.normalize();
		
		accTime += time;
		
		while (accTime >= 0.001f) {
		
			SceneNode camNode = camera.getParent();
			
			float distance = SCROLL_SPEED * 0.001f * camNode.getRelativePos().z;
			
			if (scrolling[0]) {
				camNode.translate(up.x * distance, up.y * distance, 0);
			}
			
			if (scrolling[1]) {
				camNode.translate( right.x * -distance,  right.y * -distance, 0);
			}
			
			if (scrolling[2]) {
				camNode.translate( up.x * -distance, up.y * -distance, 0);
			}
			
			if (scrolling[3]) {
				camNode.translate(right.x * distance,  right.y * distance, 0);
			}
			
			accTime -= 0.001f;
		
		}
	}

	@Override
	public boolean onEnterRenderWindow() {
		return false;
	}

	@Override
	public boolean onLeaveRenderWindow() {
		return false;
	}

	@Override
	public boolean onRenderWindowLoseFocus() {
		inScreen = false;
		return false;
	}

	@Override
	public boolean onRenderWindowGainFocus() {
		inScreen = true;
		return false;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
