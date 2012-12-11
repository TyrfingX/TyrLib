package com.tyrlib2.materials;

import com.tyrlib2.renderer.Material;
import com.tyrlib2.renderer.ProgramManager;

/** 
 * Material to display point lights
 * 
 * @author Sascha
 *
 */

public class PointLightMaterial extends Material {
	public PointLightMaterial() {
		program = ProgramManager.getInstance().getProgram("POINT_LIGHT");
		init(3,0,3, "u_MVPMatrix", "a_Position");
	}
}
