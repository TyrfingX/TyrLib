package com.tyrfing.games.id17.gui.house;

import java.util.List;

import com.tyrfing.games.id17.Action;
import com.tyrfing.games.id17.ActionCategory;
import com.tyrfing.games.id17.diplomacy.Diplomacy;
import com.tyrfing.games.id17.gui.DefaultItemListEntry;
import com.tyrfing.games.id17.gui.MenuPoint;
import com.tyrfing.games.id17.gui.holding.HoldingGUI;
import com.tyrfing.games.id17.gui.holding.OverviewGUI;
import com.tyrfing.games.id17.gui.war.ArmyBuilderGUI;
import com.tyrfing.games.id17.houses.House;
import com.tyrlib2.gui.DestroyOnEvent;
import com.tyrlib2.gui.ItemList;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.Window;
import com.tyrlib2.gui.WindowEvent.WindowEventType;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.input.InputManager;

public class ActionGUI extends MenuPoint {
	public static final ScaledVector2 CATEGORY_POS = new ScaledVector2(0.025f, 0.02f, 2);
	public static final ScaledVector2 CATEGORY_SIZE = new ScaledVector2(HoldingGUI.WINDOW_SIZE.x * 0.07f, HoldingGUI.WINDOW_SIZE.y * 0.84f, 2);
	
	private House requester;
	private House responder;
	
	private ItemList categoryItemLists;
	private ItemList[] actionItemLists;
	
	private List<ActionCategory> categories;
	
	private int currentCategory = -1;
	
	public ActionGUI(Window parent, String actionType, List<ActionCategory> categories) {
		this.categories = categories;
		
		Window window = WindowManager.getInstance().createImageBox("HOUSE/" + actionType + "/TOPICS_BG", OverviewGUI.HOLDING_INFO_POS, "MAIN_GUI", "PAPER", new ScaledVector2(ArmyBuilderGUI.FORMATION_SIZE.x / 2, ArmyBuilderGUI.FORMATION_SIZE.y, 2));
		parent.addChild(window);
		window.setReceiveTouchEvents(false);
		mainElements.add(window);
		
		window = WindowManager.getInstance().createImageBox("HOUSE/" + actionType + "/DETAILS_BG", new ScaledVector2(ArmyBuilderGUI.FORMATION_SIZE.x / 2.1f, OverviewGUI.HOLDING_INFO_POS.y, 2), "MAIN_GUI", "PAPER",  new ScaledVector2(ArmyBuilderGUI.FORMATION_SIZE.x / 1.8f, ArmyBuilderGUI.FORMATION_SIZE.y, 2));
		parent.addChild(window);
		window.setReceiveTouchEvents(false);
		mainElements.add(window);
		
		int items = (int) (3 / WindowManager.getInstance().getScale(0).y);
		categoryItemLists = (ItemList) WindowManager.getInstance().createItemList("HOUSE/" + actionType + "/CATEGORIES/MENU", CATEGORY_POS, ArmyBuilderGUI.BUILD_UNITS_SIZE, 0, items);
		parent.addChild(categoryItemLists);
		
		int countCategories = categories.size();
		actionItemLists = new ItemList[countCategories];
		
		for (int i = 0; i < countCategories; ++i) {
			ActionCategory category = categories.get(i);
			actionItemLists[i] = (ItemList) WindowManager.getInstance().createItemList("HOUSE/" + actionType + "/ACTIONS/MENU" + i, new ScaledVector2(ArmyBuilderGUI.FORMATION_SIZE.x / 2 + 0.01f, CATEGORY_POS.y, 2), ArmyBuilderGUI.BUILD_UNITS_SIZE, 0, items);
			parent.addChild(actionItemLists[i]);
			int countActions = category.getCountActions();
			for (int j = 0; j < countActions; ++j) {
				Action action = category.getAction(j);
				DefaultItemListEntry actionUI = new ActionEntry(action, this);
					
				actionItemLists[i].addItemListEntry(actionUI);
					
				actionUI.setAlpha(0);
				actionUI.setVisible(false);
				actionUI.setReceiveTouchEvents(false);
				actionUI.setEnabled(false);
			}
		}
		
		hide();
		
	}
	
	public void rebuild() {
		if (requester == null || responder == null) return;
		int countCategories = categories.size();
		
		for (int i = 0; i < countCategories; ++i) {
			ActionCategory category = categories.get(i);
			if (category.isEnabled(requester, responder)) {
				DefaultItemListEntry categoryUI = new CategoryEntry(category, this);
				categoryUI.addEventListener(WindowEventType.FADE_OUT_FINISHED, new DestroyOnEvent());
				
				categoryItemLists.addItemListEntry(categoryUI);
				
				categoryUI.setAlpha(0);
				categoryUI.setVisible(true);
				categoryUI.setReceiveTouchEvents(true);
				categoryUI.setEnabled(true);
				categoryUI.setPassTouchEventsThrough(true);
				
				if (categoryItemLists.getCountEntries() < categoryItemLists.getMaxVisibleItems()+1) {
					if (categoryItemLists.getCountEntries() != 1) {
						categoryUI.fadeIn(0.8f, 0.5f);
					} else {
						categoryUI.fadeIn(1, 0.5f);
					}
				}
				
			}
		}
		
	}
	
	public void show(House requester, House responder) {
		this.requester = requester;
		this.responder = responder;
		
		categoryItemLists.setReceiveTouchEvents(true);
		categoryItemLists.setVisible(true);
		
		if (categoryItemLists.getCountEntries() == 0) {
			rebuild();
		}
		
		int countCategories = categories.size();
		
		for (int i = 0; i < countCategories; ++i) {
			for (int j = 0; j  < actionItemLists[i].getCountEntries(); ++j) {
				((ActionEntry)actionItemLists[i].getEntry(j)).setReceiver(responder);
				((ActionEntry)actionItemLists[i].getEntry(j)).setSender(requester);
			}
		}
		
		super.show();
		
		InputManager.getInstance().sort();
	}
	
	@Override
	public void hide() {
		super.hide();
	
		clear();
		
		categoryItemLists.setReceiveTouchEvents(false);
	}
	
	public void clear() {
		for (int i = 0; i  < categoryItemLists.getCountEntries(); ++i) {
			categoryItemLists.getEntry(i).fadeOut(0, OverviewGUI.FADE_TIME);
		}
		
		hideActions();
		
		categoryItemLists.clear();
		currentCategory = -1;
	}
	
	public void hideActions() {
		
		int countCategories = categories.size();
		
		for (int i = 0; i < countCategories; ++i) {
			for (int j = 0; j  < actionItemLists[i].getCountEntries(); ++j) {
				actionItemLists[i].getEntry(j).fadeOut(0, OverviewGUI.FADE_TIME);
				((ActionEntry) actionItemLists[i].getEntry(j)).unhighlight();
				actionItemLists[i].getEntry(j).setReceiveTouchEvents(false);
			}
			actionItemLists[i].setReceiveTouchEvents(false);
			actionItemLists[i].clearRotation();
		}
		
		for (int i = 0; i  < categoryItemLists.getCountEntries(); ++i) {
			((CategoryEntry) categoryItemLists.getEntry(i)).unhighlight();
		}
		
	}
	
	public void displayActions(ActionCategory category) {
		displayActions(categories.indexOf(category));
	}
	
	public void displayActions(int index) {
		
		if (index == -1) return;
		
		currentCategory = index;
		ActionCategory category = categories.get(index);

		actionItemLists[currentCategory].setReceiveTouchEvents(true);
		actionItemLists[currentCategory].setVisible(true);
		
		int countActions = category.getCountActions();
		for (int i = 0; i < countActions; ++i) {
			ActionEntry actionUI = (ActionEntry) actionItemLists[currentCategory].getEntry(i);
			Action action = category.getAction(i);
			action.setDisabledText(requester, responder);
			Label l = (Label) WindowManager.getInstance().getWindow(actionUI.getName()+"/TooltipText");
			if (l != null) {
				l.setText(action.getDisabledText());
			}
			
			actionUI.setVisible(true);
			actionUI.setReceiveTouchEvents(true);
			actionUI.setEnabled(action.isEnabled(requester, responder));
				
			if (i < actionItemLists[currentCategory].getMaxVisibleItems()+1) {
				if (i != 1) {
					actionUI.fadeIn(0.8f, 0.5f);
				} else {
					actionUI.fadeIn(1, 0.5f);
				}
			}
		}
		
		actionItemLists[currentCategory].correctOffset();
		
		InputManager.getInstance().sort();

	}

	public void unhighlightActions() {
		int countCategories = Diplomacy.getInstance().getCountCategories();
		for (int i = 0; i < countCategories; ++i) {
			for (int j = 0; j  < actionItemLists[i].getCountEntries(); ++j) {
				if (actionItemLists[i].getEntry(j).isEnabled()) {
					((ActionEntry) actionItemLists[i].getEntry(j)).unhighlight();
				}
			}
		}
	}

	@Override
	public void update() {
		if (requester == null || responder == null) return;
		int countCategories = categories.size();
		
		boolean removedCategory = false;
		
		next: for (int i = 0; i < countCategories; ++i) {
			ActionCategory category = categories.get(i);
			if (category.isEnabled(requester, responder)) {
				
				for (int j = 0; j < categoryItemLists.getCountEntries(); ++j) {
					CategoryEntry entry = (CategoryEntry)categoryItemLists.getEntry(j);
					if (entry.category == category) {
						continue next;
					}
				}
				
				DefaultItemListEntry categoryUI = new CategoryEntry(category, this);
				categoryUI.addEventListener(WindowEventType.FADE_OUT_FINISHED, new DestroyOnEvent());
				
				categoryItemLists.addItemListEntry(categoryUI);
				
				categoryUI.setAlpha(0);
				categoryUI.setVisible(true);
				categoryUI.setReceiveTouchEvents(true);
				categoryUI.setEnabled(true);
				categoryUI.setPassTouchEventsThrough(true);
				
				if (categoryItemLists.getCountEntries() < categoryItemLists.getMaxVisibleItems()+1) {
					if (categoryItemLists.getCountEntries() != 1) {
						categoryUI.fadeIn(0.8f, 0.5f);
					} else {
						categoryUI.fadeIn(1, 0.5f);
					}
				}
				
			} else {
				for (int j = 0; j < categoryItemLists.getCountEntries(); ++j) {
					CategoryEntry entry = (CategoryEntry)categoryItemLists.getEntry(j);
					if (entry.category == category) {
						removedCategory = true;
						categoryItemLists.getEntry(j).fadeOut(0, OverviewGUI.FADE_TIME);
						categoryItemLists.removeItemListEntry(entry);
						if (currentCategory == i) {
							hideActions();
							currentCategory = -1;
						}
						break;
					}
				}
			}
		}
		
		if (removedCategory) categoryItemLists.reposition();
		
		if (currentCategory == -1) return;
		ActionCategory category = categories.get(currentCategory);
		
		int countActions = category.getCountActions();
		for (int i = 0; i < countActions; ++i) {
			ActionEntry actionUI = (ActionEntry) actionItemLists[currentCategory].getEntry(i);
			Action action = category.getAction(i);
			action.setDisabledText(requester, responder);
			Label l = (Label) WindowManager.getInstance().getWindow(actionUI.getName()+"/TooltipText");
			if (l != null) {
				l.setText(action.getDisabledText());
			}
			actionUI.setEnabled(action.isEnabled(requester, responder));
		}
		
		InputManager.getInstance().sort();
		
	}
}
