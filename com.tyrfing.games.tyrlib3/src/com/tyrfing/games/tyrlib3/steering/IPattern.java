package com.tyrfing.games.tyrlib3.steering;

import com.tyrfing.games.tyrlib3.math.Vector3F;

/**
 * An interface for steering patterns.
 * @author Sascha
 *
 */

public interface IPattern {
	public Vector3F apply(IVehicle vehicle);
}
