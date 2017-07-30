package com.tyrlib2.game;

import com.tyrlib2.graphics.renderer.IFrameListener;

public interface IGameState extends IFrameListener {
	public void onEnter(IGameState from);
	public void onLeave();
}
