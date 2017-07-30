package tyrfing.common.ui.widgets;

import android.graphics.Bitmap;
import tyrfing.common.input.InputManager;
import tyrfing.common.render.SceneManager;
import tyrfing.common.renderables.Image;
import tyrfing.common.ui.Window;

public class ImageBox extends Window{

	private Image image;
	
	public ImageBox(String name, float x, float y, float w, float h, Bitmap bitmap) {
		super(name, x, y, w, h);
		image = SceneManager.createImage(bitmap, node);
		components.add(image);
	}
	
	public void setImage(Bitmap bitmap)
	{
		Image img = image;
		long prio = image.getPriority();
		image = SceneManager.createImage(bitmap, node);
		image.setPriority(prio);
		image.setVisible(img.getVisible());
		SceneManager.RENDER_THREAD.removeRenderable(img);
		components.remove(img);
		components.add(image);
	}
	
	

}
