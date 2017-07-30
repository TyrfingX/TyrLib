package com.tyrfing.games.id17.war;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id17.holdings.Holding;
import com.tyrlib2.graphics.materials.DefaultMaterial3;
import com.tyrlib2.graphics.materials.LightedMaterial;
import com.tyrlib2.graphics.renderables.Entity;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.math.Quaternion;
import com.tyrlib2.math.Vector3;

public class ArrowPath {

	private List<Entity> segments = new ArrayList<Entity>();
	
	public static final Vector3 INIT_DIRECTION = new Vector3(0,-1,0);
	private static Vector3 SIZE = null;
	
	private int activeSegment = 0;
	private float percentPerSegment;
	
	static {
		INIT_DIRECTION.normalize();
	}
	
	private int segmentCount = 5;
	
	public ArrowPath(Army army, Holding src, Holding target) {
		Vector3 srcPos = null;
		if (src != null) {
			srcPos = src.holdingData.worldEntity.getParent().getAbsolutePos().add(target.getArmyOffset());
		} else {
			srcPos = army.node.getAbsolutePos();
		}
		
		Vector3 targetPos = target.holdingData.worldEntity.getParent().getAbsolutePos().add(target.getArmyOffset());
		
		Vector3 vectorTo = srcPos.vectorTo(targetPos);
		float dist = vectorTo.length() * 0.9f;
		vectorTo.z = 0;
		vectorTo.normalize();
		float z = vectorTo.z;
		Vector3 dep = vectorTo.cross(INIT_DIRECTION);
		
		Quaternion quat = new Quaternion();
		
		if (Math.abs(dep.x) > Army.EPS || Math.abs(dep.y) > Army.EPS || Math.abs(dep.z) > Army.EPS) {
			quat = Quaternion.rotateTo(INIT_DIRECTION, vectorTo);
			Vector3 dest = new Vector3(vectorTo.x, vectorTo.y, z);
			Quaternion quat2 = Quaternion.rotateTo(vectorTo, dest);
			quat = quat.multiply(quat2);
		} else if (Math.abs(INIT_DIRECTION.x + vectorTo.x) < Army.EPS && Math.abs(INIT_DIRECTION.y + vectorTo.y) < Army.EPS && Math.abs(INIT_DIRECTION.z + vectorTo.z) < Army.EPS) {
			quat = Quaternion.fromAxisAngle(new Vector3(0,0,1), 180);
		}
		
		Vector3 offset = null;
		if (SIZE != null) {
			offset = SIZE.multiply(2);
			segmentCount = (int) (-dist / (SIZE.y * 2)) - 1;
		}
		
		for (int i = 0; i < segmentCount; ++i) {
		
			Entity segment;
			
			SceneNode node = SceneManager.getInstance().getRootSceneNode().createChild();
			node.scale(new Vector3(Army.SCALE, Army.SCALE, Army.SCALE));
			node.rotate(quat);
			
			if (i == segmentCount - 1){
				segment = SceneManager.getInstance().createEntity("entities/arrowhead.iqm");
			} else {
				segment = SceneManager.getInstance().createEntity("entities/arrowblock.iqm");
			}
			
			segment.getSubEntity(0).setMaterial(segment.getSubEntity(0).getMaterial().copy());
			segment.setCastShadow(false);
			((DefaultMaterial3)segment.getSubEntity(0).getMaterial()).setTransparent(true);
			((DefaultMaterial3)segment.getSubEntity(0).getMaterial()).setBlendMode(TyrGL.GL_ONE);
			
			SceneManager.getInstance().getRenderer().removeRenderable(segment);
			SceneManager.getInstance().getRenderer().addRenderable(segment, OpenGLRenderer.TRANSLUCENT_CHANNEL_2);
			
			node.attachSceneObject(segment);
			if (SIZE == null) {
				SIZE = new Vector3(0, -(segment.getBoundingBox().max.x - segment.getBoundingBox().min.x)*(1+Army.SCALE),0);
				offset = SIZE.multiply(2);
				segmentCount = (int) (-dist / (SIZE.y * 2)) - 1;
			}
			
			segments.add(segment);
			Vector3 pos = quat.multiply(offset);
			node.setRelativePos(srcPos.add(pos));
			
			//((DefaultMaterial3)segments.get(i).getSubEntity(0).getMaterial()).setTransparent(true);
			
			if (i == segmentCount - 1){
				((DefaultMaterial3)segments.get(i).getSubEntity(0).getMaterial()).setTexture("ARROW_HEAD");
			} else {
				((DefaultMaterial3)segments.get(i).getSubEntity(0).getMaterial()).setTexture("ARROW_BLOCK");
			}
			
			offset = offset.add(SIZE.multiply(2));
			
		}
		
		percentPerSegment = 1.f / (segments.size()+1);
	}
	
	public void destroy(){
		for (int i = 0; i < segments.size(); ++i) {
			SceneManager.getInstance().destroyRenderable(segments.get(i));
		}
	}
	
	public void update(float percent) {
		if (percent > percentPerSegment * (activeSegment+1) && activeSegment < segments.size()) {
			((DefaultMaterial3)segments.get(activeSegment).getSubEntity(0).getMaterial()).setTransparent(true);
			((DefaultMaterial3)segments.get(activeSegment).getSubEntity(0).getMaterial()).setBlendMode(TyrGL.GL_ONE_MINUS_SRC_ALPHA);
			if (activeSegment == segmentCount - 1) {
				((DefaultMaterial3)segments.get(activeSegment).getSubEntity(0).getMaterial()).setTexture("ARROW_HEAD");
			} else {
				((DefaultMaterial3)segments.get(activeSegment).getSubEntity(0).getMaterial()).setTexture("ARROW_BLOCK");
			}
			
			activeSegment++;
		}
		
	}

	public void setVisible(boolean b) {
		for (int i = 0; i < segments.size(); ++i) {
			segments.get(i).setVisible(b);
		}
	}
}
