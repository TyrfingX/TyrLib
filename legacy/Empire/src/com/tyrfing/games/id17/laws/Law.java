package com.tyrfing.games.id17.laws;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.diplomacy.actions.AcceptLawChange;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.networking.ChangeLaw;

public class Law {
	
	public static int COUNT_LAWS = 0;
	public static final int FAVOR = 20;
	
	public final int[] stats;
	public final String name;
	public final LawOption[] options;
	public final int defaultOption;
	public final int ID;
	public final String desc;
	public final boolean needsAcknowledgement;
	
	public Law(String name, String desc, int[] stats, LawOption[] options, int defaultOption, boolean needsAcknowledgement) {
		this.name = name;
		this.desc = desc;
		this.stats = stats;
		this.options = options;
		this.needsAcknowledgement = needsAcknowledgement;
		
		for (int i = 0; i < options.length; ++i) {
			options[i].law = this;
			options[i].ID = i;
		}
		
		this.defaultOption = defaultOption;
		
		ID = COUNT_LAWS++;
	}
	
	public void selectOption(int index, House house) {
		if (EmpireFrameListener.MAIN_FRAME.getNetwork() == null || EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			if (house.canPassLaws()) {
				options[house.getLawSetting(ID)].unselect(house);
				options[index].select(house);
				
				if (needsAcknowledgement) {
					for (int i = 0; i < house.getSubHouses().size(); ++i) {
						Message m = new Message(new AcceptLawChange(), house, house.getSubHouses().get(i), new int[] { ID });
						m.action.send(m.sender, m.receiver, m.options);
					}
				}
			}
		} else {
			EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new ChangeLaw(house.id, ID, index));
		}
	}
}
