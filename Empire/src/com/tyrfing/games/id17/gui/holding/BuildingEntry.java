package com.tyrfing.games.id17.gui.holding;

import com.tyrfing.games.id17.buildings.Building;
import com.tyrfing.games.id17.gui.DefaultItemListEntry;
import com.tyrfing.games.id17.gui.war.ArmyBuilderGUI;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrlib2.gui.ImageBox;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.ScaledVector1;
import com.tyrlib2.gui.ScaledVector1.ScaleDirection;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Color;

public class BuildingEntry extends DefaultItemListEntry {

	public static final ScaledVector1 SIZE_X = new ScaledVector1(ArmyBuilderGUI.BUILD_UNITS_SIZE.x, ScaleDirection.X, 2);
	public static final ScaledVector1 SIZE_Y = new ScaledVector1(ArmyBuilderGUI.BUILD_UNITS_SIZE.y / 3, ScaleDirection.Y, 0);
	public static final ScaledVector2 NAME_LABEL = new ScaledVector2(0.05f, 0.02f, 0);
	
	public static final ScaledVector2 ICON_POS = new ScaledVector2(0.015f, 0.02f, 0);
	public static final ScaledVector2 ICON_SIZE = new ScaledVector2(0.03f, 0.04f , 0);
	
	public static final ScaledVector1 COST_LABEL_X = new ScaledVector1(SIZE_X.x * 0.65f, ScaleDirection.X, 2);
	public static final ScaledVector1 COST_ICON_POS_X = new ScaledVector1(SIZE_X.x * 0.8f, ScaleDirection.X, 2);
	public static final ScaledVector1 COST_LABEL_Y = new ScaledVector1(NAME_LABEL.y, ScaleDirection.Y, 0);
	public static final ScaledVector1 COST_ICON_POS_Y = new ScaledVector1(NAME_LABEL.y, ScaleDirection.Y, 0);
	public static final ScaledVector2 COST_ICON_SIZE = new ScaledVector2(ICON_SIZE.x, ICON_SIZE.y, 0);
	
	public Building.TYPE type;
	private ProductionGUI ui;
	private Label nameLabel;
	private Label costLabel;
	private Holding holding;
	
	public BuildingEntry(Building.TYPE type, Holding holding, ProductionGUI ui) {
		super("BuildList/" + type.toString() + "/" + Math.random() , new Vector2(SIZE_X.get(), SIZE_Y.get()), "");
		
		this.type = type;
		this.ui = ui;
		
		String name = "BuildList/" + type.toString();
		
		int level = holding.getBuildingLevel(type);
		this.holding = holding;
		
		nameLabel = (Label) WindowManager.getInstance().createLabel(name + "/NAME_LABEL", NAME_LABEL, type.toString() + " " + level);
		nameLabel.setColor(Color.BLACK.copy());
		nameLabel.setInheritsAlpha(true);
		this.addChild(nameLabel);
		
		ImageBox buildingIcon = (ImageBox) WindowManager.getInstance().createImageBox(name + "/ICON", ICON_POS, "BUILDINGS", type.toString(), ICON_SIZE);
		buildingIcon.setInheritsAlpha(true);
		addChild(buildingIcon);
		
		float cost = Building.getPrice(type, holding);
		costLabel = (Label) WindowManager.getInstance().createLabel(name + "/COST", new Vector2(COST_LABEL_X.get(), COST_LABEL_Y.get()), Integer.toString((int)cost) + "<img MAIN_GUI GOLD_ICON>");
		costLabel.setColor(Color.BLACK);
		costLabel.setInheritsAlpha(true);
		addChild(costLabel);
		
		this.addTextTooltip(Building.getDesc(type, holding));
		this.setSizeRelaxation(new Vector2(1f, 1));
	}

	public void update() {
		nameLabel.setText(type.toString() + " " + holding.getBuildingLevel(type));
		float cost = Building.getPrice(type, holding);
		costLabel.setText(Integer.toString((int)cost) + "<img MAIN_GUI GOLD_ICON>");
	}
	
	@Override
	protected void onClick() {
		if (ui.displayed != null) {
			Building.createBuildMail(type, ui.displayed);
			ui.unHighlightBuildings();
		}
	}

}
