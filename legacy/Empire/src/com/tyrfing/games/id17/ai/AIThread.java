package com.tyrfing.games.id17.ai;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Vector;

import com.tyrfing.games.id17.ai.actions.Execution;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.intrigue.IntrigueProject;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.game.GameThread;

public class AIThread extends GameThread {
	private static AIThread thread;
	private Vector<AIController> ais = new Vector<AIController>(); 
	
	public static final int DELAY = 2000;
	
	private boolean running;
	
	public static void create() {
		thread = new AIThread();
	}
	
	public static AIThread getInstance() {
		return thread;
	}
	
	@Override
	public void run() {
		
		running = true;
		
		while(running) {
		
			//ObjectiveModel.printGenerationCount();
			
			try {
				Thread.sleep(DELAY);
			} catch (InterruptedException e) {
			}
			
			try {
				for (int i = 0; i < ais.size(); ++i) {
					ais.get(i).update();
				}
			} catch (Exception e) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				String s = sw.toString();
				System.out.println("AITHREAD " + s);
			}
		
		}
	}
	
	public void addAI(AIController controller) {
		ais.add(controller);
	}
	
	public void removeAI(AIController controller) {
		ais.remove(controller);
	}
	
	public void addMessage(Message message) {
		World.getInstance().messageResponses.addMessage(message);
	}
	
	public void addExecution(Execution execution) {
		World.getInstance().executor.addExecution(execution);
	}
	
	public void addIntrigue(IntrigueProject project) {
		World.getInstance().starter.addProject(project);
	}

	public void close() {
		running = false;
	}
}
