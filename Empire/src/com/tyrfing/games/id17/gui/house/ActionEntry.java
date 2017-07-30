package com.tyrfing.games.id17.gui.house;

import com.tyrfing.games.id17.Action;
import com.tyrfing.games.id17.gui.DefaultItemListEntry;
import com.tyrfing.games.id17.gui.war.ArmyBuilderGUI;
import com.tyrfing.games.id17.houses.House;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.ScaledVector1;
import com.tyrlib2.gui.ScaledVector1.ScaleDirection;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Color;

public class ActionEntry extends DefaultItemListEntry {

	public static final ScaledVector1 SIZE_X = new ScaledVector1(ArmyBuilderGUI.BUILD_UNITS_SIZE.x, ScaleDirection.X, 2);
	public static final ScaledVector1 SIZE_Y = new ScaledVector1(ArmyBuilderGUI.BUILD_UNITS_SIZE.y / 3, ScaleDirection.Y, 0);
	public static final ScaledVector2 NAME_LABEL = new ScaledVector2(0.05f, 0.02f, 0);

	private Label text;
	private Action action;
	
	private House sender;
	private House receiver;
	
	public ActionEntry(Action action, ActionGUI ui) {
		super(action.getName(), new Vector2(SIZE_X.get(), SIZE_Y.get()), action.getDisabledText());
		
		String name = action.getName();
		
		text = (Label) WindowManager.getInstance().createLabel("DIPLO_CATEGORY/" + name + "/NAME", NAME_LABEL, name);
		text.setColor(Color.BLACK);
		text.setInheritsAlpha(true);
		addChild(text);
		
		this.action = action;
	}
	
	@Override
	protected void onClick() {
		action.selectedByUser(sender, receiver);
		activated = true;
		this.unhighlight();
	} 
	
	public void setSender(House sender) {
		this.sender = sender;
	}
	
	public void setReceiver(House receiver) {
		this.receiver = receiver;
	}

}
