package com.tyrfing.games.id17.gui.house;

import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.diplomacy.actions.Rivalize;
import com.tyrfing.games.id17.gui.DefaultItemListEntry;
import com.tyrfing.games.id17.gui.mails.JustificationEntry;
import com.tyrfing.games.id17.gui.war.ArmyBuilderGUI;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.StatModifier;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.ScaledVector1;
import com.tyrlib2.gui.ScaledVector1.ScaleDirection;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.Window;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.math.Vector2;

public class HouseEntry extends DefaultItemListEntry {

	public static final ScaledVector1 SIZE_X = new ScaledVector1(HouseOverviewGUI.BG_BRANCH_SIZE.x * 0.9f, ScaleDirection.X, 2);
	public static final ScaledVector1 SIZE_Y = new ScaledVector1(ArmyBuilderGUI.BUILD_UNITS_SIZE.y / 3, ScaleDirection.Y, 0);
	public static final ScaledVector2 OPINION_POS = new ScaledVector2(SIZE_X.x * 0.7f, JustificationEntry.NAME_LABEL.y, 1);
	
	private House house1;
	private House house2;
	private boolean flipped;
	
	private int countTraits;
	
	public HouseEntry(House house1, House house2, HouseOverviewGUI ui, boolean flipped, BG_TYPE type) {
		super(Math.random() + house1.getName(), new Vector2(SIZE_X.get(), SIZE_Y.get()), "", type);
		this.house1 = house1;
		this.house2 = house2;
		this.flipped = flipped;
		
		Window sigil = WindowManager.getInstance().createImageBox(this.getName() + "/SIGIL", JustificationEntry.SIGIL_POS, "SIGILS1", flipped ? house2.getSigilName() : house1.getSigilName(), JustificationEntry.SIGIL_SIZE);
		addChild(sigil);
		sigil.setReceiveTouchEvents(false);
		sigil.setPassTouchEventsThrough(true);
		sigil.setInheritsAlpha(true);
		
		float relation = ui.displayed == house1 ? house1.getRelation(house2) : house2.getRelation(house1);
		Label relationLabel = (Label) WindowManager.getInstance().createLabel(this.getName() + "/RELATION", OPINION_POS, Util.getFlaggedText(String.valueOf((int)relation), relation >= 0));
		
		relationLabel.setInheritsAlpha(true);
		addChild(relationLabel);
		
		if (house1.getHouseStat(house2, House.HAS_MARRIAGE) == 1) {
			this.addTrait("Married");
		}
		
		if (image != null) {
			WindowManager.getInstance().addTextTooltip(this, getRelationText());
		} else {
			WindowManager.getInstance().addTextTooltip(rect, getRelationText());
			rect.setReceiveTouchEvents(true);
			rect.setPassTouchEventsThrough(true);
		}
	}
	
	public HouseEntry(House house, HouseOverviewGUI ui) {
		this(house, house.getOverlord(), ui, false, BG_TYPE.IMAGE);
	}

	@Override
	protected void onClick() {
		World.getInstance().getMainGUI().houseGUI.show(flipped ? house2 : house1);
	}
	
	public void addTrait(String name) {
		Window trait = WindowManager.getInstance().createImageBox(this.getName() + "/TRAIT" + countTraits, JustificationEntry.SIGIL_POS.add(new ScaledVector2(JustificationEntry.SIGIL_SIZE.x * 1.1f * (countTraits+1), 0)), "TRAITS", name, JustificationEntry.SIGIL_SIZE);
		trait.setReceiveTouchEvents(false);
		trait.setPassTouchEventsThrough(true);
		trait.setInheritsAlpha(true);
		addChild(trait);
		countTraits++;
	}
	
	public String getRelationText() {
		
		String text = "";
		boolean first = true;
		
		for (int i = 0; i < house1.getCountStatModifiers(); ++i) {
			StatModifier s = house1.getStatModifier(i);
			if (s.target == house2.id && s.stat == House.RELATION_STAT && s.value != 0) {
				if (!first) {
					text += "\n";
				} else {
					first = false;
				}
				text += " " + Util.getFlaggedText(s.name + " " + (int)s.value, s.value > 0) + " ";
			}
		}
	
		if (house2 == house1.getOverlord() && house1.getLawRelation() != 0) {
			if (!first) {
				text += "\n";
			} else {
				first = false;
			}
			text += " " + Util.getFlaggedText("Laws " + (int)house1.getLawRelation(), house1.getLawRelation() > 0) + " ";
		}
		
		if (house1.stats[House.DIPLOMATIC_REPUTATION] != 0) {
			if (!first) {
				text += "\n";
			} else {
				first = false;
			}
			text += " " + Util.getFlaggedText("Reputation " + house1.getName() + " " + (int)house1.stats[House.DIPLOMATIC_REPUTATION], house1.stats[House.DIPLOMATIC_REPUTATION] > 0) + " ";
		}
		
		if (house2.stats[House.DIPLOMATIC_REPUTATION] != 0) {
			if (!first) {
				text += "\n";
			} else {
				first = false;
			}
			text += " " + Util.getFlaggedText("Reputation " + house2.getName() + " " + (int)house2.stats[House.DIPLOMATIC_REPUTATION], house2.stats[House.DIPLOMATIC_REPUTATION] > 0) + " ";
		}
		

		int rivalry = 0;
		
		if (house1.getRival() == house2) {
			rivalry += Rivalize.RELATION_HIT;
		}
		
		if (house2.getRival() == house1) {
			rivalry += Rivalize.RELATION_HIT;
		}
		
		if (rivalry != 0) {
			if (!first) {
				text += "\n";
			}
			text +=  Util.getFlaggedText(" Rivalry " + rivalry, false);
		}
		
		return text;
	}

}
