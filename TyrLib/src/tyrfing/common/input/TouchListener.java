package tyrfing.common.input;

import tyrfing.common.math.Vector2;
import tyrfing.common.struct.Prioritizable;

public interface TouchListener extends Prioritizable{
	public boolean onTouchDown(Vector2 point);
	public boolean onTouchUp(Vector2 point);
	public boolean onTouchMove(Vector2 point);
	public boolean isEnabled();
}
