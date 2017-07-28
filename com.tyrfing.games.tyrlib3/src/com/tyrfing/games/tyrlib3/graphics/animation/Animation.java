package com.tyrfing.games.tyrlib3.graphics.animation;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.tyrlib3.game.IUpdateable;
import com.tyrfing.games.tyrlib3.math.Quaternion;
import com.tyrfing.games.tyrlib3.math.Vector3F;

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
	
	private Quaternion interpolant = new Quaternion();
	private Vector3F tmp = new Vector3F();
	
	public Animation(String name) {
		this.name = name;
		this.playing = false;
		animationFrames = new ArrayList<AnimationFrame>();
	}
	
	public void play() {
		playing = true;
	}
	
	public void stop() {
		playing = false;
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
	
	public void addFrame(AnimationFrame frame) {
		if (animationFrames.size() != 0) {
			if (frame.time > animationFrames.get(animationFrames.size()-1).time) {
				duration = frame.time;
			}
		} else {
			duration = frame.time;
		}
		this.animationFrames.add(frame);

	}

	@Override
	public void onUpdate(float time) {
		if (playing && time < 0.5f) {
			animTime += time;
			
			while(animTime > animationFrames.get(currentFrame+1).time) {
				currentFrame++;
				if (currentFrame == animationFrames.size() - 1) {
					if (loop) {
						currentFrame = 0;
						animTime -= duration;
					} else {
						animTime = duration;
						playing = false;
						return;
					}
				}
			}
			
			AnimationFrame frame = getCurrentFrame();
			float alpha = 0;// blend factor between the current frame and the next one
			AnimationFrame nextFrame = null;
			
			if (currentFrame < animationFrames.size() - 1) {
				nextFrame = animationFrames.get(currentFrame+1);
				float timeDiff = animTime - frame.time;
				float totalTimeDiff = nextFrame.time -  frame.time;
				alpha = timeDiff / totalTimeDiff;
			} 
			
			for (int i = 0; i < skeleton.bones.size(); ++i) {
			
				Bone bone = skeleton.bones.get(i);
				
				Quaternion q1 = frame.boneRot[i];
				Vector3F v1 = frame.bonePos[i];
				
				if (nextFrame != null) {
					Quaternion q2 = nextFrame.boneRot[i];
					Quaternion.slerp(interpolant, q1, q2, alpha);
					
					Vector3F v2 = nextFrame.bonePos[i];
					Vector3F.lerp(v1, v2, alpha, tmp);
				}
				
				bone.initRotInverse.multiply(interpolant, interpolant);
				
				bone.setRelativePos(tmp.x - bone.initPos.x, tmp.y - bone.initPos.y, tmp.z - bone.initPos.z);
				bone.setRelativeRot(interpolant);
				
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
	
	public int getCountFrames() {
		return animationFrames.size();
	}
	
	public AnimationFrame getFrame(int frame) {
		return animationFrames.get(frame);
	}
	
}
