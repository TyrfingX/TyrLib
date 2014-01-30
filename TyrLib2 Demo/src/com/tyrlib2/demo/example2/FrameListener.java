package com.tyrlib2.demo.example2;

import com.tyrlib2.demo.R;
import com.tyrlib2.graphics.renderables.Entity;
import com.tyrlib2.graphics.renderer.Camera;
import com.tyrlib2.graphics.renderer.IFrameListener;
import com.tyrlib2.graphics.renderer.TextureManager;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.math.Quaternion;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.util.Color;

public class FrameListener implements IFrameListener {

	private SceneNode entNode;
	private Entity ent;
	
	@Override
	public void onSurfaceCreated() {
		/* Setup a basic scene similarly to last time so that we can actually see something */
		SceneNode camNode = SceneManager.getInstance().getRootSceneNode().createChild(new Vector3(0,100,100));
		Camera camera = SceneManager.getInstance().createCamera(new Vector3(0,-1,-1), new Vector3(0,0,1), camNode);
		camera.use();
		
		SceneManager.getInstance().setAmbientLight(new Color(0.8f,0.8f,0.8f,1));
		
		/* Entity objects are higher level renderables consisting of subentities (smaller renderables)
		 * They also support higher level operations such as animation. Generally, when not working
		 * with primitive objects, you will want to use entities.
		 * All entities need to be put into the "assets" folder.
		 */
		
		TextureManager.getInstance().createTexture("knight", R.drawable.knight);
		ent = SceneManager.getInstance().createEntity("entities/knight.iqe");
		
		entNode = SceneManager.getInstance().getRootSceneNode().createChild(new Vector3());
		entNode.attachSceneObject(ent);
		
		/* Play the animation */
		ent.playAnimation("Walk");

	}

	@Override
	public void onSurfaceChanged() {

	}

	@Override
	public void onFrameRendered(float time) {
		entNode.rotate(new Quaternion(Quaternion.fromAxisAngle(new Vector3(0,0,1), time*30)));
		
		/* In order to update the animation the entity requires time updates */
		ent.onUpdate(time);
	}

}
