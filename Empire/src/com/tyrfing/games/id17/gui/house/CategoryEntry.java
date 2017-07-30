package com.tyrfing.games.id17.gui.house;

import com.tyrfing.games.id17.ActionCategory;
import com.tyrfing.games.id17.gui.DefaultItemListEntry;
import com.tyrfing.games.id17.gui.war.ArmyBuilderGUI;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.ScaledVector1;
import com.tyrlib2.gui.ScaledVector1.ScaleDirection;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Color;

public class CategoryEntry extends DefaultItemListEntry {

	public static final ScaledVector1 SIZE_X = new ScaledVector1(ArmyBuilderGUI.BUILD_UNITS_SIZE.x * 0.8f, ScaleDirection.X, 2);
	public static final ScaledVector1 SIZE_Y = new ScaledVector1(ArmyBuilderGUI.BUILD_UNITS_SIZE.y / 3, ScaleDirection.Y, 0);
	public static final ScaledVector2 NAME_LABEL = new ScaledVector2(0.05f, 0.02f);

	private Label text;
	
	private ActionGUI ui;
	public final ActionCategory category;
	
	public CategoryEntry(ActionCategory category, ActionGUI ui) {
		super(category.getName(), new Vector2(SIZE_X.get(), SIZE_Y.get()), "");
		
		String name = category.getName();
		
		text = (Label) WindowManager.getInstance().createLabel("DIPLO_CATEGORY/" + name + "/NAME", NAME_LABEL, name);
		text.setColor(Color.BLACK);
		text.setInheritsAlpha(true);
		addChild(text);
		
		this.ui = ui;
		this.category = category;
	}

	@Override
	protected void onClick() {
		ui.hideActions();
		highlight();
		ui.displayActions(category);
	}

}
