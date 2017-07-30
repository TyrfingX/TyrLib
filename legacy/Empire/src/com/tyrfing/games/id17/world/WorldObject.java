package com.tyrfing.games.id17.world;

import com.tyrlib2.graphics.renderables.Entity;
import com.tyrlib2.graphics.scene.BoundedSceneObject;
import com.tyrlib2.math.AABB;
import com.tyrlib2.math.Vector3;

public class WorldObject extends BoundedSceneObject {

	private Entity entity;
	private static final AABB boundingBox = new AABB(new Vector3(0,0,0), new Vector3(0,0,0));
	private static final Vector3 extentsVector = new Vector3(0,0,0);
	
	public WorldObject(Entity entity) {
		this.entity = entity;
	}
	
	@Override
	public AABB getBoundingBox() {
		/*
		AABB aabb = 
		float extents = aabb.getExtends();
		extentsVector.set(extents*0.15f,extents*0.15f,extents*0.15f);
		Vector3.sub(aabb.min, extentsVector, boundingBox.min);
		Vector3.add(aabb.max, extentsVector, boundingBox.max);
		boundingBox.min.z = -1;
		*/
		return entity.getBoundingBox();
	}
	
	public Entity getEntity() {
		return entity;
	}

}
