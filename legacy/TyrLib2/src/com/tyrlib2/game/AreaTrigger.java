package com.tyrlib2.game;

import com.tyrlib2.graphics.scene.BoundedSceneObject;
import com.tyrlib2.math.AABB;
import com.tyrlib2.math.Vector3;

public class AreaTrigger extends BoundedSceneObject implements IUpdateable {

	private AABB aabb;
	private boolean triggered;
	private GameObject trigger;
	private IAreaListener listener;
	private Vector3 size;
	private BoundedSceneObject areaObject;
	
	public interface IAreaListener {
		public void onEnterArea();
		public void onLeaveArea();
	}
	
	public AreaTrigger(GameObject trigger, IAreaListener listener, Vector3 size) {
		this.trigger = trigger;
		this.listener = listener;
		this.size = size;
		
		aabb = new AABB(new Vector3(-size.x/2, -size.y/2, -size.y/2),
						new Vector3(size.x/2, size.y/2, size.z/2));
	}
	
	public AreaTrigger(GameObject trigger, IAreaListener listener, BoundedSceneObject areaObject) {
		this.trigger = trigger;
		this.listener = listener;
		this.areaObject = areaObject;
		
		aabb = areaObject.getBoundingBox();
	}
	
	@Override
	public AABB getBoundingBox() {
		return aabb;
	}
	
	@Override
	public void calcBoundingBox() {
		if (areaObject == null) {
			if (parent != null) {
				Vector3 pos = parent.getCachedAbsolutePosVector();
				aabb.min.x = -size.x/2 + pos.x;
				aabb.min.y = -size.y/2 + pos.y;
				aabb.min.z = -size.y/2 + pos.z;
				
				aabb.max.x = size.x/2 + pos.x;
				aabb.max.y = size.y/2 + pos.y;
				aabb.max.z = size.y/2 +  pos.z;
			}
		} 
	}

	@Override
	public void onUpdate(float time) {
		if (aabb == null) {
			aabb = areaObject.getBoundingBox();
			if (aabb == null) {
				return;
			}
		}
		
		
		if (!triggered) {
			if (trigger.getBoundingBox().intersectsAABB(aabb)) {
				triggered = true;
				listener.onEnterArea();
				
			}
		} else if (!trigger.getBoundingBox().intersectsAABB(aabb)) {
			triggered = false;
			listener.onLeaveArea();
		}
	}

	@Override
	public boolean isFinished() {
		return false;
	}

}
