package com.tyrlib2.renderables;

import com.tyrlib2.renderer.Material;
import com.tyrlib2.renderer.Mesh;
import com.tyrlib2.renderer.Renderable;

/**
 * This class represents a SubEntitiy, a part of an Entity. A SubEntity is bascially a named
 * renderable with extended functionality.
 * @author Sascha
 *
 */

public class SubEntity extends Renderable {

	protected String name;
	
	public SubEntity(String name, Mesh mesh, Material material) {
		this.name = name;
		this.mesh = mesh;
		this.material = material;
	}

}
