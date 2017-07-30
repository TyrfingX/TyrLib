package com.tyrfing.games.id17.gui.technology;

import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.technology.Technology;
import com.tyrlib2.game.IUpdateable;
import com.tyrlib2.graphics.materials.TexturedMaterial;
import com.tyrlib2.graphics.renderables.Image2;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.gui.ImageBox;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.Window;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.main.Media;
import com.tyrlib2.math.Quaternion;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.math.Vector3;

public class TechArrow implements IUpdateable {
	public ImageBox block;
	public TechMaterial mat;
	
	private boolean finished;
	
	private TechnologyGUI ui;
	private Technology t;
	private Technology t2;
	
	public static final ScaledVector2 HEAD_SIZE = new ScaledVector2(0.0f, 0.06f, 2);
	
	public TechArrow(ImageBox start, ImageBox end, Window parent, TechnologyGUI ui, Technology t, Technology t2) {
		this.t = t;
		this.t2 = t2;
		
		Vector2 startCenter = start.getRelativePos().add(start.getSize().multiply(0.5f));
		Vector2 endCenter = end.getRelativePos().add(end.getSize().multiply(0.5f));
		
		Vector2 startCenterW = new Vector2(startCenter.x * SceneManager.getInstance().getViewportWidth(), startCenter.y * Media.CONTEXT.getScreenSize().y);
		Vector2 endCenterW = new Vector2(endCenter.x * SceneManager.getInstance().getViewportWidth(), endCenter.y * Media.CONTEXT.getScreenSize().y);
		
		Vector2 direction = startCenterW.vectorTo(endCenterW);
		float length = 0;
		
		if (Math.abs(startCenter.y - endCenter.y) > 0.001f) {
			length = direction.normalize()/(WindowManager.getInstance().getScale(2).x*WindowManager.getInstance().getScale(2).x) - start.getSize().y*Media.CONTEXT.getScreenSize().y/3;
		} else {
			length = direction.normalize() - start.getSize().x*Media.CONTEXT.getScreenSize().x/1.4f;
		}
		
		startCenter = startCenter.add(new Vector2(direction.x * start.getSize().x/3, direction.y * start.getSize().y/3/WindowManager.getInstance().getScale(2).x ));
		
		Vector2 blockSize = new Vector2(length / Media.CONTEXT.getScreenSize().x, HEAD_SIZE.get().y);
		block = (ImageBox) WindowManager.getInstance()
							 			.createImageBox(end.getName() + "/PRE/" + start.getName(), 
							 							startCenter, 
							 							"TECHARROW", 
							 							"BLOCK",
							 							blockSize);
		parent.addChild(block);
		
		Quaternion quat = Quaternion.rotateTo(new Vector3(1, 0, 0), new Vector3(direction.x, -direction.y, 0));
		Vector3 off = new Vector3(0,HEAD_SIZE.multiply(0.5f).get().y, 0);
		Vector3 rotOff = quat.multiply(off);
		block.setRelativePos(block.getRelativePos().sub(new Vector2(-rotOff.x/SceneManager.getInstance().getViewportRatio(), rotOff.y)));
		
		Image2 rect = block.getImage();
		
		if (Math.abs(startCenter.y - endCenter.y) > 0.001f) {
			rect.rotate(quat);
		}
		
		mat = new TechMaterial(((TexturedMaterial)rect.getMaterial()).getTexture());
		rect.setMaterial(mat);
		
		this.ui = ui;
	}
	
	public TechArrow(ImageBox end, Window parent, TechnologyGUI ui, Technology t) {
		this.t = t;
		
		Vector2 startCenter = end.getRelativePos().add(end.getSize().multiply(0.5f)).sub(new Vector2(0.04f, 0));
		Vector2 endCenter = end.getRelativePos().add(end.getSize().multiply(0.5f));
		
		Vector2 startCenterW = new Vector2(startCenter.x * SceneManager.getInstance().getViewportWidth(), startCenter.y * Media.CONTEXT.getScreenSize().y);
		Vector2 endCenterW = new Vector2(endCenter.x * SceneManager.getInstance().getViewportWidth(), endCenter.y * Media.CONTEXT.getScreenSize().y);
		
		Vector2 direction = startCenterW.vectorTo(endCenterW);
		float length = 0;
		
		if (Math.abs(startCenter.y - endCenter.y) > 0.001f) {
			length = direction.normalize()/(WindowManager.getInstance().getScale(2).x*WindowManager.getInstance().getScale(2).x) - end.getSize().y*Media.CONTEXT.getScreenSize().y/3;
		} else {
			length = direction.normalize() - end.getSize().x*Media.CONTEXT.getScreenSize().x/1.4f;
		}
		
		startCenter = startCenter.add(new Vector2(direction.x * end.getSize().x/3, direction.y * end.getSize().y/3/WindowManager.getInstance().getScale(2).x ));
		
		Vector2 blockSize = new Vector2(length / Media.CONTEXT.getScreenSize().x, HEAD_SIZE.get().y);
		block = (ImageBox) WindowManager.getInstance()
							 			.createImageBox(end.getName() + "/PRE/" + end.getName(), 
							 							startCenter, 
							 							"TECHARROW", 
							 							"BLOCK",
							 							blockSize);
		parent.addChild(block);
		
		Quaternion quat = Quaternion.rotateTo(new Vector3(1, 0, 0), new Vector3(direction.x, -direction.y, 0));
		Vector3 off = new Vector3(0,HEAD_SIZE.multiply(0.5f).get().y, 0);
		Vector3 rotOff = quat.multiply(off);
		block.setRelativePos(block.getRelativePos().sub(new Vector2(-rotOff.x/SceneManager.getInstance().getViewportRatio(), rotOff.y)));
		
		Image2 rect = block.getImage();
		
		if (Math.abs(startCenter.y - endCenter.y) > 0.001f) {
			rect.rotate(quat);
		}
		
		mat = new TechMaterial(((TexturedMaterial)rect.getMaterial()).getTexture());
		rect.setMaterial(mat);
		
		this.ui = ui;
	}

	@Override
	public void onUpdate(float time) {
		House house = ui.getDisplayed();
		if (house != null) {
			if (house.techProject != null  && house.techProject.tech == t) {
				mat.setProgress(ui.getDisplayed().techProject.getProgress());
			} else if (house.hasResearched(t)) {
				mat.setProgress(1);
			} else {
				mat.setProgress(0);
			}
			
			if (t2 == null || house.hasResearched(t2)) {
				mat.bgColor.r = 1;
				mat.bgColor.g = 1;
				mat.bgColor.b = 0.5f;
			} else {
				mat.bgColor.r = 1;
				mat.bgColor.g = 1;
				mat.bgColor.b = 1;
			}
		}
	}

	@Override
	public boolean isFinished() {
		return finished;
	}
}
