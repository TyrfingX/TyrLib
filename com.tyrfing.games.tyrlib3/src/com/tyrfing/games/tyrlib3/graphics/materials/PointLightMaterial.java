package com.tyrfing.games.tyrlib3.graphics.materials;

import com.tyrfing.games.tyrlib3.graphics.renderer.Material;
import com.tyrfing.games.tyrlib3.graphics.renderer.ProgramManager;

/** 
 * Material to display point lights
 * 
 * @author Sascha
 *
 */

public class PointLightMaterial extends Material {
	public PointLightMaterial() {
		program = ProgramManager.getInstance().getProgram("POINT_LIGHT");
		init(0,3, "u_MVPMatrix", "a_Position");
	}
}
