package com.tyrfing.games.tyrlib3.model.game;

import com.tyrfing.games.tyrlib3.view.graphics.IFrameListener;

public interface IGameState extends IFrameListener {
	public void onEnter(IGameState from);
	public void onLeave();
}
