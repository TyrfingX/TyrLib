package tyrfing.common.ui.widgets;

import android.R.color;
import android.graphics.Color;
import tyrfing.common.render.SceneManager;
import tyrfing.common.render.TargetMetrics;
import tyrfing.common.renderables.Rectangle;
import tyrfing.common.renderables.Text;
import tyrfing.common.struct.Node;
import tyrfing.common.ui.Window;

public class Label extends Window{

	public static int DEFAULT_BG_COLOR = color.black;
	public static int DEFAULT_TEXT_COLOR = color.white;
	
	Rectangle background;
	Text caption;
	Node textNode;

	float width;
	
	public Label(String name, float x, float y, float w, float h) {
		super(name, x, y, w, h);
		
		if (Label.DEFAULT_BG_COLOR != Color.TRANSPARENT)
		{
			background = SceneManager.createRectangle(w,h, Label.DEFAULT_BG_COLOR, node);
			components.add(background);
		}
		
		textNode = node.createChild(0,0.6f*h);
		caption = SceneManager.createText("", Color.WHITE, textNode);
		caption.setColor(DEFAULT_TEXT_COLOR);

		components.add(caption);
		
		width = w;
	}
	
	public void setCaption(String caption)
	{
		String[] strLines = caption.split("\n");
		
		int length = strLines[0].length();
		for (String line : strLines)
		{
			if (line.length() > length)
			{
				length = line.length();
			}
		}
		
		textNode.setX((float)(width*0.40 - length*TargetMetrics.width*0.005f));
		this.caption.setText(caption);
	}
	
	public void setCaptionColor(int color)
	{
		caption.setColor(color);
	}
	
	public String getCaption()
	{
		return caption.getText();
	}
	
	public void setBgColor(int color)
	{
		if (background.getColor() == Color.TRANSPARENT && color != Color.TRANSPARENT)
		{
			background = SceneManager.createRectangle(w,h, Label.DEFAULT_BG_COLOR, node);
			components.add(background);
		} else if (background.getColor() != Color.TRANSPARENT && color == Color.TRANSPARENT)
		{
			SceneManager.RENDER_THREAD.removeRenderable(background);
			components.remove(background);
		}
		background.setColor(color);
	}
	
	public int getBgColor()
	{
		return background.getColor();
	}
	
	
}
