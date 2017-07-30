package tyrfing.common.ui.widgets;

import tyrfing.common.render.SceneManager;
import tyrfing.common.renderables.Rectangle;
import tyrfing.common.ui.Window;
import android.graphics.Color;

public class ProgressBar extends Window {

	public static int BAR_COLOR = Color.RED;
	public static int BG_COLOR = Color.BLACK;
	
	private Rectangle progressBar;
	
	private float progress;
	
	public enum BarType {
		VERTICAL,
		HORIZONTAL
	};
	
	private BarType type;
	
	public ProgressBar(String name, float x, float y, float w, float h, float progress) {
		super(name, x, y, w, h);
		
		this.type = BarType.HORIZONTAL;
		
		progressBar = SceneManager.createRectangle(w, h, BAR_COLOR, node);
		Rectangle bg = SceneManager.createRectangle(w, h, BG_COLOR, node);
	
		components.add(bg);
		components.add(progressBar);
		
		this.setProgress(progress);
	
	}
	
	public void setBarType(BarType type) {
		this.type = type;
	}
	
	public float getProgress() {
		return progress;
	}
	
	public void setProgress(float progress) {
		this.progress = progress;
		if (type == BarType.HORIZONTAL) {
			progressBar.setWidth(this.getWidth()*progress);
		} else {
			progressBar.setHeight(this.getHeight()*progress);
		}
	}

}
