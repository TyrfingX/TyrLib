package com.tyrfing.games.id17.gui.war;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.EmpireFrameListener.GameState;
import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.gui.DefaultItemListEntry;
import com.tyrfing.games.id17.gui.MenuPoint;
import com.tyrfing.games.id17.gui.holding.HoldingGUI;
import com.tyrfing.games.id17.gui.holding.OverviewGUI;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.holdings.projects.UpgradeRegimentProject;
import com.tyrfing.games.id17.networking.LevyAction;
import com.tyrfing.games.id17.networking.UpgradeRegimentMessage;
import com.tyrfing.games.id17.war.Army;
import com.tyrfing.games.id17.war.Regiment;
import com.tyrfing.games.id17.war.UnitType;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.gui.IEventListener;
import com.tyrlib2.gui.ImageBox;
import com.tyrlib2.gui.ItemList;
import com.tyrlib2.gui.ItemListEntry;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.Window;
import com.tyrlib2.gui.WindowEvent;
import com.tyrlib2.gui.WindowEvent.WindowEventType;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Color;
import com.tyrlib2.util.Direction4;

public class ArmyBuilderGUI extends MenuPoint {

	public static final ScaledVector2 FORMATION_SIZE = new ScaledVector2( HoldingGUI.WINDOW_SIZE.x * 0.99f, HoldingGUI.WINDOW_SIZE.y * 0.99f, 2);
	public static final ScaledVector2 FORMATION_POS = ArmyGUI.FORMATION_POS;

	public static final ScaledVector2 BUILD_UNITS_POS = new ScaledVector2(HoldingGUI.WINDOW_SIZE.x * 0.275f + 0.005f, 0.02f , 2);
	public static final ScaledVector2 BUILD_UNITS_SIZE = new ScaledVector2(HoldingGUI.WINDOW_SIZE.x * 0.45f, HoldingGUI.WINDOW_SIZE.y * 0.84f, 2);

	public static final ScaledVector2 ARMY_COUNT_POS = new ScaledVector2(ArmyGUI.INFO_POS.x + OverviewGUI.HOLDING_NAME_POS.x, OverviewGUI.HOLDING_NAME_POS.y + 0.04f, 2);
	public static final ScaledVector2 ARMY_POS = new ScaledVector2(FORMATION_SIZE.x*0.725f, OverviewGUI.HOLDING_INFO_POS.y, 2);

	private Barony barony;
	private FormationWindow formationWindowLevy;
	private FormationWindow formationWindowGarrison;
	private Window parent;
	private Label nameLabel;
	private Label nameLabelGarrison;

	public static final ScaledVector2 UPGRADE_REGIMENT_POS = new ScaledVector2(BUILD_UNITS_POS.x, BUILD_UNITS_POS.y * 0.25f + BuildUnitGUI.SIZE_Y.x, 2);
	public static final ScaledVector2 DESTROY_REGIMENT_POS = new ScaledVector2(BUILD_UNITS_POS.x, BuildUnitGUI.SIZE_Y.x + UPGRADE_REGIMENT_POS.y, 2);

	private ImageBox upgradeRegiment;
	private Label upgradeCost;

	private int selected = -1;

	private ItemList unitMenu;

	private boolean upgradeClick;

	private Army selectedArmy = null;

	public ArmyBuilderGUI(Window parent) {
		this.parent = parent;

		WindowManager.getInstance().createImageBox("BUILD_ARMY/FORMATION", OverviewGUI.HOLDING_INFO_POS, "MAIN_GUI", "PAPER2", FORMATION_SIZE);
		parent.addChild(WindowManager.getInstance().getWindow("BUILD_ARMY/FORMATION"));
		mainElements.add(WindowManager.getInstance().getWindow("BUILD_ARMY/FORMATION"));
		mainElements.get(0).setAlpha(0);
		mainElements.get(0).setRecuresiveReceiveTouchEvents(true);

		upgradeRegiment = (ImageBox) WindowManager.getInstance().createImageBox("BUILD_ARMY/UPGRADE", UPGRADE_REGIMENT_POS.get(), "MAIN_GUI", "PAPER2", new Vector2(BuildUnitGUI.SIZE_X.get(), BuildUnitGUI.SIZE_Y.get()));
		parent.addChild(upgradeRegiment);
		WindowManager.getInstance().addTextTooltip(
			upgradeRegiment, 
			""
		);

		upgradeRegiment.addEventListener(WindowEventType.TOUCH_LEAVES, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				if (upgradeClick) {
					upgradeRegiment.setAtlasRegion( "PAPER2" );
					upgradeClick = false;
				}
			}
		});

		upgradeRegiment.addEventListener(WindowEventType.TOUCH_DOWN, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				if (!barony.hasActiveProject()) {
					upgradeClick = true;
					upgradeRegiment.setAtlasRegion("PAPER2_ACTIVE");
				}
			}
		});

		upgradeRegiment.addEventListener(WindowEventType.TOUCH_UP, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				if (upgradeClick) {
					upgradeClick = false;
					upgradeRegiment();
				}
			}
		});

		upgradeRegiment.addEventListener(WindowEventType.TOUCH_MOVES, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				if (upgradeClick) {
					upgradeClick = false;
					upgradeRegiment.setAtlasRegion( "PAPER2" );
				}
			}
		});

		Label caption = (Label) WindowManager.getInstance().createLabel("BUILD_ARMY/UPGRADE_LABEL", BuildUnitGUI.NAME_LABEL, "+100");
		upgradeRegiment.addChild(caption);
		caption.setColor(Color.BLACK.copy());
		caption.setInheritsAlpha(true);

		upgradeCost = (Label) WindowManager.getInstance().createLabel("BUILD_ARMY/UPGRADE_COST_LABEL", new Vector2(BuildUnitGUI.COST_LABEL_X.get(), BuildUnitGUI.COST_LABEL_Y.get()), "DEFAULT");
		upgradeRegiment.addChild(upgradeCost);
		upgradeCost.setColor(Color.BLACK.copy());
		upgradeCost.setInheritsAlpha(true);

		WindowManager.getInstance().createImageBox("BUILD_ARMY/UPGRADE_MONEY_ICON", new Vector2(BuildUnitGUI.COST_ICON_POS_X.get(), BuildUnitGUI.COST_ICON_POS_Y.get()), "MAIN_GUI", "GOLD_ICON", BuildUnitGUI.COST_ICON_SIZE);
		upgradeRegiment.addChild(WindowManager.getInstance().getWindow("BUILD_ARMY/UPGRADE_MONEY_ICON"));
		WindowManager.getInstance().getWindow("BUILD_ARMY/UPGRADE_MONEY_ICON").setInheritsAlpha(true);

		upgradeRegiment.setAlpha(0);

		nameLabel = (Label) WindowManager.getInstance().createLabel("BUILD_ARMY/FORMATION_NAME", OverviewGUI.HOLDING_NAME_POS.get(2), "DEFAULT");
		parent.addChild(nameLabel);
		nameLabel.setColor(Color.BLACK.copy());

		nameLabelGarrison = (Label) WindowManager.getInstance().createLabel("BUILD_ARMY/FORMATION_NAME_GARRISON", new ScaledVector2(FORMATION_SIZE.x*0.76f, OverviewGUI.HOLDING_NAME_POS.y, 2), "DEFAULT");
		parent.addChild(nameLabelGarrison);
		nameLabelGarrison.setColor(Color.BLACK.copy());

		formationWindowLevy = new FormationWindow(FORMATION_POS.get(), parent, parent.getName() + "/Levy", Direction4.LEFT, FormationWindow.ICON_SIZE_BIG.get());
		formationWindowGarrison = new FormationWindow(BattleGUI.DEFENDER_FORMATION_POS, parent, parent.getName() + "/Garrison", Direction4.RIGHT, FormationWindow.ICON_SIZE_BIG.get());

		setupFormationWindow(formationWindowLevy);
		setupFormationWindow(formationWindowGarrison);

		int items = (int) (3 / WindowManager.getInstance().getScale(0).y);
		unitMenu = (ItemList) WindowManager.getInstance().createItemList("BUILD_ARMY/MENU", BUILD_UNITS_POS, BUILD_UNITS_SIZE, 0, items);
		parent.addChild(unitMenu);

		for (UnitType type : UnitType.values()) {
			if (type != UnitType.Walls) {
				ItemListEntry buildUnit = new BuildUnitGUI(type.name(), this);
				unitMenu.addItemListEntry(buildUnit);
			}
		}

		hideUnitBuildMenu();

		nameLabel.setAlpha(0);
		nameLabelGarrison.setAlpha(0);
		upgradeRegiment.setAlpha(0);
		upgradeRegiment.setReceiveTouchEvents(false);
	}

	public void setupFormationWindow(final FormationWindow formationWindow) {
		for (int i = 0; i < Army.MAX_REGIMENTS; ++i) {
			formationWindow.getIcon(i).setAlpha(0);
		}

		for (int i = 0; i < Army.MAX_REGIMENTS; ++i) {
			Window icon = formationWindow.getIcon(i);
			icon.setData("INDEX", i);
			icon.addEventListener(WindowEventType.TOUCH_UP, new IEventListener() {
				@Override
				public void onEvent(WindowEvent event) {
					Window source = event.getSource();
					Integer index = (Integer) source.getData("INDEX");
					selectRegiment(index, formationWindow.getDisplayed(), formationWindow);
				}
			});
		}
	}

	public void show(Barony barony) {
		
		if (this.barony != barony) {

			this.barony = barony;
			
			super.show();
			
			update();
			
			for (int i = 0; i < Army.MAX_REGIMENTS; ++i) {
				formationWindowLevy.getIcon(i).setReceiveTouchEvents(true);
				formationWindowGarrison.getIcon(i).setReceiveTouchEvents(true);
			}
	
			if (barony.getOwner() != World.getInstance().getPlayerController().getHouse() || EmpireFrameListener.state == GameState.SELECT) {
				parent.setRecuresiveReceiveTouchEvents(false);
			}
		
		}
	}

	public void hide() {

		for (int i = 0; i < Army.MAX_REGIMENTS; ++i) {
			formationWindowLevy.getIcon(i).setReceiveTouchEvents(false);
			formationWindowGarrison.getIcon(i).setReceiveTouchEvents(false);
		}

		formationWindowLevy.fadeOut(OverviewGUI.FADE_TIME);
		formationWindowGarrison.fadeOut(OverviewGUI.FADE_TIME);

		super.hide();

		hideUnitBuildMenu();

		nameLabel.fadeOut(0, OverviewGUI.FADE_TIME);
		nameLabelGarrison.fadeOut(0, OverviewGUI.FADE_TIME);
		upgradeRegiment.fadeOut(0, OverviewGUI.FADE_TIME);

		selected = -1;
		
		this.barony = null;

	}

	public void showUnitBuildMenu(Army army, FormationWindow formationWindow) {

		for (int i = 0; i < unitMenu.getCountEntries(); ++i) {
			((BuildUnitGUI)unitMenu.getEntry(i)).setInfo(army, barony, formationWindow);
		}

		unitMenu.setReceiveTouchEvents(true);
		unitMenu.setVisible(true);

		for (int i = 0; i < unitMenu.getCountEntries(); ++i) {
			unitMenu.getEntry(i).setVisible(true);
			unitMenu.getEntry(i).setReceiveTouchEvents(true);
			BuildUnitGUI entry = ((BuildUnitGUI) unitMenu.getEntry(i));
			entry.setPos(selected);
			entry.setEnabled(!barony.hasActiveProject() && barony.getOwner().isUnitEnabled(UnitType.valueOf(entry.name)));
			if (i < unitMenu.getMaxVisibleItems()) {
				if (i != 1) {
					unitMenu.getEntry(i).fadeIn(0.8f, 0.5f);
				} else {
					unitMenu.getEntry(i).fadeIn(1, 0.5f);
				}
			}
		}
		
		upgradeRegiment.fadeOut(0, OverviewGUI.FADE_TIME);
		upgradeRegiment.setReceiveTouchEvents(false);
	}

	public void hideUnitBuildMenu() {
		unitMenu.correctOffset();
		unitMenu.setReceiveTouchEvents(false);
		for (int i = 0; i < unitMenu.getCountEntries(); ++i) {
			unitMenu.getEntry(i).fadeOut(0, 0.5f);
			unitMenu.getEntry(i).setReceiveTouchEvents(false);
		}

		if (barony != null) {
			nameLabel.setText("Levy: " + barony.getLevy().getTotalTroops());
		}
		
		upgradeRegiment.fadeOut(0, OverviewGUI.FADE_TIME);
		upgradeRegiment.setReceiveTouchEvents(false);

		if (selected != -1) {
			formationWindowLevy.setAttacking(selected, false);
			formationWindowGarrison.setAttacking(selected, false);
			selected = -1;
		}
	}

	private void selectRegiment(int index, Army army, FormationWindow formationWindow) {

		if (selected == -1) {
			selected = index;
			selectedArmy = army;
			formationWindow.setAttacking(index, true);
			if (army.getRegiment(index) == null) {
				showUnitBuildMenu(army, formationWindow);
			} else {
				Regiment r = army.getRegiment(index);
				if (formationWindow == formationWindowLevy) {
					nameLabel.setText(r.unitType.name() + ": " +  (int)army.getRegiment(index).troops);
				} else {
					nameLabelGarrison.setText(r.unitType.name() + ": " +  (int)army.getRegiment(index).troops);
				}
				int cost = (int) UnitType.getPrice(r.unitType, (int)(r.maxTroops / 100));
				upgradeCost.setText(Integer.toString(cost));
				upgradeRegiment.fadeIn(1, OverviewGUI.FADE_TIME);
				
				Label tooltip = (Label) WindowManager.getInstance().getWindow(upgradeRegiment.getName() + "/TooltipText");
				
				tooltip.setText( 
					"- Increases regiment capacity by " + Util.getFlaggedText("+100", true) + "\n" + 
					"- Build Time: " + Util.getFlaggedText(String.valueOf((int)(UnitType.getProd(r.unitType, 0)/army.getHome().getHoldingData().prod)) + "d", false)
				);

				if (barony.hasActiveProject()) {
					upgradeRegiment.setReceiveTouchEvents(false);
					upgradeRegiment.setAtlasRegion("PAPER2_DISABLED");
				} else{
					upgradeRegiment.setReceiveTouchEvents(true);
					upgradeRegiment.setAtlasRegion("PAPER2");
				}
			}
		} else {
			if (selectedArmy == barony.getLevy()) {
				formationWindowLevy.setAttacking(selected, false);
			} else if (selectedArmy == barony.getGarrison()) {
				formationWindowGarrison.setAttacking(selected, false);
			}

			formationWindow.setAttacking(index, false);
			if (!army.isRaised() || (army.getCurrentHolding() == barony && !army.isFighting() && !army.isTravelling())) {
				if (selectedArmy == army) {
					if (formationWindow != formationWindowGarrison || (selected > 1 && index > 1)) {
						if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
							army.switchRegiments(selected, index);
						}

						short param = (short) (selected | (index << 8));
						EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new LevyAction(	army.id, 
																								LevyAction.CHANGE_FORMATION, 
																								param));
					} 


				} else if ((formationWindow == formationWindowGarrison && index > 1) || (formationWindow != formationWindowGarrison && selected > 1)) {
					if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
						if (selectedArmy == barony.getLevy()) {
							army.switchRegiments(index, barony.getLevy(), selected);
						} else if (selectedArmy == barony.getGarrison()) {
							army.switchRegiments(index, barony.getGarrison(), selected);
						}
					} 
					
					short param = (short) (selected | (index << 8));
					EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new LevyAction(	selectedArmy.id, 
																							LevyAction.TRANSFER, 
																							param));
				} 
			}
			
			selected = -1;
			hideUnitBuildMenu();
			formationWindowLevy.setArmy(barony.getLevy(), true);
			formationWindowGarrison.setArmy(barony.getGarrison(), true);
		}



	}	

	public void upgradeRegiment() {
		upgradeRegiment.setAtlasRegion("PAPER2");
		Regiment r = selectedArmy.getRegiment(selected);
		int cost = (int) UnitType.getPrice(r.unitType, (int) (r.maxTroops / 100));
		int prod = (int) UnitType.getProd(r.unitType, (int) (r.maxTroops / 100));
	
		hideUnitBuildMenu();
	
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			barony.startProject(new UpgradeRegimentProject(prod, r, selectedArmy, cost));
		} else {
			EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new UpgradeRegimentMessage(barony.getHoldingID(), r.formationPos, r.unitType.ordinal(), selectedArmy == barony.getLevy() ));
		}
	}

	@Override
	public void update() {
		formationWindowGarrison.setArmy(barony.getGarrison(), true);
		formationWindowLevy.setArmy(barony.getLevy(), true);
		nameLabel.setText("Levy: " + barony.getLevy().getTotalTroops());
		nameLabel.fadeIn(1, OverviewGUI.FADE_TIME);

		nameLabelGarrison.setText("Garrison: " + barony.getGarrison().getTotalTroops());
		nameLabelGarrison.fadeIn(1, OverviewGUI.FADE_TIME);
		
		for (int i = 0; i < unitMenu.getCountEntries(); ++i) {
			((BuildUnitGUI)unitMenu.getEntry(i)).update();
		}
	}

	public void unHighlightUnitTypes() {
		for (int i = 0; i  < unitMenu.getCountEntries(); ++i) {
			if (((DefaultItemListEntry)unitMenu.getEntry(i)).isEnabled()) {
				((DefaultItemListEntry)unitMenu.getEntry(i)).highlight();
				((DefaultItemListEntry)unitMenu.getEntry(i)).unhighlight();
			}
		}
	}
}
