package com.tyrfing.games.id17.world;

import com.tyrlib2.math.Vector3;
import com.tyrlib2.movement.ITargetProvider;

public class RoadNodeProvider implements ITargetProvider {

	private Vector3 pos;
	
	public RoadNodeProvider(RoadNode node) {
		pos = node.getTargetPos().add(new Vector3(
			(float)(Math.random()-0.5)*WorldChunk.BLOCK_SIZE*0.25f,
			(float)(Math.random()-0.5)*WorldChunk.BLOCK_SIZE*0.25f,
			0
		));
	}
	
	@Override
	public Vector3 getTargetPos() {
		return pos;
	}

}
