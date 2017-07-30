package com.tyrfing.games.id17.world;

import com.tyrlib2.graphics.renderer.Program;
import com.tyrlib2.graphics.renderer.ProgramManager;
import com.tyrlib2.graphics.renderer.UParam1f;
import com.tyrlib2.util.Valuef;


public class TileMaterial {
	public final SeasonMaterial mat;
	public final float[] uv;
	public final boolean hasSides;
	public final int baseTile;
	
	public TileMaterial(WorldChunk worldChunk, String textureWin, String texture, float[] uv, boolean water, int id) {
		Program program = !water 	? ProgramManager.getInstance().getProgram("SEASON_PROGRAM") 
									: ProgramManager.getInstance().getProgram("WATER_PROGRAM");
		this.mat = new SeasonMaterial(program, worldChunk, textureWin, texture, 1, 1, null, water);
		this.mat.addParam(new UParam1f("u_Terrain", new Valuef(1.0f)));
		this.uv = uv;
		this.hasSides = true;
		this.baseTile = id;
	}
	
	public TileMaterial(WorldChunk worldChunk, String textureWin, String texture, float[] uv, int grassHeight, int grassLayers, int baseTile) {
		Program program = ProgramManager.getInstance().getProgram("SEASON_PROGRAM_GRASS");
		this.mat = new SeasonMaterial(program, worldChunk, textureWin, texture, 1, 1, null, false);
		this.mat.setGrassSettings(0.02f, 3);
		this.mat.addParam(new UParam1f("u_Terrain", new Valuef(1.0f)));
		this.hasSides = false;
		this.mat.setCastShadow(false);
		this.uv = uv;
		this.baseTile = baseTile;
	}
}