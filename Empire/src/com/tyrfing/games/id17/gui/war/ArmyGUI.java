package com.tyrfing.games.id17.gui.war;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.gui.MainGUI;
import com.tyrfing.games.id17.gui.TabGUI;
import com.tyrfing.games.id17.gui.holding.HoldingGUI;
import com.tyrfing.games.id17.gui.holding.OverviewGUI;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.networking.LevyAction;
import com.tyrfing.games.id17.networking.RequestDisplayData;
import com.tyrfing.games.id17.war.Army;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.graphics.renderables.FormattedText2.ALIGNMENT;
import com.tyrlib2.gui.IEventListener;
import com.tyrlib2.gui.ImageBox;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.ProgressBar;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.Window;
import com.tyrlib2.gui.WindowEvent;
import com.tyrlib2.gui.WindowEvent.WindowEventType;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Direction4;

public class ArmyGUI extends TabGUI<Army> {

	private ImageBox pillage;
	private ImageBox unraise;
	private ImageBox cancel;

	private BattleGUI battleGUI;
	
	private FormationWindow formationWindow;

	public static final String[] ARMY_GUI_TAB_NAMES = new String[] { "SIGIL"  };
	public static final String[] ARMY_GUI_TAB_ATLAS_NAMES = new String[] { "SIGILS1"  };
	public static final String[] ARMY_GUI_TAB_REGION_NAMES = new String[] { "Rebels" };
	public static final String[] ARMY_GUI_TAB_TOOLTIPS = new String[] { "Owner"};


	public static final ScaledVector2 FORMATION_SIZE = new ScaledVector2(0.9f * TabGUI.WINDOW_SIZE.x / 2, TabGUI.WINDOW_SIZE.y * 0.99f, 2);
	public static final ScaledVector2 FORMATION_POS = new ScaledVector2(OverviewGUI.HOLDING_INFO_POS.x + FORMATION_SIZE.x * 0.3f, OverviewGUI.HOLDING_NAME_POS.y * 2.25f,2 );

	public static final ScaledVector2 INFO_SIZE = new ScaledVector2(1.11f * TabGUI.WINDOW_SIZE.x / 2, TabGUI.WINDOW_SIZE.y * 0.99f,2);
	public static final ScaledVector2 INFO_POS = new ScaledVector2(OverviewGUI.HOLDING_INFO_POS.x + FORMATION_SIZE.x - 0.02f, OverviewGUI.HOLDING_INFO_POS.y,2);
	public static final ScaledVector2 ARMY_NAME_POS = new ScaledVector2(INFO_POS.x + OverviewGUI.HOLDING_NAME_POS.x, OverviewGUI.HOLDING_NAME_POS.y,2);

	private Window subHeader;
	private Window formationHeader;
	private Window nameHeader;

	private Label stateLabel;
	private Label armyMaint;
	private Label armyReinf;
	private Label armyName;
	private Label armyTroops;
	
	private ProgressBar armyMoral;
	private Label armyMoralRegen;
	
	public static final Vector2 PILLAGE_POS = HoldingGUI.BUILD_HOLDER_POS;
	public static final Vector2 UNRAISE_POS = HoldingGUI.ARMY_HOLDER_POS;
	public static final Vector2 CANCEL_POS = HoldingGUI.RAISE_ARMY_HOLDER_POS;

	private List<ImageBox> disable = new ArrayList<ImageBox>();
	private boolean enablePillage;

	public ArmyGUI() {
		super("ARMY",3, ARMY_GUI_TAB_NAMES, ARMY_GUI_TAB_ATLAS_NAMES, ARMY_GUI_TAB_REGION_NAMES, ARMY_GUI_TAB_TOOLTIPS);

		cancel = (ImageBox) WindowManager.getInstance().createImageBox("ARMY/CANCEL", CANCEL_POS, "MAIN_GUI", "CANCEL", TabGUI.SIGIL_SIZE.multiply(0.95f).get());
		WindowManager.getInstance().getWindow("ARMY/CANCEL").setReceiveTouchEvents(false);
		options.add(cancel);

		WindowManager.getInstance().createImageBox("ARMY/CANCEL_HOLDER", new Vector2(-TabGUI.SIGIL_HOLDER_SIZE.x * 0.025f, -TabGUI.SIGIL_HOLDER_SIZE.x * 0.025f), "MAIN_GUI", "SMALL_CIRCLE_BORDER", TabGUI.SIGIL_HOLDER_SIZE.get());
		cancel.addChild(WindowManager.getInstance().getWindow("ARMY/CANCEL_HOLDER"));
		WindowManager.getInstance().getWindow("ARMY/CANCEL_HOLDER").setReceiveTouchEvents(true);

		WindowManager.getInstance().getWindow("ARMY/CANCEL_HOLDER").addEventListener(WindowEvent.WindowEventType.TOUCH_UP, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				if (displayed != null) {
					displayed.deselect();
				}
				hide();
			}
		});
		
		pillage = (ImageBox) WindowManager.getInstance().createImageBox("ARMY/PILLAGE_HOLDER", PILLAGE_POS, "MAIN_GUI", "SMALL_CIRCLE_WOOD", TabGUI.SIGIL_HOLDER_SIZE.get());
		pillage.setReceiveTouchEvents(true);
		WindowManager.getInstance().createImageBox("ARMY/PILLAGE_HOLDER/ICON", new Vector2(), "MAIN_GUI", "PILLAGE_ICON", TabGUI.SIGIL_HOLDER_SIZE.get());
		pillage.addChild(WindowManager.getInstance().getWindow("ARMY/PILLAGE_HOLDER/ICON"));
		options.add(pillage);
		disable.add(pillage);

		WindowManager.getInstance().addTextTooltip(pillage, " Pillage ");

		pillage.addEventListener(WindowEventType.TOUCH_UP, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				if (enablePillage) {
					if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
						displayed.pillage();
					} else {
						EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new LevyAction(	displayed.id, 
								LevyAction.PILLAGE,
								(short) 0));	
					}
				}
			}
		});

		unraise = (ImageBox) WindowManager.getInstance().createImageBox("ARMY/UNRAISE_HOLDER", UNRAISE_POS, "MAIN_GUI", "SMALL_CIRCLE_WOOD", TabGUI.SIGIL_HOLDER_SIZE.get());
		unraise.setReceiveTouchEvents(true);
		WindowManager.getInstance().createImageBox("ARMY/UNRAISE/ICON", new Vector2(), "MAIN_GUI", "RAISE_ARMY_ICON", TabGUI.SIGIL_HOLDER_SIZE.get());
		unraise.addChild(WindowManager.getInstance().getWindow("ARMY/UNRAISE/ICON"));
		options.add(unraise);
		disable.add(unraise);
		unraise.addTextTooltip("Unraise");

		unraise.addEventListener(WindowEventType.TOUCH_UP, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				if (displayed.canUnraise()) {
					if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
						displayed.unraise();
					} else {
						EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new LevyAction(	displayed.id, 
								LevyAction.UNRAISE,
								(short) 0));
					}



					if (displayed != null) {
						displayed.deselect();
					}

					hide();
				}
			}
		});
		
		stateLabel = (Label) WindowManager.getInstance().createLabel("ARMY/STATE_LABEL", new Vector2(header.getSize().x*2f/3, LEFT_LABEL_POS.get().y*4), "Idle");
		stateLabel.setAlignment(ALIGNMENT.CENTER);
		header.addChild(stateLabel);

		WindowManager.getInstance().getWindow(ARMY_GUI_TAB_NAMES[0]).addEventListener(WindowEventType.TOUCH_DOWN, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				MainGUI gui = World.getInstance().getMainGUI();
				if (displayed != null) {
					gui.houseGUI.show(displayed.getOwner());
					gui.pickerGUI.hideAll();
				}
			}
		});

		subHeader = WindowManager.getInstance().createRectWindow(	"ARMY_GUI_SUB_HEADER", 
				new Vector2(0, header.getSize().y+TAB_PADDING.get().x*4), 
				header.getSize(), GRAY_PAINT);
		header.addChild(subHeader);

		armyMaint = (Label) WindowManager.getInstance().createLabel("ARMY/ARMY_MAINT", LEFT_LABEL_POS, "<img MAIN_GUI GOLD_ICON> -100");
		subHeader.addChild(armyMaint);
		
		armyReinf = (Label) WindowManager.getInstance().createLabel("ARMY/ARMY_REINF", new Vector2(header.getSize().x+RIGHT_LABEL_POS.get().x*1.25f, RIGHT_LABEL_POS.get().y), "<img MAIN_GUI RAISE_ARMY_ICON> 10");
		subHeader.addChild(armyReinf);
		
		armyReinf.setReceiveTouchEvents(true);
		WindowManager.getInstance().addTextTooltip(armyReinf, "");

		armyMoral = (ProgressBar) WindowManager.getInstance().createProgressBar(
				"ARMY/MORAL", 
				new Vector2(
						LEFT_LABEL_POS.get().x + TAB_PADDING.get().x*2 , 
						LEFT_LABEL_POS.get().y + header.getSize().y/2), 
				new Vector2(
						header.getSize().x - LEFT_LABEL_POS.get().x*2 -TAB_PADDING.get().x*8, 
						header.getSize().y/2 - LEFT_LABEL_POS.get().y*2 -TAB_PADDING.get().x*6), 
				1);
		armyMoral.setProgress(0.5f);
		armyMoral.setReceiveTouchEvents(true);
		WindowManager.getInstance().addTextTooltip(armyMoral, "");
		subHeader.addChild(armyMoral);
		
		armyMoralRegen = (Label) WindowManager.getInstance().createLabel("ARMY/ARMY_MORAL_REGEN", new Vector2(header.getSize().x/2, RIGHT_LABEL_POS.get().y+header.getSize().y/2), "+50%");
		armyMoralRegen.setAlignment(ALIGNMENT.CENTER);
		subHeader.addChild(armyMoralRegen);
		
		
		
		formationHeader = WindowManager.getInstance().createRectWindow(	"ARMY_GUI_FORMATION_HEADER", 
				new Vector2(headerSize.x+TAB_PADDING.get().x*1, 0), 
				new Vector2(header.getSize().x, header.getSize().y*2+TAB_PADDING.get().x*4), GRAY_PAINT);
		header.addChild(formationHeader);

		formationWindow = new FormationWindow(new Vector2(formationHeader.getSize().x*0.5f, header.getSize().y/3), formationHeader, formationHeader.getName() + "/ArmyGUI", Direction4.LEFT, FormationWindow.ICON_SIZE_BIG.get());
		
		nameHeader = WindowManager.getInstance().createRectWindow(	"ARMY_GUI_NAME_HEADER", 
				new Vector2(0, formationHeader.getSize().y+TAB_PADDING.get().x*1), 
				new Vector2(header.getSize().x*2+TAB_PADDING.get().x*1, header.getSize().y/2), GRAY_PAINT);
		header.addChild(nameHeader);

		armyName = (Label) WindowManager.getInstance().createLabel("ARMY/ARMY_NAME", LEFT_LABEL_POS, "DEFAULT_NAME");
		nameHeader.addChild(armyName);
		
		armyTroops = (Label) WindowManager.getInstance().createLabel("ARMY/ARMY_TROOPS", new Vector2(LEFT_LABEL_POS.get().x+nameHeader.getSize().x*3/4, LEFT_LABEL_POS.get().y), "100/100");
		armyTroops.setAlignment(ALIGNMENT.CENTER);
		nameHeader.addChild(armyTroops);
		
		battleGUI = new BattleGUI(main);
		
		hide();

	}


	@Override
	public boolean isFinished() {
		return false;
	}
	
	@Override
	public void hide() {
		super.hide();
		if (main.isVisible()) {
			battleGUI.hide();
		}
	}
	
	@Override
	public void show(Army army) {
		if (army.isFighting()) {
			battleGUI.show(army.getBattle());
		}
		super.show(army);
	}

	@Override
	public void display() {
		
		ImageBox sigil = (ImageBox) WindowManager.getInstance().getWindow(ARMY_GUI_TAB_NAMES[0]+"/Image");
		sigil.setAtlasRegion(displayed.getOwner().getSigilName());
		
		armyName.setText(displayed.toString());
		formationWindow.setArmy(displayed, true);

		if (displayed.isFighting()) {
			battleGUI.displayBattle();
		} else {
			main.setVisible(false);
			main.moveTo(new Vector2(WINDOW_POS.x, SIGIL_HOLDER_SIZE.y), DISPLAY_TIME);
		}
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isClient()) {
			EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new RequestDisplayData(RequestDisplayData.ARMY_STATS, 
																						 displayed.id));
		}
		
		update();
	}

	@Override
	public void update() {
		if (displayed != null && displayed.isRaised()) {
			
			for (int i = 0; i < disable.size(); ++i) {
				disable.get(i).setAtlasRegion("SMALL_CIRCLE_WOOD_DISABLED");
				disable.get(i).setReceiveTouchEvents(true);
			}
			
			if (displayed.getOwner().getController() == World.getInstance().getPlayerController()) {

				if (!displayed.getOwner().isMarauder()) {
					unraise.getTooltipLabel().setText(Util.getFlaggedText("Unraise\n(1)", displayed.getCurrentHolding() == displayed.getHome()) + " Army is at home barony.");
					if (displayed.getCurrentHolding() == displayed.getHome()) {
						unraise.setAtlasRegion("SMALL_CIRCLE_WOOD");
						unraise.setReceiveTouchEvents(true);
					} else {
						unraise.setAtlasRegion("SMALL_CIRCLE_WOOD_DISABLED");
						unraise.setReceiveTouchEvents(false);
					}
				} else {
					float remainingRaisedTime = Army.MARAUDERS_RAISED_MIN - displayed.raisedTime;
					float targetWorldTime = World.getInstance().getWorldTime() + remainingRaisedTime;
					unraise.getTooltipLabel().setText("Unraise\n" + Util.getFlaggedText("(1)", displayed.raisedTime >= Army.MARAUDERS_RAISED_MIN) + " Raised until at least " +  World.toDate(targetWorldTime));
					unraise.setAtlasRegion("SMALL_CIRCLE_WOOD");
					unraise.setReceiveTouchEvents(true);
				}

				enablePillage = false;
				
				if (displayed.getCurrentHolding().isPillageableByArmy(displayed)) {
					pillage.setAtlasRegion("SMALL_CIRCLE_WOOD");
					enablePillage = true;
				} 
				
				Label tooltip = (Label) pillage.getTooltipLabel();
				if (tooltip != null) {
					
					boolean castle = 		!(displayed.getCurrentHolding() instanceof Barony) 
										|| 	displayed.getCurrentHolding().holdingData.barony.getOccupee() == displayed.getOwner();
					
					boolean owner = 	(displayed.getCurrentHolding().getOwner().isEnemy(displayed.getOwner()) != null 
									|| 	displayed.getCurrentHolding().getOwner().isSubjectOf(displayed.getOwner()));
					
					String pillageText = 
						"Pillage\n" +
						Util.getFlaggedText("(1)", !displayed.getCurrentHolding().isPillaged()) + " Not pillaged\n" + 
						Util.getFlaggedText("(2)", castle) + " Non-Castle holding or occupied by us\n" +
						Util.getFlaggedText("(3)", owner) + " Belongs to us, a vassal or an enemy" + "\n\n";
						
					if (!displayed.getOwner().isMarauder()) {
						pillageText += "Gain " + Util.getFlaggedText("-1", false) + "<img MAIN_GUI HONOR_ICON> and ";
					}
					
					pillageText +=  Util.getFlaggedText("+" + displayed.getPillagePotential(), true) + "<img MAIN_GUI GOLD_ICON>";
					
					tooltip.setText(pillageText);
				}

			} 
			
			armyMaint.setText("<img MAIN_GUI GOLD_ICON> -" + (int) displayed.maint);
			armyReinf.setText("<img MAIN_GUI RAISE_ARMY_ICON>" + (int) (displayed.getTroopChangeRate()));
			armyTroops.setText(displayed.getTotalTroops() + "/" +  displayed.getTotalTroopsMax());
			armyMoral.setProgress(displayed.getMoral());
			armyMoralRegen.setText(Util.getSignedText((int)(displayed.getMoralRegen()*100*World.DAYS_PER_SEASON)) + "%");
		
			if (displayed.isTravelling()) {
				stateLabel.setText("Moving\n(" + displayed.getEstimatedTravelTime() + "d)");
			} else if (displayed.isFighting()) {
				stateLabel.setText("In Battle");
			} else {
				stateLabel.setText("Idle");
			}
			
			Label tooltip = (Label) WindowManager.getInstance().getWindow(armyMoral.getName() + "/TooltipText");
			if (tooltip != null) {
				String text = 	"This armies moral changes by up\n"+
								"to " + Util.getFlaggedText(Util.getSignedText((int)(displayed.getMoralRegen()*100*World.DAYS_PER_SEASON)) + "%", displayed.getMoralRegen() > 0) + " within the next season.";
				tooltip.setText(text);
			}
			
			tooltip = (Label) WindowManager.getInstance().getWindow(armyReinf.getName() + "/TooltipText");
			if (tooltip != null) {
				String text = 	displayed.getReinforcementTooltip();
				tooltip.setText(text);
			}
		}
	}


	public Army getDisplayed() {
		return displayed;
	}
}
