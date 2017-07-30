package com.tyrfing.games.id17.gui.laws;

import com.tyrfing.games.id17.gui.MenuPoint;
import com.tyrfing.games.id17.gui.TabGUI;
import com.tyrfing.games.id17.gui.holding.OverviewGUI;
import com.tyrfing.games.id17.gui.war.ArmyBuilderGUI;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.laws.LawCategory;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.Window;
import com.tyrlib2.gui.WindowManager;

public class LawCategoryGUI extends MenuPoint {
	
	private LawCategory category;
	private final LawGUI[] lawGUIs;
	
	public static final ScaledVector2 BASE_POS = OverviewGUI.HOLDING_NAME_POS;
	
	public LawCategoryGUI(Window parent, LawCategory category) {
		this.category = category;
		
		Window bg = WindowManager.getInstance().createImageBox("LAW/" + category.name, OverviewGUI.HOLDING_INFO_POS, "MAIN_GUI", "PAPER2", ArmyBuilderGUI.FORMATION_SIZE);
		parent.addChild(bg);
		mainElements.add(bg);
		
		lawGUIs = new LawGUI[category.laws.length];
		ScaledVector2 pos = new ScaledVector2(BASE_POS);
		for (int i = 0; i < category.laws.length; ++i) {
			lawGUIs[i] = new LawGUI(category.laws[i], new ScaledVector2(pos), parent);
			mainElements.add(lawGUIs[i].label);
			for (int j = 0; j < lawGUIs[i].buttons.length; ++j) {
				mainElements.add(lawGUIs[i].buttons[j]);
			}
			pos.y += TabGUI.WINDOW_SIZE.y * 0.375f;
		}
	}
	
	public void show(House house) {
		super.show();
		
		for (int i = 0; i < category.laws.length; ++i) {
			lawGUIs[i].show(house);
		}
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void hide() {
		super.hide();
		
		for (int i = 0; i < category.laws.length; ++i) {
			lawGUIs[i].hide();
		}
	}
}
