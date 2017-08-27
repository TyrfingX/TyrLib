package com.tyrfing.games.id17.gui.holding;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.EmpireFrameListener.GameState;
import com.tyrfing.games.id17.buildings.Building;
import com.tyrfing.games.id17.gui.DefaultItemListEntry;
import com.tyrfing.games.id17.gui.MenuPoint;
import com.tyrfing.games.id17.gui.TabGUI;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.trade.Good;
import com.tyrfing.games.id17.trade.GoodProduction;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.graphics.renderables.FormattedText2.ALIGNMENT;
import com.tyrlib2.gui.DestroyOnEvent;
import com.tyrlib2.gui.ItemList;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.ScaledVector1;
import com.tyrlib2.gui.ScaledVector1.ScaleDirection;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.Window;
import com.tyrlib2.gui.WindowEvent.WindowEventType;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.input.InputManager;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Color;

public class ProductionGUI extends MenuPoint {

	public static final ScaledVector2 PROD_POS = new ScaledVector2(HoldingGUI.WINDOW_SIZE.x * 0.5f, 0.005f, 2);
	public static final ScaledVector2 PROD_SIZE = new ScaledVector2(HoldingGUI.WINDOW_SIZE.x * 0.49f, HoldingGUI.WINDOW_SIZE.y - 0.005f, 2);
	
	public static final ScaledVector2 BUILD_POS = new ScaledVector2(HoldingGUI.WINDOW_SIZE.x * 0.01f, 0.005f, 2);
	public static final ScaledVector1 BUILD_SIZEX = new ScaledVector1(HoldingGUI.WINDOW_SIZE.x * 0.52f, ScaleDirection.X, 2);
	public static final ScaledVector1 BUILD_SIZEY = new ScaledVector1(HoldingGUI.WINDOW_SIZE.y - 0.005f, ScaleDirection.Y, 2);
	
	public static final ScaledVector2 PROD_LABEL_POS 	= new ScaledVector2(0.015f, 0.04f + TabGUI.SIGIL_HOLDER_SIZE.y + 0.01f, 2);
	public static final ScaledVector2 PROD_BASE_POS 	= new ScaledVector2(PROD_LABEL_POS.x, 0 + 0.04f, 2);
	public static final float	PROD_PADDING 	= 0.01f;
	public static final ScaledVector2 PROD_ARROW_POS 	= new ScaledVector2(TabGUI.SIGIL_HOLDER_SIZE.x * 0.5f, TabGUI.SIGIL_HOLDER_SIZE.y * 0.5f, 2);
	public static final ScaledVector2 PROD_ARROW_SIZE = new ScaledVector2(TabGUI.SIGIL_HOLDER_SIZE.x * 0.4f, TabGUI.SIGIL_HOLDER_SIZE.y * 0.4f, 0);
	public static final ScaledVector2 SUPPLY_BASE_POS = new ScaledVector2(PROD_LABEL_POS.x, PROD_LABEL_POS.y + 0.05f, 2);
	public static final ScaledVector2 SUPPLY_SIZE		= new ScaledVector2(TabGUI.SIGIL_HOLDER_SIZE.x * 0.5f, TabGUI.SIGIL_HOLDER_SIZE.y * 0.5f, 2);
	
	protected Holding displayed;
	private List<Window> producedGoods = new ArrayList<Window>();
	
	private Window prodBG;
	private Window buildBG;
	
	private ItemList buildList;
	
	public ProductionGUI(Window parent) {
		String name = parent.getName();
		
		prodBG = WindowManager.getInstance().createImageBox(name + "/PROD_BG", PROD_POS, "MAIN_GUI", "PAPER", PROD_SIZE);
		parent.addChild(prodBG);
		mainElements.add(prodBG);
		
		buildBG = WindowManager.getInstance().createImageBox(name + "/BUILD_BG", BUILD_POS.get(), "MAIN_GUI", "PAPER", new Vector2( BUILD_SIZEX.get(), BUILD_SIZEY.get()));
		parent.addChild(buildBG);
		mainElements.add(buildBG);
		
		buildList = (ItemList) WindowManager.getInstance()
											.createItemList(name + "/OVERVIEW/BG_BRANCHES/FAMILY_LIST", 
															new ScaledVector2(BUILD_POS.x * 3, BUILD_POS.y * 6, 2).get(), 
															new Vector2( BUILD_SIZEX.get(), BUILD_SIZEY.get()), 
															0, 
															3);
		parent.addChild(buildList);
		buildList.setPassTouchEventsThrough(true);
		mainElements.add(buildList);
		
		Label prodLabel = (Label) WindowManager.getInstance().createLabel(name + "/PROD_LABEL", PROD_LABEL_POS, "Supplied Goods");
		prodLabel.setInheritsAlpha(true);
		prodBG.addChild(prodLabel);
		mainElements.add(prodLabel);
		prodLabel.setColor(Color.BLACK.copy());
		
		for (int i = 0; i < mainElements.size(); ++i) {
			mainElements.get(i).setAlpha(0);
		}
	}
	
	public void show(Holding holding){
		if (displayed != holding) {
			Vector2 pos = new ScaledVector2(PROD_BASE_POS).get();
			
			String name = prodBG.getName();
			
			for (int i = 0; i < holding.getCountProductions(); ++i) {
				GoodProduction production = holding.getProduction(i);
				createGoodProductionGUI(i, production, name, prodBG, producedGoods, pos, holding);
				pos.x += PROD_PADDING;
			}
			
			pos = new ScaledVector2(SUPPLY_BASE_POS).get();
			
			for (int i = 0; i < holding.getCountSuppliedGoods(); ++i) {
				Good good = holding.getSuppliedGood(i);
				Window icon = WindowManager.getInstance().createImageBox(name + "/SUPPLY_" + i, pos, "GOODS", good.getName(), SUPPLY_SIZE);
				prodBG.addChild(icon);
	
				Window frame = WindowManager.getInstance().createImageBox(name + "/SUPPLY_FRAME_" + i, new Vector2(), "MAIN_GUI", "BIG_RECT_BORDER", SUPPLY_SIZE);
				icon.addChild(frame);
				
				icon.setReceiveTouchEvents(true);
				WindowManager.getInstance().addTextTooltip(icon, good.getTooltip(holding));
				
				Label quantity = (Label) WindowManager.getInstance().createLabel(name + "/SUPPLY_LABEL"+ i, new Vector2(SUPPLY_SIZE.x/2,0), String.valueOf(good.getQuantity()));
				quantity.setAlignment(ALIGNMENT.CENTER);
				quantity.setColor(Color.WHITE);
				quantity.setInheritsAlpha(true);
				icon.addChild(quantity);
				
				icon.setAlpha(0);
				frame.setAlpha(0);
				quantity.setAlpha(0);
			
				frame.setInheritsAlpha(true);
				icon.fadeIn(1, OverviewGUI.FADE_TIME);
				
				producedGoods.add(icon);
				
				pos.x += SUPPLY_SIZE.x + PROD_PADDING/2;
			}
			
			displayed = holding;
			
			Building.TYPE[] types = Building.TYPE.values();
			
			for (int i = 0; i < types.length; ++i) {
				if (Building.isBuildableInHolding(types[i], displayed)) {
					BuildingEntry entry = new BuildingEntry(types[i], holding, this);
					entry.addEventListener(WindowEventType.FADE_OUT_FINISHED, new DestroyOnEvent());
					buildList.addItemListEntry(entry);
					
					entry.setAlpha(0);
					entry.setVisible(true);
					entry.setReceiveTouchEvents(true);
					
					if (		displayed.hasActiveProject() 
							|| 	displayed.getOwner() != World.getInstance().getPlayerController().getHouse() 
							|| 	EmpireFrameListener.state == GameState.SELECT) {
						entry.setEnabled(false);
					} else {
						entry.setEnabled(true);
					}
					
					if (buildList.getCountEntries() < 4) {
						if (buildList.getCountEntries() != 2) {
							entry.fadeIn(0.8f, 0.5f);
						} else {
							entry.fadeIn(1, 0.5f);
						}
					}
				}
			}
			
			InputManager.getInstance().sort();
			
			super.show();
		}

	}
	
	@Override
	public void hide() {
		super.hide();
		
		for (int i = 0; i < producedGoods.size(); ++i) {
			//producedGoods.get(i).fadeOut(0, OverviewGUI.FADE_TIME);
			//producedGoods.get(i).addEventListener(WindowEventType.FADE_OUT_FINISHED, new DestroyOnEvent());
			//producedGoods.get(i).setReceiveTouchEvents(false);
			
			producedGoods.get(i).destroy();
		}
		
		producedGoods.clear();
		
		for (int i = 0; i  < buildList.getCountEntries(); ++i) {
			//buildList.getEntry(i).fadeOut(0, OverviewGUI.FADE_TIME);
			buildList.getEntry(i).destroy();
		}
		
		buildList.clear();
		
		displayed = null;
	}
	
	public void unHighlightBuildings() {
		for (int i = 0; i  < buildList.getCountEntries(); ++i) {
			((DefaultItemListEntry)buildList.getEntry(i)).highlight();
			((DefaultItemListEntry)buildList.getEntry(i)).unhighlight();
		}
	}
	
	public static void createGoodProductionGUI(int index, GoodProduction production, String name, Window prodBG, List<Window> producedGoods, Vector2 pos, Holding holding) {
		for (int j = 0; j < production.getCountInputGoods(); ++j) {
			Good good = production.getInputGood(j);
			Window icon = WindowManager.getInstance().createImageBox(name + "/" + index + "/" + "/INPUT_" + j, pos, "GOODS", good.getName(), TabGUI.SIGIL_HOLDER_SIZE.get(1));
			prodBG.addChild(icon);
			
			Window frame = WindowManager.getInstance().createImageBox(name + "/" + index + "/" + "/INPUT_FRAME_" + j, new Vector2(), "MAIN_GUI", "BIG_RECT_BORDER", TabGUI.SIGIL_HOLDER_SIZE.get(1));
			icon.addChild(frame);
			
			frame.setReceiveTouchEvents(true);
			WindowManager.getInstance().addTextTooltip(frame, good.getTooltip(holding));
			
			Label quantity = (Label) WindowManager.getInstance().createLabel(name + "/" + index + "/"+  "/INPUT_LABEL" + j, new Vector2(TabGUI.SIGIL_HOLDER_SIZE.get(1).x/2, 0), String.valueOf(good.getNecessaryQuantity()));
			quantity.setInheritsAlpha(true);
			quantity.setAlignment(ALIGNMENT.CENTER);
			quantity.setColor(Color.WHITE);
			icon.addChild(quantity);
			
			Window arrow = WindowManager.getInstance().createImageBox(name + "/" + index + "/"+  "/INPUT_LABEL" + j, PROD_ARROW_POS, "GOODS", "In", PROD_ARROW_SIZE);
			icon.addChild(arrow);
			arrow.setInheritsAlpha(true);
			
			icon.setAlpha(0);
			frame.setAlpha(0);
			arrow.setAlpha(0);
			quantity.setAlpha(0);
		
			frame.setInheritsAlpha(true);
			icon.fadeIn(1, OverviewGUI.FADE_TIME);
			
			producedGoods.add(icon);
			
			pos.x += TabGUI.SIGIL_HOLDER_SIZE.get(1).x;
		}
		
		for (int j = 0; j < production.getCountOutputGoods(); ++j) {
			Good good = production.getOutputGood(j);
			Window icon = WindowManager.getInstance().createImageBox(name + "/" + index + "/"+ "/OUTPUT_" + j, pos, "GOODS", good.getName(), TabGUI.SIGIL_HOLDER_SIZE.get(1));
			prodBG.addChild(icon);

			Window frame = WindowManager.getInstance().createImageBox(name + "/" + index + "/"+ "/OUTPUT_FRAME_" + j, new Vector2(), "MAIN_GUI", "BIG_RECT_BORDER", TabGUI.SIGIL_HOLDER_SIZE.get(1));
			icon.addChild(frame);
			
			icon.setReceiveTouchEvents(true);
			WindowManager.getInstance().addTextTooltip(icon, good.getTooltip(holding));
			
			Label quantity = (Label) WindowManager.getInstance().createLabel(name + "/" + index + "/"+ "/OUTPUT_LABEL"+ j, new Vector2(TabGUI.SIGIL_HOLDER_SIZE.get(1).x/2, 0), String.valueOf(good.getQuantity()));
			quantity.setInheritsAlpha(true);
			quantity.setAlignment(ALIGNMENT.CENTER);
			quantity.setColor(Color.WHITE);
			icon.addChild(quantity);
			
			Window arrow = WindowManager.getInstance().createImageBox(name + "/" + index + "/"+ "/Output_LABEL" + j, PROD_ARROW_POS, "GOODS", "Out", PROD_ARROW_SIZE);
			icon.addChild(arrow);
			arrow.setInheritsAlpha(true);
			
			icon.setAlpha(0);
			frame.setAlpha(0);
			quantity.setAlpha(0);
			arrow.setAlpha(0);
		
			frame.setInheritsAlpha(true);
			icon.fadeIn(1, OverviewGUI.FADE_TIME);
			
			producedGoods.add(icon);
			
			pos.x += TabGUI.SIGIL_HOLDER_SIZE.get(1).x;
		}
	}

	@Override
	public void update() {
		for (int i = 0; i  < buildList.getCountEntries(); ++i) {
			BuildingEntry entry = ((BuildingEntry)buildList.getEntry(i));
			if (!Building.isBuildableInHolding(entry.type, displayed)) {
				buildList.removeItemListEntry(entry);
				--i;
				continue;
			}
			
			entry.update();
			
			if (		displayed.getOwner().getGold() < Building.getPrice(entry.type, displayed)
					||	displayed.hasActiveProject() 
					|| 	displayed.getOwner() != World.getInstance().getPlayerController().getHouse() 
					|| 	EmpireFrameListener.state == GameState.SELECT) {
				entry.setEnabled(false);
			} else {
				entry.setEnabled(true);
			}
		}
	}

}
