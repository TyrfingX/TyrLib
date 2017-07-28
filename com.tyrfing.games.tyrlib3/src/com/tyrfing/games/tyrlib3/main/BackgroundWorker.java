package com.tyrfing.games.tyrlib3.main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class BackgroundWorker {
	private static BackgroundWorker instance;
	
	private ExecutorService executor = Executors.newCachedThreadPool();
	private List<Future<?>> tasks = new ArrayList<Future<?>>();
	
	
	public static BackgroundWorker getInstance() {
		if (instance == null) {
			instance = new BackgroundWorker();
		}
		
		return instance;
	}
	
	public Future<?> execute(Runnable r) {
		Future<?> f = executor.submit(r);
		tasks.add(f);
		return f;
	}
	
	public void waitForTasks() {
		try {
			executor.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public boolean allTasksCompleted() {
		for (Future<?> f : tasks) {
			if (!f.isDone() && !f.isCancelled()) {
				return false;
			}
		}
		
		tasks.clear();
		return true;
	}
}
