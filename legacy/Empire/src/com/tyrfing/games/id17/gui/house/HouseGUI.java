package com.tyrfing.games.id17.gui.house;

import java.util.List;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.EmpireFrameListener.GameState;
import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.diplomacy.Diplomacy;
import com.tyrfing.games.id17.gui.TabGUI;
import com.tyrfing.games.id17.gui.laws.LawSetGUI;
import com.tyrfing.games.id17.gui.technology.TechnologyGUI;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.Loan;
import com.tyrfing.games.id17.intrigue.Intrigue;
import com.tyrfing.games.id17.networking.RequestDisplayData;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.graphics.renderables.FormattedText2.ALIGNMENT;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.gui.IEventListener;
import com.tyrlib2.gui.ImageBox;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.Window;
import com.tyrlib2.gui.WindowEvent;
import com.tyrlib2.gui.WindowEvent.WindowEventType;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.math.Vector3;

public class HouseGUI extends TabGUI<House> {

	public static final String[] HOUSE_GUI_TAB_NAMES = new String[] { "HOUSE/SIGIL", "HOUSE/DIPLO", "HOUSE/INTRIGUE", "HOUSE/MILITARY", "HOUSE/TECH", "RANK" };
	public static final String[] HOUSE_GUI_TAB_ATLAS_NAMES = new String[] { "SIGILS1", "MAIN_GUI", "MAIN_GUI", "MAIN_GUI", "MAIN_GUI" };
	public static final String[] HOUSE_GUI_TAB_REGION_NAMES = new String[] { "Rebels", "DIPLO_ICON", "INTRIGUE_ICON", "ARMY_ICON", "TECH_ICON" };
	public static final String[] HOUSE_GUI_TAB_TOOLTIPS = new String[] { "Overview", "Domestic Policies", "Intrigue", "Cycle Armies", "Technology"};
	
	public HouseOverviewGUI houseOverviewGUI;
	public ActionGUI diploGUI;
	public ActionGUI intrigueGUI;
	public IntrigueProjectGUI intrigueProjectGUI;
	public TechnologyGUI techGUI;
	public LawSetGUI lawGUI;
	
	private int holdingFocus;
	private int armyFocus;
	
	private int goldLastFrame;
	private int honorLastFrame;
	private int malesLastFrame;
	private int femalesLastFrame;
	
	private Label currentTech;
	private Label currentIntrigue;
	
	public HouseGUI() {
		super(	"HOUSE", HOUSE_GUI_TAB_NAMES.length, HOUSE_GUI_TAB_NAMES, HOUSE_GUI_TAB_ATLAS_NAMES, HOUSE_GUI_TAB_REGION_NAMES, HOUSE_GUI_TAB_TOOLTIPS);
		
		final TabGUI<House> gui = this;
		
		houseOverviewGUI = new HouseOverviewGUI(main);
		diploGUI = new ActionGUI(main, "DIPLO", Diplomacy.getInstance().categories);
		intrigueGUI = new ActionGUI(main, "INTRIGUE", Intrigue.getInstance().categories);
		intrigueProjectGUI = new IntrigueProjectGUI(main);
		techGUI = new TechnologyGUI();
		lawGUI = new LawSetGUI();
		
		subGUIs.add(houseOverviewGUI);
		subGUIs.add(diploGUI);
		subGUIs.add(intrigueGUI);
		subGUIs.add(intrigueProjectGUI);
		subGUIs.add(techGUI);
		subGUIs.add(lawGUI);
	
		WindowManager.getInstance().createImageBox("HOUSE/CANCEL", TabGUI.CANCEL_HOLDER_POS, "MAIN_GUI", "CANCEL", TabGUI.SIGIL_SIZE.multiply(0.95f).get());
		WindowManager.getInstance().getWindow("HOUSE/CANCEL").setReceiveTouchEvents(false);
		
		cancel = WindowManager.getInstance().createImageBox("HOUSE/CANCEL_HOLDER", new Vector2(-TabGUI.SIGIL_HOLDER_SIZE.x * 0.025f, -TabGUI.SIGIL_HOLDER_SIZE.x * 0.025f), "MAIN_GUI", "SMALL_CIRCLE_BORDER", TabGUI.SIGIL_HOLDER_SIZE.get());
		WindowManager.getInstance().getWindow("HOUSE/CANCEL").addChild(cancel);
		cancel.setReceiveTouchEvents(true);
		
		cancel.addEventListener(WindowEvent.WindowEventType.TOUCH_UP, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				hide();
			}
		});
		
		WindowManager.getInstance().getWindow(HOUSE_GUI_TAB_NAMES[0]).addEventListener(WindowEventType.TOUCH_DOWN, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				if (displayed != null && houseOverviewGUI.isVisible()) {
					List<Holding> holdings = displayed.getHoldings();
					if (holdings.size() > 0) {
						Holding h = holdings.get(holdingFocus++ % holdings.size());
						Vector3 pos = h.getHoldingData().worldEntity.getParent().getCachedAbsolutePos();
						EmpireFrameListener.MAIN_FRAME.camController.focus(pos);
					}
				} else if (displayed == null){
					hideSubUIs(houseOverviewGUI);
					gui.show(World.getInstance().getPlayerController().getHouse());
				} else {
					hideSubUIs(houseOverviewGUI);
					houseOverviewGUI.show(displayed);
				}
			}
		});
		
		WindowManager.getInstance().getWindow(HOUSE_GUI_TAB_NAMES[1]).addEventListener(WindowEventType.TOUCH_DOWN, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				if (!gui.isVisible()) {
					gui.show(World.getInstance().getPlayerController().getHouse());
				}
				if (displayed != World.getInstance().getPlayerController().getHouse()) {
					hideSubUIs(diploGUI);
					diploGUI.show(World.getInstance().getPlayerController().getHouse(), displayed);
				} else {
					hideSubUIs(lawGUI);
					lawGUI.show(World.getInstance().getPlayerController().getHouse());
				}
			}
		});
		
		WindowManager.getInstance().getWindow(HOUSE_GUI_TAB_NAMES[2]).addEventListener(WindowEventType.TOUCH_DOWN, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				if (!gui.isVisible()) {
					gui.show(World.getInstance().getPlayerController().getHouse());
				}
				if (World.getInstance().getPlayerController().getHouse().intrigueProject != null) {
					hideSubUIs(intrigueProjectGUI);
					intrigueProjectGUI.show(World.getInstance().getPlayerController().getHouse(), displayed);
				} else {
					hideSubUIs(intrigueGUI);
					intrigueGUI.show(World.getInstance().getPlayerController().getHouse(), displayed);
				}
			}
		});
		
		WindowManager.getInstance().getWindow(HOUSE_GUI_TAB_NAMES[3]).addEventListener(WindowEventType.TOUCH_DOWN, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				ImageBox tabHolder = (ImageBox) WindowManager.getInstance().getWindow(HOUSE_GUI_TAB_NAMES[3]);
				tabHolder.setAtlasRegion("SMALL_CIRCLE_BORDER");
 
				House h = displayed == null ? World.getInstance().getPlayerController().getHouse() : displayed;
				if (h.getBaronies().size() > 0) {
					Barony b = h.getBaronies().get(armyFocus++ % h.getBaronies().size());
					if (b.getLevy().isRaised()) {
						EmpireFrameListener.MAIN_FRAME.camController.focus(b.getLevy().getEntity().getParent());
					} else {
						EmpireFrameListener.MAIN_FRAME.camController.focus(b.holdingData.worldEntity.getParent());
					}
					
				}
			}
		});
		
		WindowManager.getInstance().getWindow(HOUSE_GUI_TAB_NAMES[4]).addEventListener(WindowEventType.TOUCH_DOWN, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				if (!gui.isVisible()) {
					gui.show(World.getInstance().getPlayerController().getHouse());
				}
				hideSubUIs(techGUI);
				if (gui.isVisible()) {
					techGUI.show(displayed);
				} else {
					techGUI.show(World.getInstance().getPlayerController().getHouse());
				}
			}
		});
		
		Vector2 offset = new Vector2((TAB_SIZE.get().x+TAB_PADDING.get().x)*HOUSE_GUI_TAB_ATLAS_NAMES.length, 0);
		Window tabHolder = WindowManager.getInstance().getWindow(HOUSE_GUI_TAB_NAMES[HOUSE_GUI_TAB_ATLAS_NAMES.length]);
		tabHolder.setRelativePos(TAB_POS.get().add(offset));
		Label rankLabel = (Label) WindowManager.getInstance().createLabel(	HOUSE_GUI_TAB_NAMES[HOUSE_GUI_TAB_ATLAS_NAMES.length] + "/Label", 
																			new Vector2(TAB_SIZE.get().x/2, TAB_SIZE.get().y/4), 
																			"5th");
		rankLabel.setFont(SceneManager.getInstance().getFont("FONT_16"));
		rankLabel.setAlignment(ALIGNMENT.CENTER);
		tabHolder.addChild(rankLabel);
		WindowManager.getInstance().addTextTooltip(tabHolder, "");
		header.addChild(tabHolder);
		
		/** Construct the sub header menu **/
		
		subHeader = WindowManager.getInstance().createRectWindow(	"HOUSE_GUI_SUB_HEADER", 
																	new Vector2(0, headerSize.y+TAB_PADDING.get().x*4), 
																	headerSize, GRAY_PAINT);
		header.addChild(subHeader);
		
		Label goldLabel = (Label) WindowManager.getInstance().createLabel("GOLD", LEFT_LABEL_POS, "<img MAIN_GUI GOLD_ICON> 315");
		goldLabel.setReceiveTouchEvents(true);
		WindowManager.getInstance().addTextTooltip(goldLabel, "");
		subHeader.addChild(goldLabel);
		
		Label honorLabel = (Label) WindowManager.getInstance().createLabel("HONOR", LEFT_LABEL_POS.get().add(new Vector2(0,headerSize.y/2)), "<img MAIN_GUI HONOR_ICON> 200");
		honorLabel.setReceiveTouchEvents(true);
		WindowManager.getInstance().addTextTooltip(honorLabel, "");
		subHeader.addChild(honorLabel);
		
		currentIntrigue = (Label) WindowManager.getInstance().createLabel("CURRENT_INTRIGUE", new Vector2(headerSize.x/2,0), "<img MAIN_GUI INTRIGUE_ICON_BIG> No Plot");
		currentIntrigue.setAlignment(ALIGNMENT.CENTER);
		subHeader.addChild(currentIntrigue);
		
		currentTech = (Label) WindowManager.getInstance().createLabel("CURRENT_TECH", new Vector2(headerSize.x/2,headerSize.y/2), "<img MAIN_GUI TECH_ICON_BIG> No Research");
		currentTech.setAlignment(ALIGNMENT.CENTER);
		subHeader.addChild(currentTech);
		
		Label maleLabel = (Label) WindowManager.getInstance().createLabel("MALES", new Vector2(headerSize.x+RIGHT_LABEL_POS.get().x, RIGHT_LABEL_POS.get().y) , "<img MAIN_GUI MALE_ICON> 5");
		maleLabel.setReceiveTouchEvents(true);
		WindowManager.getInstance().addTextTooltip(maleLabel, "");
		subHeader.addChild(maleLabel);
		
		Label femaleLabel = (Label) WindowManager.getInstance().createLabel("FEMALES", new Vector2(RIGHT_LABEL_POS.get().x+headerSize.x,headerSize.y/2+RIGHT_LABEL_POS.get().y), "<img MAIN_GUI FEMALE_ICON> 5");
		femaleLabel.setReceiveTouchEvents(true);
		WindowManager.getInstance().addTextTooltip(femaleLabel, "");
		subHeader.addChild(femaleLabel);
		
		options.add((ImageBox)WindowManager.getInstance().getWindow("HOUSE/CANCEL"));
		hide();
	}
	
	@Override
	public void show() {
		super.show();
		
		Label tooltip = (Label) WindowManager.getInstance().getWindow(HOUSE_GUI_TAB_NAMES[1]+"/TooltipText");
		tooltip.setText(HOUSE_GUI_TAB_TOOLTIPS[1]);
		
		ImageBox sigil = (ImageBox) WindowManager.getInstance().getWindow(HOUSE_GUI_TAB_NAMES[0]+"/Image");
		sigil.setAtlasRegion(World.getInstance().getPlayerController().getHouse().getSigilName());
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isClient()) {
			EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new RequestDisplayData(RequestDisplayData.HOUSE_DATA, 
																						 (short) World.getInstance().getPlayerController().getHouse().id));
		}
	}
	
		
	@Override
	public void display() {
		holdingFocus = 0;
		houseOverviewGUI.show(displayed);
		
		ImageBox tabHolder;
		
		ImageBox sigil = (ImageBox) WindowManager.getInstance().getWindow(HOUSE_GUI_TAB_NAMES[0]+"/Image");
		sigil.setAtlasRegion(displayed.getSigilName());
		
		if (displayed.getController() != World.getInstance().getPlayerController() && EmpireFrameListener.state != GameState.SELECT) {
			
			tabHolder = (ImageBox) WindowManager.getInstance().getWindow(HOUSE_GUI_TAB_NAMES[2]);
			tabHolder.setReceiveTouchEvents(true);
			tabHolder.setAtlasRegion("SMALL_CIRCLE_BORDER");
			
			Label tooltip = (Label) WindowManager.getInstance().getWindow(HOUSE_GUI_TAB_NAMES[1]+"/TooltipText");
			tooltip.setText("Foreign Policies");

		} else {
			
			tabHolder = (ImageBox) WindowManager.getInstance().getWindow(HOUSE_GUI_TAB_NAMES[2]);
			if (World.getInstance().getPlayerController().getHouse().intrigueProject == null) {
				tabHolder.setReceiveTouchEvents(false);
				tabHolder.setAtlasRegion("SMALL_CIRCLE_BORDER_DISABLED");
			} else {
				tabHolder.setReceiveTouchEvents(true);
				tabHolder.setAtlasRegion("SMALL_CIRCLE_BORDER");
			}
			
			Label tooltip = (Label) WindowManager.getInstance().getWindow(HOUSE_GUI_TAB_NAMES[1]+"/TooltipText");
			tooltip.setText(HOUSE_GUI_TAB_TOOLTIPS[1]);
		}
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isClient()) {
			EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new RequestDisplayData(RequestDisplayData.HOUSE_DATA, 
																						 (short) displayed.id));
		}

		update();
	}
	
	public House getDisplayed() {
		return displayed;
	}
	
	public void update() {
		if (houseOverviewGUI.isVisible()) {
			houseOverviewGUI.update();
		} else if (diploGUI.isVisible()) {
			diploGUI.update();
		} else if (intrigueGUI.isVisible()) {
			intrigueGUI.update();
		} 
		
		House house = displayed == null ? World.getInstance().getPlayerController().getHouse() : displayed;
		
		if (house == null) return;
		
		if (house == World.getInstance().getPlayerController().getHouse()) {
			ImageBox tabHolder = (ImageBox) WindowManager.getInstance().getWindow(HOUSE_GUI_TAB_NAMES[2]);
			if (house.intrigueProject == null) {
				tabHolder.setReceiveTouchEvents(false);
				tabHolder.setAtlasRegion("SMALL_CIRCLE_BORDER_DISABLED");
			} else {
				tabHolder.setReceiveTouchEvents(true);
				tabHolder.setAtlasRegion("SMALL_CIRCLE_BORDER");
			}
		}
		
		int gold = (int)house.getGold();
		if (gold != goldLastFrame) {
			((Label)WindowManager.getInstance().getWindow("GOLD")).setText("<img MAIN_GUI GOLD_ICON> " + String.valueOf(gold));
			goldLastFrame = gold;
		}
		
		int honor = (int)house.getHonor();
		if (honor != honorLastFrame) {
			((Label)WindowManager.getInstance().getWindow("HONOR")).setText("<img MAIN_GUI HONOR_ICON> " + String.valueOf(honor));
			honorLastFrame = honor;
		}
		
		int females = house.getFemales();
		int males = house.getMales();
		if (males != malesLastFrame || females != femalesLastFrame) {
			((Label)WindowManager.getInstance().getWindow("MALES")).setText("<img MAIN_GUI MALE_ICON> " + String.valueOf(males));
			((Label)WindowManager.getInstance().getWindow("FEMALES")).setText("<img MAIN_GUI FEMALE_ICON> " + String.valueOf(females));
			malesLastFrame = males;
			femalesLastFrame = females;
		}
		
		int rank = house.rank;
		String rankText = Util.getRankedText(rank);
		
		Window window = WindowManager.getInstance().getWindow("RANK/Label");
		if (window != null) {
			((Label)window).setText(rankText);
		}
		
		Label label = (Label) WindowManager.getInstance().getWindow("RANK/TooltipText");
		if (window != null) {
			String pointText = "";
			
			if (rank > 1) {
				House rankUp = World.getInstance().getRankedHouses().get(rank-2);
				pointText += " " + rankUp.getName() + ": " + (int)rankUp.points + " (+" + (int)(rankUp.getTotalPointInc()*World.DAYS_PER_SEASON*World.SECONDS_PER_DAY) + " x " + ((int)(rankUp.stats[House.SCORE_MULT]*10))/10.f + ")\n\n";
			}
			
			pointText += " Current " + (int)house.points + " (+" + (int)(house.getTotalPointInc()*World.DAYS_PER_SEASON*World.SECONDS_PER_DAY) + " x " + ((int)(house.stats[House.SCORE_MULT]*10))/10.f + ") \n";
			pointText += " Realm +" + (int)(house.pointsInc[House.REALM_POINTS]*World.DAYS_PER_SEASON*World.SECONDS_PER_DAY) + " \n";
			pointText += " House +" + (int)(house.pointsInc[House.HOUSE_POINTS]*World.DAYS_PER_SEASON*World.SECONDS_PER_DAY) + " \n";
			pointText += " Economy +" + (int)(house.pointsInc[House.ECONOMY_POINTS]*World.DAYS_PER_SEASON*World.SECONDS_PER_DAY) + " \n";
			pointText += " Military +" + (int)(house.pointsInc[House.MILITARY_POINTS]*World.DAYS_PER_SEASON*World.SECONDS_PER_DAY) + " \n";
			pointText += " Exploration +" + (int)(house.pointsInc[House.EXPLORATION_POINTS]*World.DAYS_PER_SEASON*World.SECONDS_PER_DAY) + " \n\n";
			
			if (rank < World.getInstance().getRankedHouses().size()) {
				House rankDown = World.getInstance().getRankedHouses().get(rank);
				pointText += " " + rankDown.getName() + ": " + (int)rankDown.points + " (+" + (int)(rankDown.getTotalPointInc()*World.DAYS_PER_SEASON*World.SECONDS_PER_DAY) + " x " + ((int)(rankDown.stats[House.SCORE_MULT]*10))/10.f + ")";
			}
			
			label.setText(pointText);
		}
		
		label = (Label) WindowManager.getInstance().getWindow("GOLD/TooltipText");
		if (label != null) {
			float income = ((int)(100*house.finalIncome))/100.f;
			String incomeText = " Income " + Util.getFlaggedText(Util.getSignedText(income), income >= 0) + "\n ------------- ";
			if (house.taxIncome != 0) {
				income = ((int)(100*house.taxIncome))/100.f;
				incomeText += "\n Taxes  " + Util.getFlaggedText(Util.getSignedText(income), income >= 0) + " ";
			}
			if (house.tradeIncome != 0) {
				income = ((int)(100*house.tradeIncome))/100.f;
				incomeText += "\n Trade  " + Util.getFlaggedText(Util.getSignedText(income), income >= 0) + " ";
			}
			
			if (house.vassalIncome != 0) {
				income = ((int)(100*house.vassalIncome))/100.f;
				incomeText += "\n Vassals  " + Util.getFlaggedText(Util.getSignedText(income), income >= 0) + " ";
			}
			
			if (house.armyMaint != 0) {
				income = ((int)(100*house.armyMaint))/100.f;
				incomeText += "\n Army   " + Util.getFlaggedText(Util.getSignedText(income), income >= 0) + " ";
			}
			if (house.mercCosts != 0) {
				income = ((int)(100*house.mercCosts))/100.f;
				incomeText += "\n Mercenaries " + Util.getFlaggedText(Util.getSignedText(income), income >= 0) + " ";
			}
			if (house.holdingMaint != 0) {
				income = ((int)(100*house.holdingMaint))/100.f;
				incomeText += "\n Govern " + Util.getFlaggedText(Util.getSignedText(income), income >= 0) + " ";
			}
			
			if (house.buildingMaint != 0) {
				income = ((int)(100*house.buildingMaint))/100.f;
				incomeText += "\n Buildings " + Util.getFlaggedText(Util.getSignedText(income), income >= 0) + " ";
			}
			
			if (house.interest != 0) {
				income = house.interest;
				incomeText += "\n Interest " + Util.getFlaggedText(Util.getSignedText(income), income >= 0) + " ";
			}
			
			if (house.loans.size() > 0) {
				for (int i = 0; i < house.loans.size(); ++i) {
					Loan loan = house.loans.get(i);
					incomeText += "\n\nLoan ";
					if (loan.giver == house) {
						incomeText += "to ";
					} else {
						incomeText += "from ";
					}
					incomeText += loan.getOther(house).getName() + "\n" +
								  loan.payback +  "<img MAIN_GUI GOLD_ICON> " +
								  "(" + World.toDate(loan.endDate) + ")";
				}
			}
			
			label.setText(incomeText);
		}
		
		label = (Label) WindowManager.getInstance().getWindow("MALES/TooltipText");
		if (label != null) {
			int holdings = house.getHoldings().size();
			String text = 	" Holdings  " + Util.getFlaggedText(String.valueOf(holdings), holdings <= house.getMaxHoldings()) + "/" + house.getMaxHoldings() + " \n";
			int fertility = (int)(house.getFertility()*100);
			int accGrowth = (int)(house.getAccGrowth()*100);
			text += 		" Fertility " + accGrowth + "/100 (" + Util.getFlaggedText(Util.getSignedText(fertility) + "%", fertility >= 0) + ")";
			label.setText(text);
			
			label = (Label) WindowManager.getInstance().getWindow("FEMALES/TooltipText");
			label.setText(text);
		}
		
		if (currentIntrigue != null) {
			if (house.intrigueProject != null && house.intrigueProject == World.getInstance().getPlayerController().getHouse().intrigueProject) {
				currentIntrigue.setText("<img MAIN_GUI INTRIGUE_ICON_BIG> " + house.intrigueProject.action.getName() + " (" + house.intrigueProject.getEstimatedRemainingDays() + "d)");
			} else if (house == World.getInstance().getPlayerController().getHouse()) {
				currentIntrigue.setText("<img MAIN_GUI INTRIGUE_ICON_BIG> No Plot ");
			} else {
				currentIntrigue.setText("<img MAIN_GUI INTRIGUE_ICON_BIG> ??? ");
			}
		}
		
		if (currentTech != null) {
			if (house.techProject != null && (house.haveSameOverlordWith(World.getInstance().getPlayerController().getHouse()) || World.getInstance().getPlayerController().getHouse().hasSpy(house))) {
				String text = "<img TECH " +  house.techProject.tech.name + "> " + house.techProject.tech.name + " (" + house.techProject.getEstimatedRemainingDays() + "d)";
				currentTech.setText(text);
			} else if (house == World.getInstance().getPlayerController().getHouse()){
				currentTech.setText("<img MAIN_GUI TECH_ICON_BIG> No Research ");
			} else {
				currentTech.setText("<img MAIN_GUI TECH_ICON_BIG> ??? ");
			}
		} 
		
	}

	@Override
	public boolean isFinished() {
		return false;
	}

}
