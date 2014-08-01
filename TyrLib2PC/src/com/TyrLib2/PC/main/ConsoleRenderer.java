package com.TyrLib2.PC.main;

import java.util.Vector;

import com.tyrlib2.graphics.renderer.GameLoop;

public class ConsoleRenderer extends GameLoop {
	private int ticksPerSecond = 10;
	
	private Vector<Runnable> queuedEvents = new Vector<Runnable>();
	
	public ConsoleRenderer(boolean serverMode) {
		super(serverMode);
	}
	
	public void setTicksPerSecond(int ticksPerSecond) {
		this.ticksPerSecond = ticksPerSecond;
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
