package com.tyrlib2.renderables;

import com.tyrlib2.renderer.Material;
import com.tyrlib2.renderer.Mesh;
import com.tyrlib2.renderer.Renderable;

public class SubEntity extends Renderable {

	protected String name;
	
	public SubEntity(String name, Mesh mesh, Material material) {
		this.name = name;
		this.mesh = mesh;
		this.material = material;
	}

}
