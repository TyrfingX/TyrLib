package com.tyrlib2.ai.steering;

import com.tyrlib2.math.Vector3;

/**
 * An interface for steering patterns.
 * @author Sascha
 *
 */

public interface IPattern {
	public Vector3 apply(IVehicle vehicle);
}
