package com.tyrlib2.demo.example4;

import com.tyrlib2.game.Updater;
import com.tyrlib2.graphics.particles.ParticleSystem;
import com.tyrlib2.graphics.particles.XMLParticleSystemFactory;
import com.tyrlib2.graphics.renderer.Camera;
import com.tyrlib2.graphics.renderer.IFrameListener;
import com.tyrlib2.graphics.renderer.TextureManager;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.math.Vector3;

public class FrameListener implements IFrameListener {
	
	@Override
	public void onSurfaceCreated() {
		/* Setup a basic scene */
		SceneNode camNode = SceneManager.getInstance().getRootSceneNode().createChild(new Vector3(0,35,35));
		Camera camera = SceneManager.getInstance().createCamera(new Vector3(0,-1,-1), new Vector3(0,-1,1), camNode);
		camera.use();
		
		/* The particle system we will create requires the texture DUST */
		TextureManager.getInstance().createTexture("DUST", ExampleFourActivity.CONTEXT,com.tyrlib2.R.drawable.dust);
		
		/* create a an appropriate factory for creating a particle system from an XML file */
		XMLParticleSystemFactory particleSystemFactory = new XMLParticleSystemFactory("particle/particle1.xml", ExampleFourActivity.CONTEXT);
		ParticleSystem system = particleSystemFactory.create();
		
		
		/* Add the particle system to the scene and give it time input */
		SceneNode node = SceneManager.getInstance().getRootSceneNode().createChild();
		system.attachTo(node);
		
		Updater updater = new Updater();
		updater.addItem(system);
		
		
		SceneManager.getInstance().addFrameListener(updater);
		SceneManager.getInstance().getRenderer().addRenderable(system);
	}

	@Override
	public void onSurfaceChanged() {

	}

	@Override
	public void onFrameRendered(float time) {

	}

}
