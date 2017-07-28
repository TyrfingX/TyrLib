package com.tyrlib2.graphics.renderables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tyrlib2.game.IUpdateable;
import com.tyrlib2.graphics.animation.Animation;
import com.tyrlib2.graphics.animation.Skeleton;
import com.tyrlib2.graphics.renderer.BoundedRenderable;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.math.AABB;

/**
 * This class represents an Entity object. Entities are higher level objects which employ
 * skeletal animation and consist of several subentities.
 * @author Sascha
 *
 */

public class Entity extends BoundedRenderable implements IUpdateable {

	private Map<String, SubEntity> subEntities;
	private List<SubEntity> subEntityList;
	protected Skeleton skeleton;
	protected int countSubEntities;
	
	private boolean visible;
	private boolean castShadow;
	
	private int userData = -1;
	private int insertionID;
	
	public Entity() {
		subEntities = new HashMap<String, SubEntity>();
		subEntityList = new ArrayList<SubEntity>();
		visible = true;
	}
	
	/**
	 * Add a new subentity
	 * @param subEntity	The subentity to be added
	 */
	
	public void addSubEntity(SubEntity subEntity) {
		countSubEntities++;
		subEntities.put(subEntity.name, subEntity);
		subEntityList.add(subEntity);
		calcBoundingBox();
	}
	
	/**
	 * Get a Subentity
	 * @param name	The name of the subentity
	 * @return		The subentity with the name
	 */
	
	public SubEntity getSubEntity(String name) {
		return subEntities.get(name);
	}
	
	/**
	 * Get a subentity by index
	 * @param index
	 * @return
	 */
	
	public SubEntity getSubEntity(int index) {
		return subEntityList.get(index);
	}
	
	@Override
	public void renderShadow(float[] vpMatrix) {
		if (visible && castShadow) {
			float[] boneData = skeleton.getBoneData();
			for (int i = 0; i < countSubEntities; ++i) {
				subEntityList.get(i).renderShadow(vpMatrix, boneData, skeleton.getCountBones());
			}
		}
	}
	
	public void setCastShadow(boolean castShadow) {
		this.castShadow = castShadow;
	}
	
	@Override
	public void render(float[] vpMatrix) {
		if (visible) {
			float[] boneData = skeleton.getBoneData();
			
			for (int i = 0; i < countSubEntities; ++i) {
				subEntityList.get(i).render(vpMatrix, boneData, skeleton.getCountBones());
			}
		}
	}
	
	@Override
	public void attachTo(SceneNode node)  {
		for (SubEntity subEntity : subEntityList) {
			subEntity.attachTo(node);
		}
		super.attachTo(node);
	}
	

	@Override
	public SceneNode detach() {
		for (SubEntity subEntity : subEntities.values()) {
			subEntity.detach();
		}
		return super.detach();	
	}
	
	/** 
	 * Get the skeleton of this entity if it has one
	 * @return The skeleton of this entity
	 */
	public Skeleton getSkeleton() {
		return skeleton;
	}
	
	public void setSkeleton(Skeleton skeleton) {
		this.skeleton = skeleton;
	}
	
	public void playAnimation(String animName) {
		Animation anim = skeleton.getAnimation(animName);
		anim.play();
	}
	
	public void stopAnimation(String animName) {
		Animation anim = skeleton.getAnimation(animName);
		anim.stop();
	}

	@Override
	public void onUpdate(float time) {
		if (skeleton != null && visible) {
			skeleton.onUpdate(time);
		}
	}

	@Override
	public boolean isFinished() {
		return false;
	}
	
	@Override
	protected AABB createUntransformedBoundingBox() {
		float[] points = new float[countSubEntities * 6];
		
		int i = 0;
		for (SubEntity sub : subEntityList) {
			AABB subBoundingBox = sub.getUntransformedBoundingBox();
			
			points[i + 0] = subBoundingBox.min.x;
			points[i + 1] = subBoundingBox.min.y;
			points[i + 2] = subBoundingBox.min.z;
			
			points[i + 3] = subBoundingBox.max.x;
			points[i + 4] = subBoundingBox.max.y;
			points[i + 5] = subBoundingBox.max.z;
			
			i += 6;
		}
		
		return AABB.createFromPoints(points, 3); 
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public boolean hasAnimation(String animName) {
		return skeleton.hasAnimation(animName);
	}
	
	public void setUserData(int data) {
		this.userData = data;
	}
	
	public int getUserData() {
		return userData;
	}
	
	@Override
	public void setInsertionID(int id) {
		this.insertionID = id;
	}

	@Override
	public int getInsertionID() {
		return insertionID;
	}

	@Override
	public void destroy() {
		for (SubEntity e : subEntityList) {
			e.destroy();
		}
	}

}
