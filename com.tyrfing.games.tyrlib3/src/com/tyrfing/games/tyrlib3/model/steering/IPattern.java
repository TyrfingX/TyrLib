package com.tyrfing.games.tyrlib3.model.steering;

import com.tyrfing.games.tyrlib3.model.math.Vector3F;

/**
 * An interface for steering patterns.
 * @author Sascha
 *
 */

public interface IPattern {
	public Vector3F apply(IVehicle vehicle);
}
