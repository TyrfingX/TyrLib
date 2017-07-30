package tyrfing.games.id3.lib.rooms;

import android.graphics.Color;

public enum DoorColor {
	RED, BLUE, YELLOW;
	
	public int toColor() {
		switch(this) {
		case RED:
			return Color.RED;
		case BLUE:
			return Color.BLUE;
		case YELLOW:
			return Color.YELLOW;
		}
		
		return Color.RED;
	}
}
