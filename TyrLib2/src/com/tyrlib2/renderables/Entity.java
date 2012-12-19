package com.tyrlib2.renderables;

import java.util.HashMap;
import java.util.Map;

import com.tyrlib2.renderer.IRenderable;
import com.tyrlib2.scene.SceneNode;
import com.tyrlib2.scene.SceneObject;

/**
 * This class represents an Entity object. Entities are higher level objects which employ animation and consist
 * of several subentities.
 * @author Sascha
 *
 */

public class Entity extends SceneObject implements IRenderable {

	private Map<String, SubEntity> subEntities;
	
	public Entity() {
		subEntities = new HashMap<String, SubEntity>();
	}
	
	/**
	 * Add a new subentity
	 * @param subEntity	The subentity to be added
	 */
	
	public void addSubEntity(SubEntity subEntity) {
		subEntities.put(subEntity.name, subEntity);
	}
	
	/**
	 * Get a Subentity
	 * @param name	The name of the subentity
	 * @return		The subentity with the name
	 */
	
	public SubEntity getSubEntity(String name) {
		return subEntities.get(name);
	}
	
	@Override
	public void render(float[] vpMatrix) {
		for (SubEntity subEntity : subEntities.values()) {
			subEntity.render(vpMatrix);
		}
	}
	
	@Override
	public void attachTo(SceneNode node)  {
		for (SubEntity subEntity : subEntities.values()) {
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
	

}
