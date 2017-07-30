package com.tyrfing.games.id17.gui.war;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id17.war.Army;
import com.tyrfing.games.id17.war.Regiment;
import com.tyrfing.games.id17.war.UnitType;
import com.tyrlib2.graphics.renderables.FormattedText2.ALIGNMENT;
import com.tyrlib2.gui.ImageBox;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.Skin;
import com.tyrlib2.gui.Window;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Color;
import com.tyrlib2.util.Direction4;

public class FormationWindow {
	
	public static final int REGIMENTS_PER_COLUMN = 2;
	public static final ScaledVector2 ICON_SIZE_SMALL = new ScaledVector2(0.06f*0.7f, 0.0825f*0.7f,0);
	public static final ScaledVector2 ICON_SIZE_BIG = new ScaledVector2(0.075f*0.8f, 0.1035f*0.8f,0);
	private final float offsetX;
	private final float offsetY;
	
	private List<ImageBox> icons = new ArrayList<ImageBox>();
	private List<ImageBox> healths = new ArrayList<ImageBox>();
	private List<ImageBox> attacking = new ArrayList<ImageBox>();
	private List<Label> labels = new ArrayList<Label>();
	
	public static final Color GREEN = new Color(0.2f, 0.7f, 0.2f, 1);
	private static final Color YELLOW = Color.YELLOW.copy();
	private static final Color RED = Color.RED.copy();
	
	private Army army;
	
	private Vector2 basePos;
	private int direction = -1;
	
	public FormationWindow(Vector2 basePos, Window parent, String name, Direction4 orientation, Vector2 iconSize) {
		
		offsetX = iconSize.x;
		offsetY = iconSize.y;
		
		this.basePos = new Vector2(basePos);
		Vector2 iconPos = new Vector2(basePos.x, basePos.y);
		int column = 0;
		
		if (orientation == Direction4.RIGHT) {
			direction = 1;
		}
		
		for (int i = 0; i < Army.MAX_REGIMENTS; ++i) {
			ImageBox icon = (ImageBox) WindowManager.getInstance().createImageBox(	name + "/DETAIL_FORMATION" + i, 
																					iconPos,
																					"UNIT_ICONS",
																					"Empty", 
																					iconSize);
			icon.setAlpha(0.6f);
			parent.addChild(icon);
			icons.add(icon);
			
			Skin skin = WindowManager.getInstance().getSkin();
			String tmp = skin.LABEL_FONT;
			skin.LABEL_FONT = "FONT_14";
			
			Label label = (Label) WindowManager.getInstance().createLabel(name + "/DETAIL_FORMATION" + i + "/LABEL", new Vector2(iconSize.multiply(0.5f).x, iconSize.multiply(0.5f).x), "1000");
			icon.addChild(label);
			label.setAlignment(ALIGNMENT.CENTER);
			label.setMaxAlpha(0.8f);
			label.setInheritsAlpha(true);
			label.setColor(Color.WHITE);
			labels.add(label);
			
			skin.LABEL_FONT = tmp;
			
			ImageBox attack = (ImageBox) WindowManager.getInstance().createImageBox(	name + "/DETAIL_FORMATION" + i + "/ATTACK", 
																						new Vector2(),
																						"UNIT_ICONS",
																						"ATTACKER_BRUSH", 
																						iconSize);
			attacking.add(attack);
			icon.addChild(attack);
			attack.setAlpha(0);
			
			ImageBox health = (ImageBox) WindowManager.getInstance().createImageBox(	name + "/DETAIL_FORMATION" + i + "/HEALTH", 
																						new Vector2(),
																						"UNIT_ICONS",
																						"HEALTH_BRUSH_1", 
																						iconSize);
			healths.add(health);
			icon.addChild(health);
			health.setAlpha(0);
																	
			if ((i+1) % REGIMENTS_PER_COLUMN != 0) {
				iconPos.y += offsetY;
			} else {
				column++;
				iconPos.y = basePos.y;
				iconPos.x = basePos.x + column * offsetX * direction;
			}
			
		}
		

	}
	
	public ImageBox getIcon(int index) {
		return icons.get(index);
	}
	
	public void setArmy(Army army, boolean resetPos) {
		Vector2 iconPos = new Vector2(basePos.x, basePos.y);
		int column = 0;
		this.army = army;
		for (int i = 0; i < Army.MAX_REGIMENTS; ++i) {
			Regiment regiment = army.getRegiment(i);
			float attackingAlpha = attacking.get(i).getAlpha();
			if (resetPos) {
				icons.get(i).setRelativePos(iconPos);
			}
			icons.get(i).setVisible(true);
			healths.get(i).setVisible(true);
			if (regiment != null) {
				String typeName = regiment.unitType.toString();
				icons.get(i).setAtlasRegion(typeName);
				icons.get(i).setAlpha(1.0f);
				healths.get(i).setAlpha(1.0f);
				
				if (regiment.unitType != UnitType.Walls) {
					labels.get(i).setText(String.valueOf((int)regiment.troops));
				} else {
					labels.get(i).setText("");
				}
				
				float average = regiment.troops / (regiment.maxTroops+1f);
				
				if (average == 0) {
					healths.get(i).setAtlasRegion("HEALTH_BRUSH_5");
					icons.get(i).setAlpha(0.75f);
				} else if (average < 0.33f) {
					healths.get(i).getMaterial().setColor(RED);
					healths.get(i).setAtlasRegion("HEALTH_BRUSH_4");
				} else if (average < 0.5f) {
					healths.get(i).getMaterial().setColor(YELLOW);
					healths.get(i).setAtlasRegion("HEALTH_BRUSH_3");
				} else if (average < 0.85f) {
					healths.get(i).getMaterial().setColor(YELLOW);
					healths.get(i).setAtlasRegion("HEALTH_BRUSH_2");
				} else {
					healths.get(i).getMaterial().setColor(GREEN);
					healths.get(i).setAtlasRegion("HEALTH_BRUSH_1");
				}
				
				
			} else {
				icons.get(i).setAlpha(0.6f);
				icons.get(i).setAtlasRegion("Empty");
				healths.get(i).getMaterial().setColor(Color.WHITE);
				healths.get(i).setAlpha(0.6f);
				labels.get(i).setText("");
				
			}
			attacking.get(i).setAlpha(attackingAlpha);
			
			if ((i+1) % REGIMENTS_PER_COLUMN != 0) {
				iconPos.y += offsetY;
			} else {
				column++;
				iconPos.y = basePos.y;
				iconPos.x = basePos.x + column * offsetX * direction;
			}
		}
	}
	
	public Vector2 getIconPos(int formationPos) {
		float y = formationPos % REGIMENTS_PER_COLUMN;
		float x = formationPos / REGIMENTS_PER_COLUMN;
		
		y = basePos.y + offsetY * y;
		x = basePos.x + x * offsetX * direction;
		
		return new Vector2(x, y);
	}
	
	public Army getDisplayed() {
		return army;
	}
	
	public void setAttacking(int index, boolean state) {
		if (state) {
			attacking.get(index).setAlpha(0.9f);
			attacking.get(index).setAtlasRegion("ATTACKER_BRUSH");
		} else {
			attacking.get(index).setAlpha(0);
		}
	}

	public void setDefending(int index, boolean state) {
		if (state) {
			attacking.get(index).setAlpha(0.9f);
			attacking.get(index).setAtlasRegion("DEFENDER_BRUSH");
		} else {
			attacking.get(index).setAlpha(0);
		}
	}
	
	public void fadeIn(float time) {
		for (int i = 0; i < icons.size(); ++i) {
			icons.get(i).fadeIn(0.6f, time);
		}
	}
	
	public void fadeOut(float time) {
		for (int i = 0; i < icons.size(); ++i) {
			icons.get(i).fadeOut(0, time);
		}
	}
}
