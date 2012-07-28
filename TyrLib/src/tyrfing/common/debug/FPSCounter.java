package tyrfing.common.debug;

import android.graphics.Color;
import tyrfing.common.render.IFrameListener;
import tyrfing.common.render.SceneManager;
import tyrfing.common.renderables.Text;
import tyrfing.common.struct.Node;

public class FPSCounter implements IFrameListener {

	protected Node node;
	protected Text fps;
	protected float passedTime;
	protected int frames;
	
	public FPSCounter(Node node)
	{
		this.node = node;
		fps = SceneManager.createText("0", Color.WHITE, node);
		fps.setPriority(1000000);
	}
	
	@Override
	public void onUpdate(float time) {
		passedTime += time;
		frames++;
		if (passedTime >= 1)
		{
			passedTime -= 1;
			fps.setText("" + frames);
			frames = 0;
		}
		
	}

	@Override
	public boolean isFinished() {
		return false;
	}

	@Override
	public void onClearRenderer() {
		fps = SceneManager.createText("dfds", Color.WHITE, node);
	}


}
