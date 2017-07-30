package com.tyrfing.games.pc.main;
import javax.swing.JFrame;

import com.TyrLib2.PC.config.Config;
import com.TyrLib2.PC.main.PCOpenGLActivity;
import com.tyrfing.games.id17.EmpireActivity;
import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.geometry.GrassMaterial;
import com.tyrlib2.graphics.materials.DefaultMaterial3;
import com.tyrlib2.graphics.materials.OutlineMaterial;
import com.tyrlib2.graphics.renderer.PreprocessorOptions;
import com.tyrlib2.graphics.renderer.ProgramManager;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.main.Media;
import com.tyrlib2.util.Options;



public class Game extends PCOpenGLActivity {
	
	private EmpireFrameListener listener;
	
	public Game(JFrame frame) { super(frame); }
	
	@Override
	public void go() {
		
		Options options = Media.CONTEXT.getOptions();
		
		options.setOption(EmpireActivity.BLOOM_LEVELS, 2);
		options.setOption(EmpireActivity.BLOOM_DOWNSCALE, 2);
		
		EmpireFrameListener.BUILD_TARGET = EmpireFrameListener.PC_TARGET;
		listener = new EmpireFrameListener(new float[] { 1,1 });
		SceneManager.getInstance().getRenderer().addFrameListener(listener);
		
	}
	
	public Game(JFrame frame, int port) {
		super(frame, true, null, "Sword & Scroll");
		System.out.println("Starting server on port: " +  port + "...");
	}

	public Game(JFrame frame, Config config) {
		super(frame, config, "Sword & Scroll");
	}

	@Override
	public void loadShaders() {
		PreprocessorOptions prepOptions = new PreprocessorOptions();
		if (EmpireFrameListener.BUILD_TARGET != EmpireFrameListener.ANDROID_TARGET) {
			//prepOptions.define("BUMP");
			prepOptions.define("SHADOW");
			//prepOptions.define("ORENNAYAR");
		}
		
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
									 new String[]{"a_Position", "a_Normal", "a_Color", "a_TexCoordinate" },
									 prepOptions);
		
		// Default program for animated 3D objects
		ProgramManager.getInstance()
					  .createProgram(DefaultMaterial3.PER_PIXEL_PROGRAM_NAME + "_ANIMATED", 
							  		 Media.CONTEXT.getResourceID("animated_textured_ppl_vs", "raw"), 
							  		 Media.CONTEXT.getResourceID("animated_textured_ppl_fs", "raw"), 
									 new String[]{"a_Position", "a_Normal", "a_Color", "a_TexCoordinate", "a_BoneIndex", "a_BoneWeight"},
									 prepOptions);
		ProgramManager.getInstance()
					  .createProgram(GrassMaterial.PROGRAM_NAME, 
									 Media.CONTEXT.getResourceID("grass_vs", "raw"), 
									 Media.CONTEXT.getResourceID("grass_fs", "raw"), 
									 new String[]{"a_Position", "a_TexCoordinate"});
	
		ProgramManager.getInstance().createProgram(	OutlineMaterial.OUTLINE_PROGRAM, 
													Media.CONTEXT.getResourceID("outline_vs", "raw"), 
													Media.CONTEXT.getResourceID("outline_fs", "raw"), 
													new String[]{"a_Position", "a_Normal"});
		
		ProgramManager.getInstance().createProgram(	"ANIMATED_" + OutlineMaterial.OUTLINE_PROGRAM, 
													Media.CONTEXT.getResourceID("animated_outline_vs", "raw"), 
													Media.CONTEXT.getResourceID("outline_fs", "raw"), 
													new String[]{"a_Position", "a_Normal", "a_BoneIndex", "a_BoneWeight"});

		
		// Create a material to render point sprites from texture sheets
		ProgramManager.getInstance().createProgram(	"PARTICLE", 
													Media.CONTEXT.getResourceID("particle_vs", "raw"), 
													Media.CONTEXT.getResourceID("particle_fs", "raw"), 
													new String[]{"a_Position", "a_TexCoordinate", "a_Color"});
		
		ProgramManager.getInstance().createProgram(  "SEASON_PROGRAM", 
											  		 Media.CONTEXT.getResourceID("season_vs", "raw"), 
											  		 Media.CONTEXT.getResourceID("season_fs", "raw"), 
													 new String[]{"a_Position", "a_Diffuse", "a_TexCoordinate"},
													 prepOptions);
		
		PreprocessorOptions grassOptions = prepOptions.copy();
		//grassOptions.define("GRASS");
		//grassOptions.define("SHADOW");
		//grassOptions.define("ORENNAYAR");
		ProgramManager.getInstance().createProgram(  
			"SEASON_PROGRAM_GRASS", 
		  	 Media.CONTEXT.getResourceID("season_vs", "raw"), 
		  	 Media.CONTEXT.getResourceID("season_fs", "raw"), 
			 new String[]{"a_Position", "a_Diffuse", "a_TexCoordinate"},
			 grassOptions
		);
		
		ProgramManager.getInstance().createProgram(	"TERRAIN_FOG", 
													Media.CONTEXT.getResourceID("fog_vs", "raw"), 
													Media.CONTEXT.getResourceID("fog_fs", "raw"), 
													new String[]{"a_Position"});
		
		ProgramManager.getInstance().createProgram(	"TERRAIN_STRATEGIC", 
				Media.CONTEXT.getResourceID("strategic_view_vs", "raw"), 
				Media.CONTEXT.getResourceID("strategic_view_fs", "raw"), 
				new String[]{"a_Position"});
		
		ProgramManager.getInstance().createProgram(  "WATER_PROGRAM", 
											  		 Media.CONTEXT.getResourceID("water_vs", "raw"), 
											  		 Media.CONTEXT.getResourceID("water_fs", "raw"), 
													 new String[]{"a_Position", "a_Diffuse", "a_TexCoordinate"},
													 prepOptions);

		ProgramManager.getInstance().createProgram(  "OCEAN_PROGRAM", 
											  		 Media.CONTEXT.getResourceID("ocean_vs", "raw"), 
											  		 Media.CONTEXT.getResourceID("ocean_fs", "raw"), 
													 new String[]{"a_Position", "a_TexCoordinate"});
		
		ProgramManager.getInstance().createProgram(  "TECH", 
											  		 Media.CONTEXT.getResourceID("tech_vs", "raw"), 
											  		 Media.CONTEXT.getResourceID("tech_fs", "raw"), 
											  		new String[]{"a_Position", "a_TexCoordinate"});
		
		// Required for post processing
		ProgramManager.getInstance().createProgram(
			"BLIT", 
			 Media.CONTEXT.getResourceID("postprocessing_vs", "raw"), 
		  	 Media.CONTEXT.getResourceID("blit_fs", "raw"), 
			 new String[]{"a_Position"}
		);
		
		prepOptions = new PreprocessorOptions();
		prepOptions.define("X");
		
		ProgramManager.getInstance().createProgram(
			"GAUSS_X", 
			 Media.CONTEXT.getResourceID("postprocessing_vs", "raw"), 
		  	 Media.CONTEXT.getResourceID("gaussian_fs", "raw"), 
			 new String[]{"a_Position"}, prepOptions
		);
		
		prepOptions = new PreprocessorOptions();
		prepOptions.define("Y");
		
		ProgramManager.getInstance().createProgram(
			"GAUSS_Y", 
			 Media.CONTEXT.getResourceID("postprocessing_vs", "raw"), 
		  	 Media.CONTEXT.getResourceID("gaussian_fs", "raw"), 
			 new String[]{"a_Position"}, prepOptions
		);
	}
}
