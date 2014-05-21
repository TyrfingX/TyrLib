package com.tyrlib2.graphics.renderables;

import java.nio.FloatBuffer;

import com.tyrlib2.graphics.animation.Skeleton;
import com.tyrlib2.graphics.renderer.Material;
import com.tyrlib2.graphics.renderer.Mesh;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.renderer.Program;
import com.tyrlib2.graphics.renderer.Renderable;
import com.tyrlib2.graphics.renderer.TyrGL;

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
	
	public void render(float[] vpMatrix, float[] skeletonBuffer, int bones) {
		Skeleton.passData(skeletonBuffer, bones, material, mesh);
		super.render(vpMatrix);
	}

}
