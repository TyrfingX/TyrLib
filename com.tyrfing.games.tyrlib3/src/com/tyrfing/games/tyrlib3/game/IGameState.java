package com.tyrfing.games.tyrlib3.game;

import com.tyrfing.games.tyrlib3.graphics.renderer.IFrameListener;

public interface IGameState extends IFrameListener {
	public void onEnter(IGameState from);
	public void onLeave();
}
