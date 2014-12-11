package com.tyrlib2.graphics.renderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.tyrlib2.graphics.materials.DefaultMaterial3;
import com.tyrlib2.main.Media;
import com.tyrlib2.main.OpenGLSurfaceView;

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
		
	//	GLES20.glEnable(GLES20.GL_BLEND);
		//GLES20.glEnable(GLES20.GL_ALPHA_BITS);
		//GLES20.glSampleCoverage(4, false);
		
		defaultSetup();
		loadShaders();
		startRendering();

	}
	
	public void onDrawFrame(GL10 unused) {
		render();
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    public void onSurfaceChanged(GL10 unused, int width, int height) {
    	surfaceChanged(width, height);
    }

	@Override
	public void loadShaders() {
		// Create a default program for 2D primitives
		ProgramManager.getInstance().createProgram(	"BASIC", 
													Media.CONTEXT.getResourceID("basic_color_vs", "raw"), 
													Media.CONTEXT.getResourceID("basic_color_fs", "raw"), 
													new String[]{"a_Position", "a_Color"});
		
		// Create a default program for textured 2D primitives
		ProgramManager.getInstance().createProgram(	"TEXTURED", 
													Media.CONTEXT.getResourceID("textured_vs", "raw"), 
													Media.CONTEXT.getResourceID("textured_fs", "raw"), 
													new String[]{"a_Position", "a_TexCoordinate"});
		
		// Default program for 3D objects
		ProgramManager.getInstance()
					  .createProgram(DefaultMaterial3.PER_PIXEL_PROGRAM_NAME, 
							  		 Media.CONTEXT.getResourceID("textured_ppl_vs", "raw"), 
							  		 Media.CONTEXT.getResourceID("textured_ppl_fs", "raw"), 
									 new String[]{"a_Position", "a_Normal", "a_Color", "a_TexCoordinate", "a_BoneIndex", "a_BoneWeight"});
		
		// Default program for 3D objects
		ProgramManager.getInstance()
					  .createProgram(DefaultMaterial3.PER_PIXEL_PROGRAM_NAME + "_ANIMATED", 
							  		 Media.CONTEXT.getResourceID("animated_textured_ppl_vs", "raw"), 
							  		 Media.CONTEXT.getResourceID("animated_textured_ppl_fs", "raw"), 
									 new String[]{"a_Position", "a_Normal", "a_Color", "a_TexCoordinate", "a_BoneIndex", "a_BoneWeight"});
		
		// Create a material for point lights
		ProgramManager.getInstance().createProgram(	"POINT_LIGHT", 
													Media.CONTEXT.getResourceID("point_light_vs", "raw"), 
													Media.CONTEXT.getResourceID("point_light_fs", "raw"), 
													new String[]{"a_Position"});
		
		// Create a material for rendering terrain
		ProgramManager.getInstance().createProgram(	"TERRAIN", 
													Media.CONTEXT.getResourceID("terrain_vs", "raw"), 
													Media.CONTEXT.getResourceID("terrain_fs", "raw"), 
													new String[]{"a_Position", "a_Normal", "a_TexCoordinate", "a_TexWeights"});
		
		// Create a material to render point sprites
		ProgramManager.getInstance().createProgram(	"POINT_SPRITE", 
													Media.CONTEXT.getResourceID("point_sprite_vs", "raw"), 
													Media.CONTEXT.getResourceID("point_sprite_fs", "raw"), 
													new String[]{"a_Position, a_Color", "a_Size"});
		
		// Create a material to render point sprites from texture sheets
		ProgramManager.getInstance().createProgram(	"POINT_SHEET", 
													Media.CONTEXT.getResourceID("point_sheet_vs", "raw"), 
													Media.CONTEXT.getResourceID("point_sheet_fs", "raw"), 
													new String[]{"a_Position, a_Color", "a_Size"});
		
		// Create a material to render point sprites from texture sheets
		ProgramManager.getInstance().createProgram(	"PARTICLE", 
													Media.CONTEXT.getResourceID("particle_vs", "raw"), 
													Media.CONTEXT.getResourceID("particle_fs", "raw"), 
													new String[]{"a_Position", "a_TexCoordinate", "a_Color"});
	}

	@Override
	public void queueEvent(Runnable r) {
		OpenGLSurfaceView.instance.queueEvent(r);
	}
}
