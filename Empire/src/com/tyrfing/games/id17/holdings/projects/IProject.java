package com.tyrfing.games.id17.holdings.projects;

import java.io.Serializable;

import com.tyrlib2.game.IUpdateable;


public interface IProject extends IUpdateable, Serializable {
	
	public float getProgress();
	public void abort();
	public void finish();
	public String getIconAtlasName();
	public String getIconRegionName();
	public void setProgress(float progress);
}
