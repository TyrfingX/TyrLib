package com.tyrfing.games.tyrlib3.model.game;

public enum Direction4 {
	LEFT, RIGHT, TOP, BOTTOM;
	
	public boolean isOpposite(Direction4 other) {
		switch (this) {
		case LEFT:
			return other == Direction4.RIGHT;
		case RIGHT:
			return other == Direction4.LEFT;
		case TOP:
			return other == Direction4.BOTTOM;
		case BOTTOM:
			return other == Direction4.TOP;
		default:
			return false;
		}
	}
	
	public Direction4 turn(Direction4 dir) {
		switch (this) {
		case LEFT:
			return dir == Direction4.LEFT ? Direction4.BOTTOM : Direction4.TOP;
		case RIGHT:
			return dir == Direction4.LEFT ? Direction4.TOP : Direction4.BOTTOM;
		case TOP:
			return dir == Direction4.LEFT ? Direction4.LEFT : Direction4.RIGHT;
		case BOTTOM:
			return dir == Direction4.LEFT ? Direction4.RIGHT : Direction4.LEFT;
		default:
			return this;
		}
	}
}
