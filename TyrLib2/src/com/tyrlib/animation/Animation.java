package com.tyrlib.animation;

import java.util.ArrayList;
import java.util.List;

import com.tyrlib2.game.IUpdateable;
import com.tyrlib2.math.Quaternion;
import com.tyrlib2.math.Vector3;

/**
 * This class represents an animation by manipulating a skeleton.
 * @author Sascha
 *
 */

public class Animation implements IUpdateable {
	
	/** Animation data **/
	protected String name;
	protected boolean loop;
	protected boolean playing;
	protected float duration;
	protected float animTime;
	protected List<AnimationFrame> animationFrames;
	protected int currentFrame;
	protected Skeleton skeleton;
	
	public Animation(String name) {
		this.name = name;
		this.playing = false;
		animationFrames = new ArrayList<AnimationFrame>();
	}
	
	public void play() {
		playing = true;
	}
	
	public void pause() {
		
	}
	
	public void reset() {
		animTime = 0;
	}
	
	public String getName() {
		return name;
	}
	
	public void setLooping(boolean loop) {
		this.loop = loop;
	}
	
	public boolean isLooping() {
		return loop;
	}
	
	public float duration() {
		return duration;
	}
	
	public void addAllFrames(List<AnimationFrame> animationFrames) {
		this.animationFrames.addAll(animationFrames);
		duration = animationFrames.get(animationFrames.size()-1).time;
	}

	@Override
	public void onUpdate(float time) {
		if (playing) {
			animTime += time;
			
			while(animTime > animationFrames.get(currentFrame).time) {
				currentFrame++;
				if (currentFrame == animationFrames.size()) {
					currentFrame = 0;
					animTime -= duration;
					
				}
			}
			
			AnimationFrame frame = getCurrentFrame();
			//skeleton.getBone(0).rotate(new Quaternion(30*time,0,1,1));
			//skeleton.getBone(1).rotate(new Quaternion(30*time,0,1,0));
			skeleton.getBone(31).rotate(Quaternion.fromAxisAngle(new Vector3(0,1,0), time*30));
			
			for (int i = 0; i < skeleton.bones.size(); ++i) {
				/*
				Bone bone = skeleton.bones.get(i);
				bone.setRelativePos(frame.bonePos[i]);
				Quaternion q = frame.boneRot[i];
				Quaternion quat = bone.initRot.multiply(q);
				
				if (quat.x == 0 && quat.y == 0 && quat.z == 0) {
					quat.w = 1;
				}
				bone.setRelativeRot(quat);
				*/
			}	
			
		}
	}
	
	public AnimationFrame getCurrentFrame() {
		return animationFrames.get(currentFrame);
	}

	@Override
	public boolean isFinished() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
