package com.tyrlib2.gui;

import java.util.HashMap;
import java.util.Map;

import com.tyrlib2.math.Vector2;

/**
 * A basic GUI element for containing other GUI elements
 * @author Sascha
 *
 */

public class Frame extends Window{

	public enum FrameImagePosition {
		MIDDLE,
		LEFT, RIGHT, BOTTOM, TOP,
		TOPLEFT, TOPRIGHT, BOTTOMLEFT, BOTTOMRIGHT,
	}
	
	private Map<FrameImagePosition, String> bgRegions;
	private Map<FrameImagePosition, ImageBox> bgImageBoxes;
	private float borderSize;

	private float alpha;
	
	public Frame(String name, Vector2 pos, Vector2 size) {
		super(name, size);
		
		setRelativePos(pos);
		
		bgRegions = new HashMap<FrameImagePosition, String>();
		bgImageBoxes = new HashMap<FrameImagePosition, ImageBox>();
		
		Skin skin = WindowManager.getInstance().getSkin();
		bgRegions.put(FrameImagePosition.LEFT, skin.FRAME_LEFT);
		bgRegions.put(FrameImagePosition.RIGHT, skin.FRAME_RIGHT);
		bgRegions.put(FrameImagePosition.TOP, skin.FRAME_TOP);
		bgRegions.put(FrameImagePosition.BOTTOM, skin.FRAME_BOTTOM);

		bgRegions.put(FrameImagePosition.TOPLEFT, skin.FRAME_TOPLEFT);
		bgRegions.put(FrameImagePosition.TOPRIGHT, skin.FRAME_TOPRIGHT);
		bgRegions.put(FrameImagePosition.BOTTOMLEFT, skin.FRAME_BOTTOMLEFT);
		bgRegions.put(FrameImagePosition.BOTTOMRIGHT, skin.FRAME_BOTTOMRIGHT);
		
		borderSize = skin.FRAME_BORDER_SIZE;
		
		String atlas = skin.TEXTURE_ATLAS;
		
		WindowManager windowManager = WindowManager.getInstance();
		bgImageBoxes.put(FrameImagePosition.TOPLEFT,
						(ImageBox) windowManager.createImageBox(name + "/BackgroundTopLeft", 
							     new Vector2(), 
							     atlas, 
								 bgRegions.get(FrameImagePosition.TOPLEFT), 
								 new Vector2(borderSize, borderSize)));
		bgImageBoxes.put(FrameImagePosition.TOP,
						(ImageBox) windowManager.createImageBox(name + "/BackgroundTop", 
							     new Vector2(borderSize, 0), 
							     atlas, 
								 bgRegions.get(FrameImagePosition.TOP), 
								 new Vector2(size.x - 2.09f * borderSize, borderSize)));
		bgImageBoxes.put(FrameImagePosition.TOPRIGHT,
						(ImageBox) windowManager.createImageBox(name + "/BackgroundTopRight", 
							     new Vector2(size.x-borderSize, 0), 
							     atlas, 
								 bgRegions.get(FrameImagePosition.TOPRIGHT), 
								 new Vector2(borderSize, borderSize)));
		bgImageBoxes.put(FrameImagePosition.LEFT,
						(ImageBox) windowManager.createImageBox(name + "/BackgroundLeft", 
							     new Vector2(0, borderSize), 
							     atlas, 
								 bgRegions.get(FrameImagePosition.LEFT), 
								 new Vector2(borderSize, size.y - 2 * borderSize)));
		bgImageBoxes.put(FrameImagePosition.MIDDLE,
						(ImageBox) windowManager.createImageBox(name + "/BackgroundMiddle", 
							     new Vector2(borderSize, borderSize), 
							     atlas, 
								 bgRegions.get(FrameImagePosition.MIDDLE), 
								 new Vector2(size.x - 2 * borderSize, size.y - 2 * borderSize)));
		bgImageBoxes.put(FrameImagePosition.RIGHT,
						(ImageBox) windowManager.createImageBox(name + "/BackgroundRight", 
							     new Vector2(size.x-borderSize, borderSize), 
							     atlas, 
								 bgRegions.get(FrameImagePosition.RIGHT), 
								 new Vector2(borderSize, size.y - 2 * borderSize)));
		bgImageBoxes.put(FrameImagePosition.BOTTOMLEFT,
						(ImageBox) windowManager.createImageBox(name + "/BackgroundBottomLeft", 
							     new Vector2(0, size.y-borderSize), 
							     atlas, 
								 bgRegions.get(FrameImagePosition.BOTTOMLEFT), 
								 new Vector2(borderSize, borderSize)));
		bgImageBoxes.put(FrameImagePosition.BOTTOM,
						(ImageBox) windowManager.createImageBox(name + "/BackgroundBottom", 
							     new Vector2(borderSize, size.y-borderSize), 
							     atlas, 
								 bgRegions.get(FrameImagePosition.BOTTOM), 
								 new Vector2(size.x - 2.09f * borderSize, borderSize)));
		bgImageBoxes.put(FrameImagePosition.BOTTOMRIGHT,
						(ImageBox) windowManager.createImageBox(name + "/BackgroundBottomRight", 
							     new Vector2(size.x-borderSize, size.y-borderSize), 
							     atlas, 
								 bgRegions.get(FrameImagePosition.BOTTOMRIGHT), 
								 new Vector2(borderSize, borderSize)));
		
		for (FrameImagePosition position : FrameImagePosition.values()) {
			bgImageBoxes.get(position).setReceiveTouchEvents(false);
			this.addChild(bgImageBoxes.get(position));
		}
	}
	
	public float getAlpha() {
		return alpha;
	}
	
	public void setAlpha(float alpha) {
		super.setAlpha(alpha);
		this.alpha = alpha;
	}
	
	public void setBgRegion(FrameImagePosition position, String regionName) {
		bgRegions.put(position, regionName);
		ImageBox imageBox = bgImageBoxes.get(position);
		imageBox.setAtlasRegion(regionName);
	}
	
	public float getBorderSize() {
		return borderSize;
	}
	
}
