package com.tyrfing.tools.particle.editor;



import javax.swing.JFrame;

import com.TyrLib2.PC.config.Config;
import com.TyrLib2.PC.main.PCOpenGLActivity;
import com.jogamp.newt.opengl.GLWindow;
import com.tyrlib2.graphics.materials.DefaultMaterial3;
import com.tyrlib2.graphics.renderer.PreprocessorOptions;
import com.tyrlib2.graphics.renderer.ProgramManager;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.main.Media;



public class Viewer extends PCOpenGLActivity {
	
	private ViewerListener listener;
	
	public Viewer(JFrame frame) { super(frame); }
	
	@Override
	public void go() {
		listener = new ViewerListener();
		SceneManager.getInstance().getRenderer().addFrameListener(listener);
	}
	
	public GLWindow getWindow() {
		return this.getGLView().getWindow();
	}

	public Viewer(JFrame frame, Config config) {
		super(frame, config, "Particle Editor");
	}
	
    @Override
	public void loadShaders() {
		
		System.out.println("Loadeding shaders.");
		
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
		
		// Create a material to render point sprites from texture sheets
		ProgramManager.getInstance().createProgram(	"PARTICLE", 
													Media.CONTEXT.getResourceID("particle_vs", "raw"), 
													Media.CONTEXT.getResourceID("particle_fs", "raw"), 
													new String[]{"a_Position", "a_TexCoordinate", "a_Color"});
		

		System.out.println("Loaded all shaders.");
	}
}
