package com.tyrfing.games.id17.gui.mails;

import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.gui.TabGUI;
import com.tyrfing.games.id17.war.Army;
import com.tyrfing.games.id17.war.Battle;
import com.tyrfing.games.id17.war.Regiment;
import com.tyrfing.games.id17.war.UnitType;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.gui.ImageBox;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.Window;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.util.Color;

public class BattleMail extends HeaderedMail {
	
	public static final ScaledVector2 TOTAL_LABEL_POS = new ScaledVector2(0.025f, 0.035f);
	
	public static final ScaledVector2 LOSSES_BASE_POS = new ScaledVector2(TOTAL_LABEL_POS.x, TOTAL_LABEL_POS.y + 0.11f);
	public static final float 	PADDING_X = ATTACKER_PAPER_SIZE.x * 0.4f;
	public static final float 	PADDING_Y = 0.05f;
	public static final int 	TYPES_PER_LINE = 2;
	public static final ScaledVector2 ICON_SIZE = new ScaledVector2(TabGUI.SIGIL_SIZE.x / 2, TabGUI.SIGIL_SIZE.y / 2);
	public static final ScaledVector2	LABEL_PADDING = new ScaledVector2(ICON_SIZE.x, 0);
	
	private Battle battle;
	private String result;
	
	public BattleMail(String name, Battle battle) {
		super(name);
		
		this.battle = battle;
		this.iconName = "Siege";
		
		createFrameContent();
		createButtons();
	}
	
	private void createFrameContent() {
		
		int warprogress = (int) (battle.getWarProgress() * 100);
	
		result = "VICTORY!";
		
		if (battle.getWinner().getOwner().getController() != World.getInstance().getPlayerController()) {
			result = "DEFEAT!";
			if (warprogress > 0) {
				warprogress *= -1;
			}
		} else {
			if (warprogress < 0) {
				warprogress *= -1;
			}
		}
		
		String msg = result + " " + Util.getSignedText(warprogress) + "% Warscore!";
		
		createHeaderedContent(battle.getAttacker().getOwner().getSigilName(), battle.getDefender().getOwner().getSigilName(), false, "Battle of " + battle.getPlace().getLinkedName() + " " + World.getInstance().getDate() + "\n"+ msg);
		
		createStatistics(battle.getAttacker(), left);
		createStatistics(battle.getDefender(), right);
	}
	
	private void createStatistics(Army army, Window holder) {
		
		String name = holder.getName();
		Label total = (Label) WindowManager.getInstance().createLabel(name + "/TOTAL_LABEL", TOTAL_LABEL_POS, "Remaining: " + army.getTotalTroops() + "\nLosses: <#ff0000>" + battle.getLosses(army));
		total.setColor(Color.BLACK.copy());
		total.setInheritsAlpha(true);
		holder.addChild(total);
		
		UnitType[] types = UnitType.values();
		int[] losses = new int[types.length];
		
		for (int i = 0; i < Army.MAX_REGIMENTS; ++i) {
			Regiment r = army.getRegiment(i);
			if (r != null) {
				int lossCount = battle.getLosses(army, i);
				losses[r.unitType.ordinal()] += lossCount;
			}
		}
		
		ScaledVector2 pos = new ScaledVector2(LOSSES_BASE_POS);
		int j = 0;
		
		for (int i = 0; i < types.length; ++i) {
			if (losses[i] > 0) {
				ImageBox icon = (ImageBox) WindowManager.getInstance().createImageBox(name + "/ICON" + i, new ScaledVector2(pos), "UNIT_ICONS", types[i].name(), ICON_SIZE);
				holder.addChild(icon);
				icon.setInheritsAlpha(true);
				
				Label count = (Label) WindowManager.getInstance().createLabel(name + "/COUNT" + i, pos.add(LABEL_PADDING), String.valueOf(losses[i]));
				holder.addChild(count);
				count.setColor(Color.RED.copy());
				count.setInheritsAlpha(true);
				
				if (j % TYPES_PER_LINE == 0) {
					pos.x += PADDING_X;
				} else {
					pos.y += PADDING_Y;
					pos.x -= PADDING_X;
				}
				
				++j;
			}
		}
		
	}
	
	@Override
	public String getTooltipText() {
		if (result != null) {
			return result;
		} else {
			return super.getTooltipText();
		}
	}

}
