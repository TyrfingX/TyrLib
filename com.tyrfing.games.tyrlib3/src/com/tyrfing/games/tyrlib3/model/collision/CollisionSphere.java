package com.tyrfing.games.tyrlib3.model.collision;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.tyrlib3.model.graphics.scene.BoundedSceneObject;
import com.tyrfing.games.tyrlib3.model.graphics.scene.SceneNode;
import com.tyrfing.games.tyrlib3.model.math.AABB;
import com.tyrfing.games.tyrlib3.model.math.Vector3F;
import com.tyrfing.games.tyrlib3.view.graphics.SceneManager;
import com.tyrfing.games.tyrlib3.view.graphics.renderables.BoundingBox;
import com.tyrfing.games.tyrlib3.view.graphics.renderer.OpenGLRenderer;

/**
 * This class represents a the collision component of an object
 * @author Sascha
 *
 */

public class CollisionSphere extends BoundedSceneObject {
	private List<CollisionSphere> collisions;
	private float radius;
	private int tag;
	protected AABB boundingBox;
	private BoundingBox boundingBoxRenderable;
	
	private boolean testCollision;

	public CollisionSphere(float radius) {
		collisions = new ArrayList<CollisionSphere>();
		this.radius = radius;
	}
	
	public List<CollisionSphere> getCollisions() {
		return collisions;
	}
	
	public void resetCollisions() {
		collisions.clear();
	}
	
	public void addCollision(CollisionSphere object) {
		collisions.add(object);
	}

	public boolean collidesWith(CollisionSphere object) {
		Vector3F distance = object.getAbsolutePos().sub(this.getAbsolutePos());
		if (distance.squaredLength() <= (radius-object.getRadius()) * (radius-object.getRadius()) ) {
			return true;
		}
		
		return false;
	}
	
	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	@Override
	public AABB getBoundingBox() {
		if (boundingBox == null) {
			Vector3F pos = parent.getCachedAbsolutePosVector();
			if (pos != null) {
				boundingBox.min = new Vector3F(pos.x + -radius, pos.y + -radius, pos.z + -radius);
				boundingBox.max = new Vector3F(pos.x + radius,  pos.y + radius, pos.z + radius);
			}
		}
		return boundingBox;
	}

	@Override
	public void setBoundingBoxVisible(boolean visible) {
		if (boundingBoxRenderable == null && visible) {
			AABB aabb = new AABB(new Vector3F(-radius, -radius, -radius),
								 new Vector3F(radius,  radius, radius)); 
			
			boundingBoxRenderable = new BoundingBox(aabb);
			SceneManager.getInstance().getRenderer().addRenderable(boundingBoxRenderable, OpenGLRenderer.TRANSLUCENT_CHANNEL_2);
			parent.attachSceneObject(boundingBoxRenderable);
		} else if (boundingBoxRenderable != null && !visible) {
			parent.detachSceneObject(boundingBoxRenderable);
			SceneManager.getInstance().destroyRenderable(boundingBoxRenderable, OpenGLRenderer.TRANSLUCENT_CHANNEL_2);
			boundingBoxRenderable = null;
		}
	}
	
	@Override
	public void onTransformed() {
		super.onTransformed();
		Vector3F pos = parent.getCachedAbsolutePos();
		if (pos != null) {
			boundingBox = new AABB(	new Vector3F(pos.x + -radius, pos.y + -radius, pos.z + -radius),
									new Vector3F(pos.x + radius,  pos.y + radius, pos.z + radius));
		}
	}
	
	@Override
	public void attachTo(SceneNode node)  {
		if (boundingBoxRenderable != null) {
			boundingBoxRenderable.attachTo(node);
		}
		super.attachTo(node);
	}
	

	@Override
	public SceneNode detach() {
		if (boundingBoxRenderable != null) {
			boundingBoxRenderable.detach();
		}
		return super.detach();	
	}
	
	public boolean isTestingCollision() {
		return testCollision;
	}

	public void setTestCollision(boolean testCollision) {
		this.testCollision = testCollision;
	}
	
	
}
