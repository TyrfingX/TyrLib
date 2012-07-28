package tyrfing.common.render;

import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import tyrfing.common.renderables.Batch;
import tyrfing.common.renderables.Circle;
import tyrfing.common.renderables.Image;
import tyrfing.common.renderables.Line;
import tyrfing.common.renderables.Rectangle;
import tyrfing.common.renderables.Renderable;
import tyrfing.common.renderables.Text;
import tyrfing.common.struct.Node;

public class SceneManager {
	
	
	private static Map<String, Batch> batches;
	
	private SceneManager() { }
	
	public static RenderThread RENDER_THREAD;
	
	public static void init(RenderThread thread)
	{
		SceneManager.RENDER_THREAD = thread;
		if (batches == null) batches = new HashMap<String, Batch>();
	}
	
	public static Circle createCircle(float r, int color, Node parent)
	{
		Circle circle = new Circle(r, color, parent);
		RENDER_THREAD.addRenderable(circle);
		circle.setVisible(false);
		return circle;		
	}
	
	public static Image createImage(Bitmap bitmap, Node parent)
	{
		Image image;
		if (parent != null) {
			image = new Image(bitmap, parent);
		}
		else {
			image = new Image(bitmap);
		}
		RENDER_THREAD.addRenderable(image);
		return image;		
	}

	public static Line createLine(float toX, float toY, int color, Node parent)
	{
		Line line = new Line(toX, toY, color, parent);
		RENDER_THREAD.addRenderable(line);
		return line;		
	}
	
	public static Rectangle createRectangle(float width, float height, int color, Node parent)
	{
		Rectangle rect = new Rectangle(width, height, color, parent);
		RENDER_THREAD.addRenderable(rect);
		return rect;			
	}	
	

	public static Text createText(String text, int color, Node parent)
	{
		Text text_r = new Text(text, color, parent);
		RENDER_THREAD.addRenderable(text_r);
		return text_r;			
	}
	

	public static Animation createAnimation(Bitmap bitmap, int frames, float timePerFrame, Node node)
	{
		Animation anim = new Animation(bitmap, frames, timePerFrame, node);
		RENDER_THREAD.addRenderable(anim);
		return anim;
	}
	
	public static Batch createBatch(String name, Node node, float width, float height)
	{
		Batch batch = new Batch(node, (int)width, (int)height);
		batches.put(name, batch);
		RENDER_THREAD.addRenderable(batch);
		return batch;
	}
	
	public static Batch getBatch(String name)
	{
		return batches.get(name);
	}
	
	public static void addBatch(String name, Batch batch)
	{
		batches.put(name, batch);
	}
	
	public static void removeBatch(String name)
	{
		batches.remove(name);
	}
	
	public static void destroyRenderable(Renderable renderable) {
		RENDER_THREAD.removeRenderable(renderable);
		renderable.detach();
	}
	
}
