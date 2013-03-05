package com.tyrlib2.demo.example3;

import android.view.MotionEvent;

import com.tyrlib2.game.Updater;
import com.tyrlib2.graphics.lighting.DirectionalLight;
import com.tyrlib2.graphics.lighting.Light.Type;
import com.tyrlib2.graphics.materials.TerrainMaterial;
import com.tyrlib2.graphics.renderer.Camera;
import com.tyrlib2.graphics.renderer.IFrameListener;
import com.tyrlib2.graphics.renderer.TextureManager;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.graphics.terrain.Terrain;
import com.tyrlib2.graphics.terrain.TerrainTexture;
import com.tyrlib2.input.Controller;
import com.tyrlib2.input.InputManager;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.util.Color;

public class FrameListener implements IFrameListener {

	
	@Override
	public void onSurfaceCreated() {
		/* Setup a basic scene */
		final SceneNode camNode = SceneManager.getInstance().getRootSceneNode().createChild(new Vector3(0,35,35));
		final Camera camera = SceneManager.getInstance().createCamera(new Vector3(0,-1,-1), new Vector3(0,-1,1), camNode);
		camera.use();
		
		SceneManager.getInstance().setAmbientLight(new Color(0.4f,0.4f,0.4f,1));
		
		DirectionalLight light = (DirectionalLight) SceneManager.getInstance().createLight(Type.DIRECTIONAL_LIGHT);
		light.setLightDirection(new Vector3(0,-1,-1));
		light.setIntensity(1);
		SceneManager.getInstance().getRootSceneNode().createChild(new Vector3(0,0,0)).attachSceneObject(light);
		
		/*
		 * Next we need to load the necessary resources
		 */
		
		TextureManager.getInstance().createTexture("SNOW", ExampleThreeActivity.CONTEXT,com.tyrlib2.R.drawable.snow);
		TextureManager.getInstance().createTexture("ROCK", ExampleThreeActivity.CONTEXT,com.tyrlib2.R.drawable.rocky);
		TextureManager.getInstance().createTexture("GRASS", ExampleThreeActivity.CONTEXT,com.tyrlib2.R.drawable.grass);
		TextureManager.getInstance().createTexture("SOIL", ExampleThreeActivity.CONTEXT,com.tyrlib2.R.drawable.soil);
		TextureManager.getInstance().createTexture("MAP", ExampleThreeActivity.CONTEXT,com.tyrlib2.R.drawable.heightmap);
		
		/*
		 * Next we create some terrain textures.
		 * Terrain textures decide how important a texture is for any vertex 
		 * within the terrain. This decision can for example based on slope & height.
		 */
		
		TerrainTexture terrTex = new TerrainTexture("GRASS");
		terrTex.setHeightWeight(0.1f, 0.6f, 0.6f);
		terrTex.setSlopeWeight(0, 0.015f, 0.15f);
		
		TerrainTexture terrTex2 = new TerrainTexture("SOIL");
		terrTex2.setHeightWeight(0, 1, 0);
		terrTex2.setSlopeWeight(0.15f, 0.13f, 0.5f);
		
		TerrainTexture terrTex3 = new TerrainTexture("SNOW");
		terrTex3.setHeightWeight(1, 0.3f, 10);
		terrTex3.setSlopeWeight(0, 0.1f, 0);
		
		TerrainTexture terrTex4 = new TerrainTexture("ROCK");
		terrTex4.setHeightWeight(0.9f, 0.3f, 2);
		terrTex4.setSlopeWeight(0.15f, 0.12f, 1);
		
		/*
		 * Here we create a terrain material which is used for rendering terrain.
		 */
		
		TerrainMaterial terrMat = new TerrainMaterial(new Vector2(0.1f, 0.1f));
		terrMat.setTexture(terrTex, 0);
		terrMat.setTexture(terrTex2, 1);
		terrMat.setTexture(terrTex3, 2);
		terrMat.setTexture(terrTex4, 3);
	 
		// This is where we actually create our terrain
		Terrain terrain = Terrain.fromHeightmap("MAP", terrMat, new Vector2(50,50), 25);
		SceneManager.getInstance().getRootSceneNode().createChild(new Vector3()).attachSceneObject(terrain);
		SceneManager.getInstance().getRenderer().addRenderable(terrain);
	
		
		// We want to see the entire terrain, so we create a controller for scrolling the camera
		
		Controller controller = new Controller() {
			
			Vector2 lastPoint = null;
			
			@Override
			public boolean onTouchMove(Vector2 point, MotionEvent event) {
				Vector2 movement = lastPoint.vectorTo(point).multiply(30);
				movement.y *= -1;
				
				lastPoint = new Vector2(point);

				camNode.translate(new Vector3(movement.x, movement.y, 0));

				return true;
			}
			
			@Override
			public boolean onTouchDown(Vector2 point, MotionEvent event) {
				lastPoint = new Vector2(point);
				return true;
			}

			@Override
			public void onUpdate(float time) {	}
			@Override
			public boolean isFinished() { return false; }
			
			
		};
		
		// Add our controller to some updater
		Updater updater = new Updater();
		SceneManager.getInstance().addFrameListener(updater);
		updater.addItem(controller);
		InputManager.getInstance().addTouchListener(controller);

	}

	@Override
	public void onSurfaceChanged() {

	}

	@Override
	public void onFrameRendered(float time) {
		
	}

}
