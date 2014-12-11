package com.TyrLib2.PC.main;


import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import com.tyrlib2.graphics.materials.DefaultMaterial3;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.renderer.PreprocessorOptions;
import com.tyrlib2.graphics.renderer.ProgramManager;
import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.main.Media;



public class PCOpenGLRenderer extends OpenGLRenderer implements GLEventListener {

	private Vector<Runnable> queuedEvents = new Vector<Runnable>();
	private List<Runnable> events = new ArrayList<Runnable>();

	//private int[] fbo = new int[1];
	//private int[] rbo = new int[2];
	
	public PCOpenGLRenderer() {
		super();
	}
	
	@Override
	public void init(GLAutoDrawable drawable) {
		
		//drawable.setGL(new DebugGL3((GL3) drawable.getGL()));
		
		GL3 gl = drawable.getGL().getGL3();
		TyrGL.IMPL = new PCGL3(gl);
		
		defaultSetup();
		
		drawable.getGL().glEnable(GL3.GL_VERTEX_PROGRAM_POINT_SIZE);
		
		loadShaders();
		/*
		gl.glEnable(GL3.GL_MULTISAMPLE);
		
		gl.glGenFramebuffers( 1, fbo, 0 );
		gl.glBindFramebuffer( GL3.GL_FRAMEBUFFER, fbo[0] );
		
		gl.glGenRenderbuffers( 2, rbo, 0 );
		gl.glBindRenderbuffer(GL3.GL_RENDERBUFFER, rbo[0]);
		gl.glRenderbufferStorageMultisample(GL3.GL_RENDERBUFFER, 4, GL3.GL_RGB8, drawable.getWidth(), drawable.getHeight());
		gl.glBindRenderbuffer(GL3.GL_RENDERBUFFER, rbo[1]);
		gl.glRenderbufferStorageMultisample(GL3.GL_RENDERBUFFER, 4, GL3.GL_DEPTH_COMPONENT24, drawable.getWidth(), drawable.getHeight());
		*/
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
		System.out.println("Disposing of OpenGL Context");
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		try {
			synchronized (queuedEvents) {
				events.addAll(queuedEvents);
				queuedEvents.clear();
			}
			
			for (int i = 0; i < events.size(); ++i) {
				events.get(i).run();
			}
			events.clear();
			
		} catch (Exception e) {
			e.printStackTrace();
			errorHandler.onError();
		}
		
		GL3 gl = drawable.getGL().getGL3();
		/*
		gl.glBindFramebuffer( GL3.GL_FRAMEBUFFER, fbo[0] );
		gl.glFramebufferRenderbuffer(GL3.GL_FRAMEBUFFER, GL3.GL_COLOR_ATTACHMENT0, GL3.GL_RENDERBUFFER, rbo[0]);
		gl.glFramebufferRenderbuffer(GL3.GL_FRAMEBUFFER, GL3.GL_DEPTH_ATTACHMENT, GL3.GL_RENDERBUFFER, rbo[1]);
		*/
		
		render();
		/*
		gl.glBindFramebuffer(GL3.GL_READ_FRAMEBUFFER, fbo[0]); // Unseren Framebuffer also Quelle binden
		gl.glBindFramebuffer(GL3.GL_DRAW_FRAMEBUFFER, 0); // Den OpenGL Framebuffer als Ziel binden
		gl.glBlitFramebuffer(0, 0, drawable.getWidth(), drawable.getHeight(), 0, 0, 
							 drawable.getWidth(), drawable.getHeight(), GL3.GL_COLOR_BUFFER_BIT, GL3.GL_NEAREST);
		*/
	}
	
	@Override
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
		
		PreprocessorOptions prepOptions = new PreprocessorOptions();
		prepOptions.define("BUMP");
		prepOptions.define("SHADOW");
		
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
									 new String[]{"a_Position", "a_Normal", "a_Color", "a_TexCoordinate", "a_BoneIndex", "a_BoneWeight"},
									 prepOptions);
		
		// Default program for 3D objects
		ProgramManager.getInstance()
					  .createProgram(DefaultMaterial3.PER_PIXEL_PROGRAM_NAME + "_ANIMATED", 
							  		 Media.CONTEXT.getResourceID("animated_textured_ppl_vs", "raw"), 
							  		 Media.CONTEXT.getResourceID("animated_textured_ppl_fs", "raw"), 
									 new String[]{"a_Position", "a_Normal", "a_Color", "a_TexCoordinate", "a_BoneIndex", "a_BoneWeight"},
									 prepOptions);
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

	}

}
