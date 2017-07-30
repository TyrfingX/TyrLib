package com.tyrfing.games.id17.gui.laws;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.gui.MainGUI;
import com.tyrfing.games.id17.gui.TabGUI;
import com.tyrfing.games.id17.gui.holding.HoldingGUI;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.laws.LawSet;
import com.tyrfing.games.id17.networking.RequestDisplayData;
import com.tyrlib2.gui.IEventListener;
import com.tyrlib2.gui.ImageBox;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.Window;
import com.tyrlib2.gui.WindowEvent;
import com.tyrlib2.gui.WindowEvent.WindowEventType;
import com.tyrlib2.gui.WindowManager;

public class LawSetGUI extends TabGUI<House> {

	private Window economyHolder;
	private Window vassalHolder;
	private Window armyHolder;
	
	private LawCategoryGUI economy;
	private LawCategoryGUI vassal;
	private LawCategoryGUI army;
	
	public int currentCategory;
	
	public final List<LawCategoryGUI> categories = new ArrayList<LawCategoryGUI>();
	
	public LawSetGUI() {
		super("LAWS");
		
		economy = new LawCategoryGUI(main, LawSet.categories[0]);
		vassal = new LawCategoryGUI(main, LawSet.categories[1]);
		army = new LawCategoryGUI(main, LawSet.categories[2]);

		categories.add(economy);
		categories.add(vassal);
		categories.add(army);
		
		subGUIs.add(economy);
		subGUIs.add(vassal);
		subGUIs.add(army);
		
		
		economyHolder = WindowManager.getInstance().createImageBox("LAW/ECONOMY_HOLDER", HoldingGUI.BUILD_HOLDER_POS, "MAIN_GUI", "SMALL_CIRCLE_WOOD", TabGUI.SIGIL_HOLDER_SIZE.get());
		
		economyHolder.addEventListener(WindowEventType.TOUCH_UP, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				showCategory(0);
			}
		});
		
		WindowManager.getInstance().createImageBox("LAW/ECONOMY_HOLDER/ICON", MainGUI.DIPLO_ICON_POS, "MAIN_GUI", "GOLD_ICON", MainGUI.DIPLO_ICON_SIZE.multiply(0.85f));
		economyHolder.addChild(WindowManager.getInstance().getWindow("LAW/ECONOMY_HOLDER/ICON"));
		
		
		vassalHolder = WindowManager.getInstance().createImageBox("LAW/VASSAL_HOLDER", HoldingGUI.BUILD_HOLDER_POS, "MAIN_GUI", "SMALL_CIRCLE_WOOD", TabGUI.SIGIL_HOLDER_SIZE.get());
		
		vassalHolder.addEventListener(WindowEventType.TOUCH_UP, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				showCategory(1);
			}
		});
		
		WindowManager.getInstance().createImageBox("LAW/VASSAL_HOLDER/ICON", new ScaledVector2(MainGUI.DIPLO_ICON_POS.x*0.7f, MainGUI.DIPLO_ICON_POS.y*0.8f), "MAIN_GUI", "VASSAL_LAWS", MainGUI.DIPLO_ICON_SIZE.multiply(1.2f));
		vassalHolder.addChild(WindowManager.getInstance().getWindow("LAW/VASSAL_HOLDER/ICON"));
		
		
		armyHolder = WindowManager.getInstance().createImageBox("LAW/ARMY_HOLDER", HoldingGUI.BUILD_HOLDER_POS, "MAIN_GUI", "SMALL_CIRCLE_WOOD", TabGUI.SIGIL_HOLDER_SIZE.get());
		
		armyHolder.addEventListener(WindowEventType.TOUCH_UP, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				showCategory(2);
			}
		});
		
		WindowManager.getInstance().createImageBox("LAW/ARMY_HOLDER/ICON", new ScaledVector2(MainGUI.DIPLO_ICON_POS.x*0.8f, MainGUI.DIPLO_ICON_POS.y*0.8f), "MAIN_GUI", "ARMY_ICON", MainGUI.DIPLO_ICON_SIZE.multiply(1.0f));
		armyHolder.addChild(WindowManager.getInstance().getWindow("LAW/ARMY_HOLDER/ICON"));
		
		options.add(null);
		options.add((ImageBox)economyHolder);
		WindowManager.getInstance().addTextTooltip(economyHolder, "Taxes");
		options.add((ImageBox)vassalHolder);
		WindowManager.getInstance().addTextTooltip(vassalHolder, "Vassals");
		options.add((ImageBox)armyHolder);
		WindowManager.getInstance().addTextTooltip(armyHolder, "Recruitment");
	}
	
	@Override
	public void display() {
		hideAllLaws();
		resetHighlights();
		currentCategory = 0;
		economy.show(displayed);
		
		options.get(1).setAtlasRegion("SMALL_CIRCLE_WOOD_ACTIVE");
		options.get(1).setReceiveTouchEvents(true);
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isClient()) {
			EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new RequestDisplayData(RequestDisplayData.LAW_STATS, 
																						 (short) displayed.id));
		}
		
	}
	
	public void hideAllLaws() {
		for (int i = 0; i < subGUIs.size(); ++i) {
			subGUIs.get(i).hide();
		}
	}
	
	public void showCategory(int index) {
		if (displayed != null) {
			hideAllLaws();
			categories.get(index).show(displayed);
			resetHighlights();
			
			options.get(index+1).setAtlasRegion("SMALL_CIRCLE_WOOD_ACTIVE");
			options.get(index+1).setReceiveTouchEvents(true);
			
			currentCategory = index;
		}
	}
	
	private void resetHighlights() {
		for (int i = 1; i < options.size(); ++i) {
			if (options.get(i) != null) {
				options.get(i).setAtlasRegion("SMALL_CIRCLE_WOOD");
				options.get(i).setReceiveTouchEvents(true);
			}
		}
	}

	public House getDisplayed() {
		return displayed;
	}
	
	@Override
	public void update() {
		if (this.isVisible()) {
			showCategory(currentCategory);
		}
	}

	@Override
	public boolean isFinished() {
		// TODO Auto-generated method stub
		return false;
	}
}
