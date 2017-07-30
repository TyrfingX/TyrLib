package tyrfing.common.render;

import tyrfing.common.renderables.Image;
import tyrfing.common.struct.Node;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Animation extends Image {

	private int frames;
	private int currentFrame;
	private float timePerFrame;
	private float currentTime;
	private boolean repeat;
	
	public Animation(Bitmap bitmap, int frames, float timePerFrame) {
		super(bitmap);
		this.frames = frames;
		currentFrame = 0;
		currentTime = 0;
		this.timePerFrame = timePerFrame;
		area = new Rect(0,0,bitmap.getWidth(), bitmap.getHeight());
		this.setAnimRect();
		this.setRepeat(true);
	}

	public Animation(Bitmap bitmap, int frames, float timePerFrame, Node node) {
		super(bitmap, node);
		this.frames = frames;
		currentFrame = 0;
		currentTime = 0;
		this.timePerFrame = timePerFrame;
		area = new Rect(0,0,bitmap.getWidth(), bitmap.getHeight());
		this.setAnimRect();
		this.setRepeat(true);
	}
	
	private void setAnimRect()
	{
		int frameWidth = bitmap.getWidth() / frames; 
		area.left = currentFrame * frameWidth;
		area.right = area.left + frameWidth;
	}
	
	public void setUsedArea(Rect area)
	{
		super.setUsedArea(area);
		this.setAnimRect();
	}
	
	public int getCountFrames()
	{
		return frames;
	}
	
	public void setCountFrames(int frames)
	{
		this.frames = frames;
	}
	
	public void setRepeat(boolean repeat)
	{
		this.repeat = repeat;
	}
	
	public void setFrame(int frame)
	{
		currentFrame = frame;
	}
	
	public void nextFrame()
	{
		currentFrame++;
	}
	
	public void previousFrame()
	{
		currentFrame--;
		if (currentFrame < 0)
		{
			currentFrame = frames - 1;
		}
	}

	public int getCurrentFrame()
	{
		return currentFrame;
	}
	
	public void onRender(Canvas target, float time)
	{	
		if (timePerFrame != 0)
		{
			// Update only if time dependency is set
			while (currentTime >= timePerFrame)
			{
				currentTime -= timePerFrame;
				currentFrame = currentFrame + 1;
				if (repeat)
				{
					currentFrame = currentFrame  % frames;
				}
				else
				{
					if (currentFrame >= frames) currentFrame = frames - 1;
				}
			}
			
			if (currentFrame < frames)
			{
				this.setAnimRect();
				super.onRender(target, time);
				
				currentTime += time;
			}			
		}
		else
		{
			this.setAnimRect();
			super.onRender(target, time);
		}

		
	}
	
}
