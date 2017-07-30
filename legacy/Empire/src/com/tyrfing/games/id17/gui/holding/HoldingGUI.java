package com.tyrfing.games.id17.gui.holding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.gui.DateGUI;
import com.tyrfing.games.id17.gui.MainGUI;
import com.tyrfing.games.id17.gui.TabGUI;
import com.tyrfing.games.id17.gui.charts.PieChart;
import com.tyrfing.games.id17.gui.war.ArmyBuilderGUI;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.holdings.PopulationType;
import com.tyrfing.games.id17.holdings.UnrestSource;
import com.tyrfing.games.id17.holdings.projects.RoadProject;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.networking.BuildRoadMessage;
import com.tyrfing.games.id17.networking.LevyAction;
import com.tyrfing.games.id17.networking.RequestDisplayData;
import com.tyrfing.games.id17.trade.Good;
import com.tyrfing.games.id17.world.RoadNode;
import com.tyrfing.games.id17.world.Tile;
import com.tyrfing.games.id17.world.World;
import com.tyrfing.games.id17.world.WorldChunk;
import com.tyrlib2.graphics.renderables.Entity;
import com.tyrlib2.graphics.renderables.FormattedText2.ALIGNMENT;
import com.tyrlib2.graphics.renderer.Texture;
import com.tyrlib2.graphics.renderer.TextureAtlas;
import com.tyrlib2.graphics.renderer.TextureManager;
import com.tyrlib2.graphics.renderer.TextureRegion;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.scene.SceneObject;
import com.tyrlib2.gui.IEventListener;
import com.tyrlib2.gui.ImageBox;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.Window;
import com.tyrlib2.gui.WindowEvent;
import com.tyrlib2.gui.WindowEvent.WindowEventType;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.input.IMotionEvent;
import com.tyrlib2.input.IMoveListener;
import com.tyrlib2.input.ITouchListener;
import com.tyrlib2.input.InputManager;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Color;
import com.tyrlib2.util.Raycast;
import com.tyrlib2.util.RaycastResult;

public class HoldingGUI extends TabGUI<Holding> implements IMoveListener, ITouchListener {
	public static final Vector2 BUILD_HOLDER_POS = new Vector2(TabGUI.SIGIL_POS.x, TabGUI.SIGIL_POS.y );
	public static final ScaledVector2 BUILD_HOLDER_OFFSET = new ScaledVector2(TabGUI.PADDING.x,TabGUI.PADDING.y, 3);
	public static final Vector2 ARMY_HOLDER_POS = new Vector2(BUILD_HOLDER_POS.x, BUILD_HOLDER_POS.y);
	public static final ScaledVector2 ARMY_HOLDER_OFFSET = new ScaledVector2(TabGUI.PADDING.x,2*TabGUI.PADDING.y, 3);
	public static final Vector2 RAISE_ARMY_HOLDER_POS =new Vector2(BUILD_HOLDER_POS.x, BUILD_HOLDER_POS.y);
	public static final ScaledVector2 RAISE_ARMY_HOLDER_OFFSET = new ScaledVector2(2*TabGUI.PADDING.x,3*TabGUI.PADDING.y, 3);
	public static final Vector2 OVERVIEW_HOLDER_POS = new Vector2(BUILD_HOLDER_POS.x, BUILD_HOLDER_POS.y);
	public static final ScaledVector2 OVERVIEW_HOLDER_OFFSET = new ScaledVector2(3*TabGUI.PADDING.x,4*TabGUI.PADDING.y, 3);
	public static final ScaledVector2 OVERVIEW_SIZE = new ScaledVector2(TabGUI.SIGIL_HOLDER_SIZE.x * 0.6f, TabGUI.SIGIL_HOLDER_SIZE.y * 0.6f, 3);
	public static final ScaledVector2 OVERVIEW_POS= new ScaledVector2(TabGUI.SIGIL_HOLDER_SIZE.x * 0.2f, TabGUI.SIGIL_HOLDER_SIZE.y * 0.2f, 3);
	
	public static final String[] HOLDING_GUI_TAB_NAMES = new String[] { "HOLDING/SIGIL", "HOLDING/BUILD", "HOLDING/ARMY", };
	public static final String[] HOLDING_GUI_TAB_ATLAS_NAMES = new String[] { "SIGILS1", "MAIN_GUI", "MAIN_GUI" };
	public static final String[] HOLDING_GUI_TAB_REGION_NAMES = new String[] { "Rebels", "BUILD_ICON", "REGIMENT_ICON" };
	public static final String[] HOLDING_GUI_TAB_TOOLTIPS = new String[] { "Owner", "Construction", "Levy & Garrison"};
	
	public static final Color MERCHANT_COLOR = new Color(1f, 1f, 0.4f, 1f);
	public static final Color SCHOLAR_COLOR = new Color(0.5f, 0.8f, 1.0f, 1f);
	public static final Color PEASANT_COLOR = new Color(0.7f, 0.5f, 0.5f, 1f);
	public static final Color WORKER_COLOR = new Color(1.0f, 0.6f, 0.4f, 1f);
	
	
	public static final String BASE_ROAD_TEXT = 
			"Build Road\n" + 
			"(1) Enables Migration\n" + 
			"(2) Enables Good Transfer\n" + 
			"(3) Army Movement " + Util.getFlaggedText("+50%", true);
	
	private ImageBox raiseArmyHolder;
	private ImageBox buildRoadHolder;
	
	private List<ImageBox> disableHolders = new ArrayList<ImageBox>();
	private ArmyBuilderGUI armyBuilderGUI;
	public final ProductionGUI productionGUI;
	
	private Window subHeader;
	private Window popHeader;
	private Window subHeader2;
	
	private ImageBox chartImage;
	
	private Label holdingName;
	private Label incomeLabel;
	private Label prodLabel;
	private Label researchLabel;
	private Label revoltLabel;
	private Label popLabel;
	
	private boolean raiseEnabled;
	private boolean buildRoadEnabled;
	
	private boolean roadConstruction;
	private Holding selection;
	private int countDowns;
	
	public HoldingGUI() {
		super("HOLDING", HOLDING_GUI_TAB_NAMES.length, HOLDING_GUI_TAB_NAMES, HOLDING_GUI_TAB_ATLAS_NAMES, HOLDING_GUI_TAB_REGION_NAMES, HOLDING_GUI_TAB_TOOLTIPS);
		
		armyBuilderGUI = new ArmyBuilderGUI(main);
		productionGUI = new ProductionGUI(main);
		
		subGUIs.add(armyBuilderGUI);
		subGUIs.add(productionGUI);

		WindowManager.getInstance().createImageBox("HOLDING/CANCEL", TabGUI.CANCEL_HOLDER_POS.add(TabGUI.CANCEL_HOLDER_OFFSET.get()), "MAIN_GUI", "CANCEL", TabGUI.SIGIL_SIZE.multiply(0.95f).get());
		WindowManager.getInstance().getWindow("HOLDING/CANCEL").setReceiveTouchEvents(false);
		
		cancel = WindowManager.getInstance().createImageBox("HOLDING/CANCEL_HOLDER", new Vector2(-TabGUI.SIGIL_HOLDER_SIZE.x * 0.025f, -TabGUI.SIGIL_HOLDER_SIZE.x * 0.025f), "MAIN_GUI", "SMALL_CIRCLE_BORDER", TabGUI.SIGIL_HOLDER_SIZE.get());
		WindowManager.getInstance().getWindow("HOLDING/CANCEL").addChild(cancel);
		cancel.setReceiveTouchEvents(true);
		
		cancel.addEventListener(WindowEvent.WindowEventType.TOUCH_UP, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				hide();
			}
		});
		
		WindowManager.getInstance().getWindow(HOLDING_GUI_TAB_NAMES[0]).addEventListener(WindowEventType.TOUCH_DOWN, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				displayHouse();
			}
		});
		
		WindowManager.getInstance().getWindow(HOLDING_GUI_TAB_NAMES[1]).addEventListener(WindowEventType.TOUCH_DOWN, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				hideSubUIs(productionGUI);
				productionGUI.show(displayed);
			}
		});
		
		WindowManager.getInstance().getWindow(HOLDING_GUI_TAB_NAMES[2]).addEventListener(WindowEventType.TOUCH_DOWN, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				hideSubUIs(armyBuilderGUI);
				Barony barony = (Barony) displayed;
				armyBuilderGUI.show(barony);
			}
		});
		
		subHeader = WindowManager.getInstance().createRectWindow(	"HOLDING_GUI_SUB_HEADER", 
																	new Vector2(0, headerSize.y+TAB_PADDING.get().x*3), 
																	headerSize, GRAY_PAINT);
		header.addChild(subHeader);
		
		
		incomeLabel = (Label) WindowManager.getInstance().createLabel("HOLDING/INCOME", LEFT_LABEL_POS, "<img MAIN_GUI GOLD_ICON> +38");
		incomeLabel.setReceiveTouchEvents(true);
		WindowManager.getInstance().addTextTooltip(incomeLabel, "");
		subHeader.addChild(incomeLabel);
		
		prodLabel = (Label) WindowManager.getInstance().createLabel("HOLDING/PROD", new Vector2(headerSize.x+RIGHT_LABEL_POS.get().x*1.6f, RIGHT_LABEL_POS.get().y), "<img MAIN_GUI BUILD_ICON_BIG> +200");
		prodLabel.setReceiveTouchEvents(true);
		WindowManager.getInstance().addTextTooltip(prodLabel, "");
		subHeader.addChild(prodLabel);
		
		researchLabel = (Label) WindowManager.getInstance().createLabel("HOLDING/RESEARCH", LEFT_LABEL_POS.get().add(new Vector2(0,headerSize.y/2)), "<img MAIN_GUI TECH_ICON_BIG> +1.2");
		researchLabel.setReceiveTouchEvents(true);
		WindowManager.getInstance().addTextTooltip(researchLabel, "");
		subHeader.addChild(researchLabel);
		
		revoltLabel = (Label) WindowManager.getInstance().createLabel("HOLDING/REVOLT", new Vector2(RIGHT_LABEL_POS.get().x*1.6f+headerSize.x,headerSize.y/2+RIGHT_LABEL_POS.get().y), "<img SIGILS1 Rebels> 2.0%");
		revoltLabel.setReceiveTouchEvents(true);
		WindowManager.getInstance().addTextTooltip(revoltLabel, "");
		subHeader.addChild(revoltLabel);
		
		float width = (headerSize.y*2+TAB_PADDING.get().x*3)/SceneManager.getInstance().getViewportRatio();
		
		popHeader = WindowManager.getInstance().createRectWindow(	"HOLDING_GUI_POP_HEADER", 
																	new Vector2(headerSize.x+TAB_PADDING.get().x*1,0), 
																	new Vector2(width, headerSize.y*2+TAB_PADDING.get().x*3), GRAY_PAINT);
		header.addChild(popHeader);


		subHeader2 = WindowManager.getInstance().createRectWindow(	"HOLDING_GUI_SUB_HEADER2", 
																	new Vector2(0, headerSize.y*2+TAB_PADDING.get().x*9), 
																	new Vector2(headerSize.x+width+TAB_PADDING.get().x*1, headerSize.y/2), GRAY_PAINT);
		header.addChild(subHeader2);
		
		holdingName = (Label) WindowManager.getInstance().createLabel("HOLDING/NAME", LEFT_LABEL_POS, "Undefined");
		subHeader2.addChild(holdingName);
		
		popLabel = (Label) WindowManager.getInstance().createLabel("HOLDING/POP", new Vector2(LEFT_LABEL_POS.get().x+headerSize.x+width/2,LEFT_LABEL_POS.get().y), "0/0");
		popLabel.setAlignment(ALIGNMENT.CENTER);
		popLabel.setReceiveTouchEvents(true);
		WindowManager.getInstance().addTextTooltip(popLabel, "");
		WindowManager.getInstance().addTextTooltip(popLabel, "");
		WindowManager.getInstance().addTextTooltip(popLabel, "");
		subHeader2.addChild(popLabel);
		
		raiseArmyHolder = (ImageBox) WindowManager.getInstance().createImageBox("HOLDING/RAISE_ARMY_HOLDER", RAISE_ARMY_HOLDER_POS.add(RAISE_ARMY_HOLDER_OFFSET.get()), "MAIN_GUI", "SMALL_CIRCLE_WOOD", TabGUI.SIGIL_HOLDER_SIZE.get());
		
		raiseArmyHolder.addEventListener(WindowEventType.TOUCH_UP, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				if (raiseEnabled) {
					raise();
				}
			}
		});
		
		WindowManager.getInstance().createImageBox("HOLDING/RAISE_ARMY_HOLDER/RAISE_ARMY_ICON", new Vector2(), "MAIN_GUI", "RAISE_ARMY_ICON", TabGUI.SIGIL_HOLDER_SIZE.get());
		raiseArmyHolder.addChild(WindowManager.getInstance().getWindow("HOLDING/RAISE_ARMY_HOLDER/RAISE_ARMY_ICON"));
		
		
		
		buildRoadHolder = (ImageBox) WindowManager.getInstance().createImageBox("HOLDING/BUILD_RODE_HOLDER", RAISE_ARMY_HOLDER_POS.add(RAISE_ARMY_HOLDER_OFFSET.get().multiply(2)), "MAIN_GUI", "SMALL_CIRCLE_WOOD", TabGUI.SIGIL_HOLDER_SIZE.get());
		
		buildRoadHolder.addEventListener(WindowEventType.TOUCH_UP, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				if (buildRoadEnabled) {
					roadSelection();
				}
			}
		});
		
		WindowManager.getInstance().createImageBox("HOLDING/BUILD_RODE_HOLDER/BUILD_ROAD_ICON", new Vector2(), "MAIN_GUI", "GOLD_ICON", TabGUI.SIGIL_HOLDER_SIZE.get());
		buildRoadHolder.addChild(WindowManager.getInstance().getWindow("HOLDING/BUILD_RODE_HOLDER/BUILD_ROAD_ICON"));
		
		disableHolders.add(raiseArmyHolder);
		disableHolders.add(buildRoadHolder);

		options.add((ImageBox)WindowManager.getInstance().getWindow("HOLDING/CANCEL"));
		options.add(raiseArmyHolder);
		options.add(buildRoadHolder);
		
		WindowManager.getInstance().addTextTooltip(raiseArmyHolder, "Raise Levy");
		WindowManager.getInstance().addTextTooltip(buildRoadHolder, 
			BASE_ROAD_TEXT
		);
		
		main.setVisible(false);
		for (int i = 0; i < options.size(); ++i) {
			options.get(i).setVisible(false);
		}
		
		InputManager.getInstance().addTouchListener(this);
		InputManager.getInstance().addMoveListener(this);
	}
	
	@Override
	public void display() {
		//sigil.setAtlasRegion(displayed.getOwner().getName());
		
		holdingName.setText(displayed.getName() + ", Pop:");
		
		ImageBox sigil = (ImageBox) WindowManager.getInstance().getWindow(HOLDING_GUI_TAB_NAMES[0]+"/Image");
		sigil.setAtlasRegion(displayed.getOwner().getSigilName());
		
		ImageBox tabHolder = (ImageBox) WindowManager.getInstance().getWindow(HOLDING_GUI_TAB_NAMES[2]);
		if (displayed instanceof Barony 
			&& 		(World.getInstance().getPlayerController().getHouse().hasSpy(displayed.getOwner())
				|| 	World.getInstance().getPlayerController().getHouse().haveSameOverlordWith(displayed.getOwner()))) {
			
			tabHolder.setAtlasRegion("SMALL_CIRCLE_BORDER");
			tabHolder.setReceiveTouchEvents(true);
		} else {
			tabHolder.setAtlasRegion("SMALL_CIRCLE_BORDER_DISABLED");
			tabHolder.setReceiveTouchEvents(false);
		}
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isClient()) {
			EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new RequestDisplayData(RequestDisplayData.HOLDING_DATA, 
																						 displayed.getHoldingID()));
		}
		
		productionGUI.show(displayed);
		
		update();
	}
	
	
	public void raise() {
		if (displayed != null) {
			Barony barony = (Barony) displayed;
			if (barony.getLevy().isRaised()){
				if (!barony.getLevy().isFighting() && !barony.getLevy().isTravelling() && barony.getLevy().getCurrentHolding() == barony) {
					
					if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
						barony.getLevy().unraise();
					} else {
						EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new LevyAction(	barony.getLevy().id, 
																								LevyAction.UNRAISE,
																								(short) 0));
					}
				}
			} else {
				if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
					barony.raiseArmy();
				} else {
					short index = barony.getHoldingID();
					EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new LevyAction(	barony.getLevy().id, 
																							LevyAction.RAISE, 
																							index));	
				}
			}
		}
	}
	
	private void displayHouse() {
		MainGUI gui = World.getInstance().getMainGUI();
		if (displayed != null) {
			gui.houseGUI.show(displayed.getOwner());
			gui.pickerGUI.hideAll();
		}
	}
	
	public Holding getDisplayed() {
		return displayed;
	}

	@Override
	public boolean isFinished() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void update() {
		
		if (displayed != null && displayed.getOwner() != null && displayed.getOwner().getController() != null) {
			if (displayed.getOwner().getController() == World.getInstance().getPlayerController() && displayed instanceof Barony) {
				for (int i = 0; i < disableHolders.size(); ++i) {
					disableHolders.get(i).setAtlasRegion("SMALL_CIRCLE_WOOD");
					disableHolders.get(i).setReceiveTouchEvents(true);
				}
			} else {
				for (int i = 0; i < disableHolders.size(); ++i) {
					disableHolders.get(i).setAtlasRegion("SMALL_CIRCLE_WOOD_DISABLED");
					disableHolders.get(i).setReceiveTouchEvents(false);
				}
			}
			
			buildRoadHolder.setReceiveTouchEvents(true);
			
			if (displayed.holdingData.barony.getOwner() == World.getInstance().getPlayerController().getHouse() && displayed.getProject() == null) {
				buildRoadHolder.setAtlasRegion("SMALL_CIRCLE_WOOD");
				buildRoadEnabled = true;
			} else {
				buildRoadHolder.setAtlasRegion("SMALL_CIRCLE_WOOD_DISABLED");
				buildRoadEnabled = false;
			}
			
			boolean enoughGold = World.getInstance().getPlayerController().getHouse().getGold() >= RoadProject.getCosts(displayed.holdingData.barony);
			
			if (!enoughGold) {
				buildRoadEnabled = false;
			}
			
			buildRoadHolder.getTooltipLabel().setText(
				BASE_ROAD_TEXT + "\n" +
				"Funds " + Util.getFlaggedText(String.valueOf(RoadProject.getCosts(displayed.holdingData.barony)), enoughGold) + "<img MAIN_GUI GOLD_ICON>, Time " + RoadProject.getExpectedDays(displayed) + "d"
			);
			
			if (displayed instanceof Barony) {
				
				Barony barony = (Barony) displayed;
				
				float percent =  0;
				if (barony.getLevy().getTotalTroopsMax() != 0) {
					percent = (float)barony.getLevy().getTotalTroops() / barony.getLevy().getTotalTroopsMax();
				}
				
				if (percent >= 0.3f && barony.getOwner() == World.getInstance().getPlayerController().getHouse() &&
					(barony.getLevy().getCurrentHolding() == displayed || !barony.getLevy().isRaised())) {
					raiseArmyHolder.setAtlasRegion("SMALL_CIRCLE_WOOD");
					raiseEnabled = true;
				} else {
					raiseArmyHolder.setAtlasRegion("SMALL_CIRCLE_WOOD_DISABLED");
					raiseEnabled = false;
				}
				
				
				Label tooltip = (Label) raiseArmyHolder.getTooltipLabel();
				if (tooltip != null) {
					if (!barony.getLevy().isRaised()) {

						tooltip.setText(
							"Raise Army\n" +
							Util.getFlaggedText("(1)", percent >= 0.3f) + " Levy (" + (int)(percent*100) + "%) >= 30%"
						);
					} else {
						tooltip.setText(
								"Unraise Army\n" + 
								Util.getFlaggedText("(1)", barony.getLevy().getCurrentHolding() == displayed ) + " Levy present"
						);
					}
				}
			}
		}
		
		if (armyBuilderGUI.isVisible()) {
			armyBuilderGUI.update();
		}
		
		if (productionGUI.isVisible()) {
			productionGUI.update();
		}
		
		if (displayed != null) {
			float freeSupplies =  displayed.holdingData.barony.holdingData.supplies;
			for (int i = 0; i < displayed.holdingData.barony.getCountSubHoldings(); ++i) {
				if ( displayed.holdingData.barony.getSubHolding(i) != displayed) {
					freeSupplies -= displayed.holdingData.barony.getSubHolding(i).holdingData.inhabitants;
				}
			}
			popLabel.setText((int)displayed.getHoldingData().inhabitants +  "/" + (int)freeSupplies);
			incomeLabel.setText("<img MAIN_GUI GOLD_ICON> " +  Float.valueOf( (int)(displayed.holdingData.income*displayed.holdingData.incomeMult*100*displayed.getOwner().stats[House.INCOME_MULT])/100.f ));
			String prod = String.valueOf((int)(displayed.holdingData.prod*10)/10.f);
			prodLabel.setText("<img MAIN_GUI BUILD_ICON_BIG> " +  prod);
			researchLabel.setText("<img MAIN_GUI TECH_ICON_BIG> " +  Float.valueOf((int)(displayed.holdingData.research*10*displayed.holdingData.researchMult * displayed.getOwner().stats[House.RESEARCH_MULT]))/10);
			revoltLabel.setText("<img SIGILS1 Rebels> " +  Float.valueOf( ((int)(displayed.holdingData.accRevoltRisk*1000))/10.f ) +  "%");
			
			Label tooltip = (Label) WindowManager.getInstance().getWindow(popLabel.getName() + "/TooltipText");
			if (tooltip != null) {
				String text = 	"Pop Changes\n" + 
								"---------------";
				
				int growth = displayed.getGrowth();
				int hunger = displayed.getHunger();
				int wander = displayed.getWander();
				
				if (growth != 0) {
					text += "\nGrowth " + Util.getFlaggedText(Util.getSignedText(growth), true);
				}
				
				if (hunger != 0) {
					text += "\nHunger " + Util.getFlaggedText(Util.getSignedText(hunger), false);
				}
				
				if (wander != 0) {
					text += "\nMigration " + Util.getFlaggedText(Util.getSignedText(wander), wander > 0);
				}
				
				tooltip.setText(text);
			}
			
			tooltip = (Label) WindowManager.getInstance().getWindow(popLabel.getName() + "/TooltipText2");
			if (tooltip != null) {
				String text = 	"Supplies\n" + 
								"---------------\n"+
								"Base " + Util.getFlaggedText("+"  + (int)displayed.holdingData.baseSupplies, true);
				
				if (displayed.holdingData.tradeSupplies != 0) {
					text += "\nFood " + Util.getFlaggedText("+"  + (int)displayed.holdingData.tradeSupplies, true);
				}
				
				float seasonChange = World.getInstance().getSupplyFactor() * (displayed.holdingData.baseSupplies+displayed.holdingData.tradeSupplies) - (displayed.holdingData.baseSupplies+displayed.holdingData.tradeSupplies);
				
				if (World.getInstance().getSupplyFactor() != 1) {
					String seasonName = World.getSeasonNameFull(World.getInstance().getSeason());
					text += "\n" +  seasonName + " " + Util.getFlaggedText(Util.getSignedText((int)seasonChange), seasonChange > 0);
				}
				
				for (int i = 0; i < displayed.holdingData.barony.getCountSubHoldings(); ++i) {
					Holding h = displayed.holdingData.barony.getSubHolding(i);
					if (h != displayed) {
						int inhabitants = (int) h.holdingData.inhabitants;
						if (inhabitants != 0) {
							text += "\n" + h.getName() + " " + Util.getFlaggedText(Util.getSignedText(-inhabitants), false);
						}
					}
				}
				
				tooltip.setText(text);
			}
			
			tooltip = (Label) WindowManager.getInstance().getWindow(popLabel.getName() + "/TooltipText3");
			if (tooltip != null) {
				String text = 	"Demands\n" + 
								"---------------";

				boolean hasDemands = false;
				
				for (int i = 0; i < Good.COUNT_GOODS; ++i) {
					float demand = displayed.getDemand(i);
					if (demand > 0) {
						hasDemands = true;
						String goodName = Good.getName(i);
						int supply = (int) displayed.getSupply(i);
						text += "\n- " +  goodName + "<img GOODS " + goodName + "> " + Util.getFlaggedText(String.valueOf((int)demand), false) + " (" + Util.getFlaggedText(String.valueOf(supply), true) +")";
					}
				}
				
				
				int foodDemand = (int) displayed.getFoodDemand();
				if (foodDemand != 0) {
					hasDemands = true;
					text += "\n- Food";
					for (int i = 0; i < Good.FOOD_IDS.length; ++i) {
						text += "<img GOODS " + Good.getName(Good.FOOD_IDS[i]) + ">";
					}
					text += Util.getFlaggedText(String.valueOf((int)foodDemand), false) + " (" + Util.getFlaggedText(String.valueOf(Math.round(displayed.getFoodSupply())), true) +")";
				}
				
				if (hasDemands) {
					text += "\n---------------";
				}
				
				text += "\nValuation " + Util.getFlaggedText(Util.getSignedText((int)(displayed.getSupplyAttractivity()*100)) + "%", true);
				
				tooltip.setText(text);
			}
			
			tooltip = (Label) WindowManager.getInstance().getWindow(prodLabel.getName() + "/TooltipText");
			if (tooltip != null) {
				String text = 	"Productivity" + 
								"\n---------------";
				if ((int)(displayed.holdingData.prodPop*100)/100.f != 0) {
					text += "\nWorkforce " + Util.getFlaggedText(Util.getSignedText((int)(displayed.holdingData.prodPop*100)/100.f), true);
				}
				
				if ((int)(displayed.holdingData.prodBuildings*100)/100.f != 0) {
					text += "\nInfrastructure " + Util.getFlaggedText(Util.getSignedText((int)(displayed.holdingData.prodBuildings*100)/100.f), true);
				}
				
				if ((int)(displayed.holdingData.prodTrade*100)/100.f != 0) {
					text += "\nGoods " + Util.getFlaggedText(Util.getSignedText((int)(displayed.holdingData.prodTrade*100)/100.f), true);
				}
				
				tooltip.setText(text);
			}
			
			tooltip = (Label) WindowManager.getInstance().getWindow(incomeLabel.getName() + "/TooltipText");
			if (tooltip != null) {
				String text = 	"Income" + 
								"\n---------------";
				
				if ((int)(displayed.holdingData.taxes*100)/100.f != 0) {
					text += "\nPop Taxes " + Util.getFlaggedText(Util.getSignedText((int)(displayed.holdingData.taxes*100)/100.f), true);
				}
				
				if ((int)(displayed.holdingData.trade*100)/100.f != 0) {
					text += "\nTrade Taxes " + Util.getFlaggedText(Util.getSignedText((int)(displayed.holdingData.trade*100)/100.f), true);
				}
				
				if (displayed instanceof Barony) {
					float maint = -((Barony) displayed).getLevy().maint;
					if ((int)(maint*100)/100.f != 0) {
						text += "\nLevy " + Util.getFlaggedText(Util.getSignedText((int)(maint*100)/100.f), false);
					}
					maint = -((Barony) displayed).getGarrison().maint;
					if ((int)(maint*100)/100.f != 0) {
						text += "\nGarrison " + Util.getFlaggedText(Util.getSignedText((int)(maint*100)/100.f), false);
					}
				}
				
				if ((int)(displayed.holdingData.buildingMaint*100)/100.f != 0) {
					text += "\nBuildings " + Util.getFlaggedText(Util.getSignedText((int)(-displayed.holdingData.buildingMaint*100)/100.f), false);
				}
				
				tooltip.setText(text);
			}
			
			tooltip = (Label) WindowManager.getInstance().getWindow(researchLabel.getName() + "/TooltipText");
			if (tooltip != null) {
				String text = 	"Research" + 
								"\n---------------";
				
				if ((int)(displayed.holdingData.researchPop*100)/100.f != 0) {
					text += "\nScholars " + Util.getFlaggedText(Util.getSignedText((int)(displayed.holdingData.researchPop*100)/100.f), true);
				}
				
				if ((int)(displayed.holdingData.researchBuildings*100)/100.f != 0) {
					text += "\nBuildings " + Util.getFlaggedText(Util.getSignedText((int)(displayed.holdingData.researchBuildings*100)/100.f), true);
				}
				
				tooltip.setText(text);
			}
			
			tooltip = (Label) WindowManager.getInstance().getWindow(revoltLabel.getName() + "/TooltipText");
			if (tooltip != null) {
				String text = 	"Unrest. Revolt at 100%" + 
								"\n---------------";
				
				int totalTroops = displayed.holdingData.barony == displayed ? ( displayed.holdingData.barony.getLevy().getTotalTroops() +  displayed.holdingData.barony.getGarrison().getTotalTroops() ) : 0;
				
				for (int i = 0; i < displayed.getCountUnrestSources(); ++i) {
					UnrestSource src = displayed.getUnrestSource(i);
					if (totalTroops > 0) {
						text += "\n" + src.name + " " + Util.getFlaggedText(Util.getSignedText((int)(Math.sqrt(totalTroops)*displayed.getRevoltRisk(src)*1000)/10.f) + "%", false);
					} else {
						text += "\n" + src.name + " " + Util.getFlaggedText(Util.getSignedText((int)(displayed.getRevoltRisk(src)*1000)/10.f) + "%", false);
					}
				}
				
				if ((int)(displayed.holdingData.troopRevoltStop*1000)/10.f != 0) {
					text += "\nTroops " + Util.getFlaggedText("-" + (int)(displayed.holdingData.troopRevoltStop*1000)/10.f + "%", true);
				}
				
				tooltip.setText(text);
			}
			
			int workers = (int) displayed.getHoldingData().population[PopulationType.Workers.ordinal()];
			int scholars = (int) displayed.getHoldingData().population[PopulationType.Scholars.ordinal()];
			int peasants = (int) displayed.getHoldingData().population[PopulationType.Peasants.ordinal()];
			int merchants = (int) displayed.getHoldingData().population[PopulationType.Traders.ordinal()];
			
			PieChart chart = new PieChart((int)(256/WindowManager.getInstance().getScale(0).x), (int)(256/WindowManager.getInstance().getScale(0).y));
			if (merchants > 0) {
				chart.addDataSet(merchants, MERCHANT_COLOR, "Traders");
			}
			
			if (scholars > 0) {
				chart.addDataSet(scholars, SCHOLAR_COLOR, "Scholars");
			}
			
			if (workers > 0) {
				chart.addDataSet(workers, WORKER_COLOR, "Workers");
			}
			
			if (peasants > 0) {
				chart.addDataSet(peasants, PEASANT_COLOR, "Peasants");
			}
			
			Texture texture = TextureManager.getInstance().getTexture("PIE_CHART");
			if (texture != null) {
				TextureManager.getInstance().destroyTexture(texture);
			}
			
			texture = chart.build("PIE_CHART");
	
			if (chartImage == null) {
				TextureAtlas atlas = new TextureAtlas(texture);
				atlas.addRegion("FULL", new TextureRegion(0,0,1,1));
				SceneManager.getInstance().addTextureAtlas("HOLDING_ATLAS", atlas);
				chartImage = (ImageBox) WindowManager.getInstance().createImageBox("HOLDING/CHART", new Vector2(0,0), "HOLDING_ATLAS", "FULL", popHeader.getSize());
				popHeader.addChild(chartImage);
			} else {
				chartImage.setTexture(texture);
			}
		}
	}

	@Override
	public void showOptions() {
		for (int i = 0; i < options.size(); ++i) {
			if (options.get(i) != null) {
				options.get(i).moveTo(new Vector2(DateGUI.FAST_FORWARD_BUTTON_POS.x+DateGUI.FAST_FORWARD_BUTTON_OFFSET.get().x, SIGIL_POS.y + PADDING.get().y * (i+1)), DISPLAY_TIME);
			}
		}
	}
	
	private void roadSelection() {
		Holding[] neighbours = World.getInstance().getMap().getNeighboursHolding(displayed);
		for (int i = 0; i < neighbours.length; ++i) {
			if (RoadProject.canBuild(displayed, neighbours[i])) {
				neighbours[i].setColor(Color.YELLOW);
			}
		}
		
		roadConstruction = true;
		countDowns = 0;
	}

	@Override
	public long getPriority() {
		return 1;
	}

	@Override
	public boolean onTouchDown(Vector2 point, IMotionEvent event, int fingerId) {
		if (countDowns >= 0) {
			if (selection != null) {
				
				
				if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
					displayed.startProject(new RoadProject(displayed, selection));
				} else {
					EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new BuildRoadMessage(displayed.getHoldingID(), selection.getHoldingID()));
				}
				
				hideRoadPreview();

			}
			
			Holding[] neighbours = World.getInstance().getMap().getNeighboursHolding(displayed);
			for (int i = 0; i < neighbours.length; ++i) {
				if (neighbours[i].holdingData.barony.isExplored()) {
					neighbours[i].removeColor();
				}
			}
			
			roadConstruction = false;
		}
		
		countDowns++;
		
		return true;
	}

	@Override
	public boolean onTouchUp(Vector2 point, IMotionEvent event, int fingerId) {
		return false;
	}

	@Override
	public boolean onTouchMove(Vector2 point, IMotionEvent event, int fingerId) {
		pOnMove(point);
		return false;
	}
	
	private void hideRoadPreview() {
		List<RoadNode> unrealizedPath = World.getInstance().getMap().getRoadMap().hasDirectRealizeablePath(displayed.getHoldingID(), selection.getHoldingID());
		for (int j = 1; j < unrealizedPath.size()-1; ++j) {
			RoadNode node = unrealizedPath.get(j);
			if (!node.isRealized()) {
				Tile t = World.getInstance().getMap().getTile(node.x, node.y);
				t.chunk.changeTileType(t, 1);
			}
		}
		
		selection = null;
	}

	@Override
	public boolean isEnabled() {
		return roadConstruction;
	}

	@Override
	public boolean onMove(Vector2 point) {
		pOnMove(point);
		return false;
	}
	
	private void pOnMove(Vector2 point) {
		
		if (!roadConstruction) return;
		
		Raycast raycast = Raycast.fromScreen(point);
		List<RaycastResult> results = raycast.performRaycast();
		Collections.sort(results);
		
		for (int i = 0; i < results.size(); ++i) {
			RaycastResult result = results.get(i);
			SceneObject object = result.sceneObject;
			if (object.getMask() == WorldChunk.HOLDING_MASK) {
				Entity entity = (Entity) object;
				Holding holding = World.getInstance().getHolding(entity);
				if (holding != null && holding.holdingData.barony.isExplored() && holding.getColor() == Color.YELLOW) {					
					if (selection != holding && !World.getInstance().getMap().getRoadMap().hasDirectPath(displayed.getHoldingID(), holding.getHoldingID())) {
						if (selection != null) {
							hideRoadPreview();
						}
						
						selection = holding;
						
						List<RoadNode> unrealizedPath = World.getInstance().getMap().getRoadMap().hasDirectRealizeablePath(displayed.getHoldingID(), selection.getHoldingID());
						for (int j = 1; j < unrealizedPath.size()-1; ++j) {
							RoadNode node = unrealizedPath.get(j);
							Tile t = World.getInstance().getMap().getTile(node.x, node.y);
							t.chunk.changeTileType(t, 8);
						}
					}
					
					return;
				}
			} 
		}
		
		if (selection != null) {
			hideRoadPreview();
		}
	}

	@Override
	public boolean onEnterRenderWindow() {
		return false;
	}

	@Override
	public boolean onLeaveRenderWindow() {
		return false;
	}

	@Override
	public boolean onRenderWindowLoseFocus() {
		return false;
	}

	@Override
	public boolean onRenderWindowGainFocus() {
		return false;
	}
	
	@Override
	public void hide() {
		super.hide();
		if (selection != null) {
			hideRoadPreview();
		}
		
		if (roadConstruction) {
			Holding[] neighbours = World.getInstance().getMap().getNeighboursHolding(displayed);
			for (int i = 0; i < neighbours.length; ++i) {
				if (neighbours[i].holdingData.barony.isExplored()) {
					neighbours[i].removeColor();
				}
			}
		}
		
		roadConstruction = false;
	}
	
}
