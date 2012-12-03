package tyrfing.common.ui.widgets;

import android.graphics.Bitmap;
import android.graphics.Color;
import tyrfing.common.input.InputManager;
import tyrfing.common.math.Vector2;
import tyrfing.common.render.Ressources;
import tyrfing.common.ui.Event;
import tyrfing.common.ui.Window;
import tyrfing.common.ui.WindowManager;

public class Button extends Window {

	private ImageBox bg;
	private Label caption;
	private Bitmap normal;
	private Bitmap click;
	private Bitmap disabled;
	
	public static int NORMAL_TEXT_COLOR = Color.BLACK;
	public static int CLICK_TEXT_COLOR = Color.BLACK;
	public static int DISABLED_TEXT_COLOR = Color.GRAY;
	public static float TEXT_OFFSET = 0;
	
	public Button(String name, float x, float y, float w, float h, String text, String normalName, String clickName, String disabledName) {
		super(name, x, y, w, h);
		this.normal = Ressources.getScaledBitmap(normalName, new Vector2(w,h));
		this.click = Ressources.getScaledBitmap(clickName, new Vector2(w,h));
		this.disabled = Ressources.getScaledBitmap(disabledName, new Vector2(w,h));
		
		caption = WindowManager.createLabel(name + "/Caption", 0, TEXT_OFFSET, w, h, text, Color.TRANSPARENT);
		caption.setCaptionColor(NORMAL_TEXT_COLOR);
		
		bg = WindowManager.createImageBox(name + "/Bg", 0, 0, w, h, normal);
		this.addChild(bg);
		bg.addChild(caption);
		InputManager.addTouchListener(this);
	}

	@Override
	public boolean onTouchDown(Vector2 point) {
		if (this.enabled)
		{
		
			if (this.isPointInWindow(point))
			{
				bg.setImage(click);
				caption.setCaptionColor(CLICK_TEXT_COLOR);
				return true;
			}
			
		}
		
		return false;
	}

	
	public void setCaption(String caption)
	{
		this.caption.setCaption(caption);
	}
	
	@Override
	public boolean onTouchUp(Vector2 point) {
		
		if (this.enabled)
		{
			bg.setImage(normal);
			caption.setCaptionColor(NORMAL_TEXT_COLOR);
			
			if (this.isPointInWindow(point))
			{
				this.evokeClick(new Event(this));
				return true;
			}
		
		}
		
		return false;
	
	}

	@Override
	public boolean onTouchMove(Vector2 point) {
		
		
		if (this.enabled)
		{
			if (this.isPointInWindow(point))
			{
				bg.setImage(click);
				caption.setCaptionColor(CLICK_TEXT_COLOR);
				return true;
			}
			else
			{
				bg.setImage(normal);
				caption.setCaptionColor(NORMAL_TEXT_COLOR);
			}
		}
		
		return false;
	}
	
	public void enable()
	{
		super.enable();
		bg.setImage(normal);
		caption.setCaptionColor(NORMAL_TEXT_COLOR);
	}
	
	public void disable()
	{
		super.disable();
		bg.setImage(disabled);
		caption.setCaptionColor(DISABLED_TEXT_COLOR);
	}
	
	public void setVisible(boolean visible)
	{
		if (visible != this.visible)
		{
			if 		(!visible) 		InputManager.removeTouchListener(this);
			else 					InputManager.addTouchListener(this);
		}
		super.setVisible(visible);
	}
	
	public void setEnabled(boolean enable)
	{
		super.setEnabled(enable);
		if (!this.enabled)
		{
			bg.setImage(disabled);
			caption.setCaptionColor(DISABLED_TEXT_COLOR);
		}
		else
		{
			bg.setImage(normal);
			caption.setCaptionColor(NORMAL_TEXT_COLOR);
		}
	}
	
	
	public void destroy()
	{
		super.destroy();
		InputManager.removeTouchListener(this);
	}

}
