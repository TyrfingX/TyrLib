package com.tyrlib2.graphics.renderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.tyrlib2.graphics.lighting.Light;
import com.tyrlib2.graphics.materials.DefaultMaterial3;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.math.FrustumG;
import com.tyrlib2.math.Matrix;

/**
 * This class initiates the actual rendering.
 * @author Sascha
 *
 */

public class AndroidOpenGLRenderer extends OpenGLRenderer implements GLSurfaceView.Renderer {
	
	public AndroidOpenGLRenderer() {
		super();
	}
	
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		
        // Enable depth testing
        GLES20.glDepthFunc( GLES20.GL_LEQUAL );
        GLES20.glDepthMask( true );
        
		// Use culling to remove back faces.
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glCullFace(GLES20.GL_BACK);
		
		// Set the blend function
		
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);

		ProgramManager.getInstance().recreateAll();
		TextureManager.getInstance().reloadAll();
		SceneManager.getInstance().recreateFonts();
		
        // Setup the SceneManager
        SceneManager.getInstance().setRenderer(this);
		
		// Create a default program for 2D primitives
		ProgramManager.getInstance().createProgram(	"BASIC", 
													com.tyrlib2.R.raw.basic_color_vs, 
													com.tyrlib2.R.raw.basic_color_fs, 
													new String[]{"a_Position", "a_Color"});
		
		// Create a default program for textured 2D primitives
		ProgramManager.getInstance().createProgram(	"TEXTURED", 
													com.tyrlib2.R.raw.textured_vs, 
													com.tyrlib2.R.raw.textured_fs, 
													new String[]{"a_Position", "a_TexCoordinate"});
		
		// Default program for 3D objects
		ProgramManager.getInstance()
					  .createProgram(DefaultMaterial3.PER_PIXEL_PROGRAM_NAME, 
									 com.tyrlib2.R.raw.textured_ppl_vs, 
									 com.tyrlib2.R.raw.textured_ppl_fs, 
									 new String[]{"a_Position", "a_Normal", "a_Color", "a_TexCoordinate", "a_BoneIndex", "a_BoneWeight"});
		
		// Default program for 3D objects
		ProgramManager.getInstance()
					  .createProgram(DefaultMaterial3.PER_PIXEL_PROGRAM_NAME + "_ANIMATED", 
									 com.tyrlib2.R.raw.animated_textured_ppl_vs, 
									 com.tyrlib2.R.raw.animated_textured_ppl_fs, 
									 new String[]{"a_Position", "a_Normal", "a_Color", "a_TexCoordinate", "a_BoneIndex", "a_BoneWeight"});
		
		// Create a material for point lights
		ProgramManager.getInstance().createProgram(	"POINT_LIGHT", 
													com.tyrlib2.R.raw.point_light_vs, 
													com.tyrlib2.R.raw.point_light_fs, 
													new String[]{"a_Position"});
		
		// Create a material for rendering terrain
		ProgramManager.getInstance().createProgram(	"TERRAIN", 
													com.tyrlib2.R.raw.terrain_vs, 
													com.tyrlib2.R.raw.terrain_fs, 
													new String[]{"a_Position", "a_Normal", "a_TexCoordinate", "a_TexWeights"});
		
		// Create a material to render point sprites
		ProgramManager.getInstance().createProgram(	"POINT_SPRITE", 
													com.tyrlib2.R.raw.point_sprite_vs, 
													com.tyrlib2.R.raw.point_sprite_fs, 
													new String[]{"a_Position, a_Color"});
		
		// Create a material to render point sprites from texture sheets
		ProgramManager.getInstance().createProgram(	"POINT_SHEET", 
													com.tyrlib2.R.raw.point_sheet_vs, 
													com.tyrlib2.R.raw.point_sheet_fs, 
													new String[]{"a_Position, a_Color"});
		
		// Create a program for rendering shadow depth maps
		ProgramManager.getInstance().createProgram(	"SHADOW_DEPTH", 
													com.tyrlib2.R.raw.depth_vs, 
													com.tyrlib2.R.raw.depth_fs, 
													new String[]{"aPosition" });

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        viewport = new Viewport();
        
        lastTime = 0;
        
        rendering = true;
        init = true;
        
        for (int i = 0; i < frameListeners.size(); ++i) {
           	frameListeners.get(i).onSurfaceCreated();
        }
	}
	
	public void onDrawFrame(GL10 unused) {
    	
    	if (rendering) {
    	
	        // Redraw background color
	    	GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		    
		    rootSceneNode.update();
		    
		    // Update the view matrix of the camera
		    camera.update();
		    
		    if (frustum == null) {
			    frustum = new FrustumG();
		    }
		    
		    frustum.update(	camera.getAbsolutePos(), 
							camera.getWorldLookDirection(), 
							camera.getWorldUpVector(), 
							viewport.getNearClip(), 
							viewport.getFarClip(),
							viewport.getNearClipWidth(),
							viewport.getNearClipHeight());
		    
		    // Update the eye space matrices of all lights
		    SceneManager sceneManager = SceneManager.getInstance();
		    for (int i = 0; i < sceneManager.getLightCount(); ++i) {
		    	Light light = sceneManager.getLight(i);
		    	light.update(camera.viewMatrix);
		    }

		    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
			GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		    
			GLES20.glViewport(-(int)(viewport.getWidth()*0.2f), -(int)(viewport.getHeight()*0.2f), (int)(viewport.getWidth()*1.4f), (int)(viewport.getHeight()*1.4f));
		    
		    Matrix.multiplyMM(vpMatrix, 0, viewport.projectionMatrix, 0, camera.viewMatrix, 0);
		    
		    drawScene();
		    updateListeners();
		    
		    textureFails = 0;

    	}
        
    }
    
    public void onSurfaceChanged(GL10 unused, int width, int height) {
       viewport.setFullscreen(width, height);
       
       for (int i = 0; i < frameListeners.size(); ++i) {
       	frameListeners.get(i).onSurfaceChanged();
       }
       
		if (init) {
			ProgramManager.getInstance().recreateAll();
			TextureManager.getInstance().reloadAll();
			SceneManager.getInstance().recreateFonts();
		}
    }
}
