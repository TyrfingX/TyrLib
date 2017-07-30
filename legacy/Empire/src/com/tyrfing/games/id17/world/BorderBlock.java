package com.tyrfing.games.id17.world;

import com.tyrlib2.util.Direction4;

public class BorderBlock {
	public int x, y;
	public int length;
	public Direction4 direction;
	public BorderBlock(int x, int y, Direction4 direction) {
		this.x = x;
		this.y = y;
		this.direction = direction;
		this.length = 0;
	}
}