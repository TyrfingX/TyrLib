package com.tyrfing.games.tyrlib3.model.game;

import com.tyrfing.games.tyrlib3.model.math.Vector2I;

public enum Direction4 {
	LEFT, RIGHT, TOP, BOTTOM;
	
	public boolean isOpposite(Direction4 other) {
		Direction4 opposite = getOppostite();
		return other.equals(opposite);
	}
	
	public Direction4 getOppostite() {
		switch (this) {
		case LEFT:
			return RIGHT;
		case RIGHT:
			return LEFT;
		case TOP:
			return BOTTOM;
		case BOTTOM:
			return TOP;
		default:
			return null;
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

	public static Direction4 fromUnitVector(Vector2I unitVector) {
		if (unitVector.equals(Vector2I.UNIT_X)) {
			return Direction4.RIGHT;
		}
		
		if (unitVector.equals(Vector2I.NEGATIVE_UNIT_X)) {
			return Direction4.LEFT;
		}
		
		if (unitVector.equals(Vector2I.UNIT_Y)) {
			return Direction4.TOP;
		}
		
		if (unitVector.equals(Vector2I.NEGATIVE_UNIT_Y)) {
			return Direction4.BOTTOM;
		}
		
		return null;
	}
}
