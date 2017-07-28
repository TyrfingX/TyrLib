package com.tyrfing.games.tyrlib3.pc.main;

import java.util.Vector;

import com.tyrfing.games.tyrlib3.graphics.renderer.GameLoop;

public class ConsoleRenderer extends GameLoop {
	private Vector<Runnable> queuedEvents = new Vector<Runnable>();
	
	public ConsoleRenderer(boolean serverMode) {
		super(serverMode);
	}

	@Override
	public void queueEvent(Runnable r) {
		synchronized (queuedEvents) {
			queuedEvents.add(r);
		}
	}
	
	@Override
	public void startRendering() {
		System.out.println("Starting Game Loop");
		super.startRendering();
	}
	
	@Override
	public void render() {
		
		try {
			Thread.sleep(30);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		try {
			synchronized (queuedEvents) {
				for (int i = 0; i < queuedEvents.size(); ++i) {
					queuedEvents.get(i).run();
				}
				queuedEvents.clear();
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorHandler.onError();
		}
		
		super.render();
	}
}
