package com.tyrfing.games.id17.ai.actions;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.tyrlib2.game.IUpdateable;

public class Executor implements IUpdateable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4395097516808185935L;
	private BlockingQueue<Execution> executions = new LinkedBlockingQueue<Execution>();
	
	public Executor() {
	}
	
	public void addExecution(Execution e) {
		if (e == null || e.action == null || e.house == null || e.options  == null) {
			throw new RuntimeException("Invalid action to be executed!");
		}
		executions.offer(e);
	}

	@Override
	public void onUpdate(float time) {
		while(!executions.isEmpty()) {
			Execution e = executions.poll();
			if (e == null || e.action == null || e.house == null || e.options  == null) {
				System.out.println("nullptr exc incoming");
			}
			if (e.action.isEnabled(e.house, e.options)) e.action.execute(e.house, e.options);
		}
	}

	@Override
	public boolean isFinished() {
		return false;
	}
}
