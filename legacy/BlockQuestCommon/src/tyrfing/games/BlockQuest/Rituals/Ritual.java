package tyrfing.games.BlockQuest.Rituals;

import tyrfing.games.BlockQuest.mechanics.State;

public interface Ritual {
	
	public String getName();
	public String getDescription();
	public int getMoneyCost();
	public int getHpCost();
	public RitualType getType();
	public void acquire(State state);
}
