package com.TyrLib2.PC.main;


import java.util.Vector;

import javax.media.opengl.DebugGL3;
import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import com.tyrlib2.graphics.materials.DefaultMaterial3;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.renderer.ProgramManager;
import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.main.Media;



public class PCOpenGLRenderer extends OpenGLRenderer implements GLEventListener {

	private int fb;
	private Vector<Runnable> queuedEvents = new Vector<Runnable>();
	
	public PCOpenGLRenderer() {
		super();
	}
	
	@Override
	public void init(GLAutoDrawable drawable) {
		
		drawable.setGL(new DebugGL3((GL3) drawable.getGL()));
		
		TyrGL.IMPL = new PCGL3(drawable.getGL().getGL3());
		
		defaultSetup();
		
		drawable.getGL().glEnable(GL3.GL_VERTEX_PROGRAM_POINT_SIZE);
		
		loadShaders();
		startRendering();
				
		//Generate a new FBO. It will contain your texture.
//		int[] fb = new int[1];
//		TyrGL.glGenFramebuffers(1, fb, 0);
//		TyrGL.glBindFramebuffer(TyrGL.GL_FRAMEBUFFER, fb[0]);
//
//		IntBuffer colorBufferBuffer = IntBuffer.allocate(1);
//		
//		drawable.getGL().glGenRenderbuffers(1, colorBufferBuffer);
//		
//		colorBufferBuffer.position(0);
//		int colorBuffer = colorBufferBuffer.get();
//		
//		drawable.getGL().glBindRenderbuffer(GL3.GL_RENDERBUFFER, colorBuffer);
//		drawable.getGL().glRenderbufferStorage(GL3.GL_RENDERBUFFER, GL3.GL_RGBA8, viewport.getWidth(), viewport.getHeight());
//		drawable.getGL().glFramebufferRenderbuffer(GL3.GL_FRAMEBUFFER, GL3.GL_COLOR_ATTACHMENT0, GL3.GL_RENDERBUFFER, colorBuffer);
//		drawable.getGL().glEnable(GL3.GL_MULTISAMPLE);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		synchronized (queuedEvents) {
			for (int i = 0; i < queuedEvents.size(); ++i) {
				queuedEvents.get(i).run();
			}
			queuedEvents.clear();
		}
		
		render();
	}
	
	public void queueEvent(Runnable r) {
		synchronized (queuedEvents) {
			queuedEvents.add(r);
		}
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
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
		// BLOOM
//		ProgramManager.getInstance()
//					  .createProgram("BLOOM", 
//							  		 Media.CONTEXT.getResourceID("bloom_vs", "raw"), 
//							  		 Media.CONTEXT.getResourceID("bloom_fs", "raw"), 
//									 new String[]{"a_Position", "a_TexCoordinate"});
		
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
													new String[]{"a_Position, a_Color"});
		
		// Create a material to render point sprites from texture sheets
		ProgramManager.getInstance().createProgram(	"POINT_SHEET", 
													Media.CONTEXT.getResourceID("point_sheet_vs", "raw"), 
													Media.CONTEXT.getResourceID("point_sheet_fs", "raw"), 
													new String[]{"a_Position, a_Color"});
		
		// Create a material to render point sprites from texture sheets
		ProgramManager.getInstance().createProgram(	"PARTICLE", 
													Media.CONTEXT.getResourceID("particle_vs", "raw"), 
													Media.CONTEXT.getResourceID("particle_fs", "raw"), 
													new String[]{"a_Position", "a_TexCoordinate", "a_Color"});
		
		// Create a program for rendering shadow depth maps
		ProgramManager.getInstance().createProgram(	"SHADOW_DEPTH", 
													Media.CONTEXT.getResourceID("depth_vs", "raw"), 
													Media.CONTEXT.getResourceID("depth_fs", "raw"), 
													new String[]{"aPosition" });
	}

}
