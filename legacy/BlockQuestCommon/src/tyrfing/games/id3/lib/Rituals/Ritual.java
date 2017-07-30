package tyrfing.games.id3.lib.Rituals;

import tyrfing.games.id3.lib.mechanics.State;

public interface Ritual {
	
	public String getName();
	public String getDescription();
	public int getMoneyCost();
	public int getHpCost();
	public RitualType getType();
	public void acquire(State state);
}
