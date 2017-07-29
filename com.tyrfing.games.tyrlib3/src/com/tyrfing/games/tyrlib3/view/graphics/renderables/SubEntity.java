package com.tyrfing.games.tyrlib3.view.graphics.renderables;

import com.tyrfing.games.tyrlib3.view.graphics.Mesh;
import com.tyrfing.games.tyrlib3.view.graphics.Program;
import com.tyrfing.games.tyrlib3.view.graphics.animation.Skeleton;
import com.tyrfing.games.tyrlib3.view.graphics.materials.Material;

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
		Program program = material.getProgram();
		program.use();
		material.updateHandles();
		Skeleton.passData(skeletonBuffer, bones, material, mesh);
		super.render(vpMatrix);
	}

	public void renderShadow(float[] vpMatrix, float[] skeletonBuffer, int bones) {
		Skeleton.passShadowData(skeletonBuffer, bones, material, mesh);
		super.renderShadow(vpMatrix);
	}

}
