package com.tyrfing.games.id17.gui.war;

import java.util.List;

import com.tyrfing.games.id17.gui.holding.HoldingGUI;
import com.tyrfing.games.id17.gui.holding.OverviewGUI;
import com.tyrfing.games.id17.war.Army;
import com.tyrfing.games.id17.war.Battle;
import com.tyrfing.games.id17.war.Regiment;
import com.tyrfing.games.id17.war.Skirmish;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.game.IUpdateable;
import com.tyrlib2.graphics.renderables.FormattedText2.ALIGNMENT;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.gui.ImageBox;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.Window;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Color;
import com.tyrlib2.util.Direction4;

public class BattleGUI implements IUpdateable {
	
	public static final Vector2 ATTACKER_FORMATION_POS =  new Vector2(OverviewGUI.HOLDING_INFO_POS.x + ArmyGUI.FORMATION_SIZE.x * 0.5f, OverviewGUI.HOLDING_NAME_POS.y * 2.5f );
	public static final Vector2 DEFENDER_FORMATION_POS =  new Vector2(OverviewGUI.HOLDING_INFO_POS.x + ArmyGUI.FORMATION_SIZE.x * 1.5f, OverviewGUI.HOLDING_NAME_POS.y * 2.5f );
	
	private FormationWindow formationWindowAttacker;
	private FormationWindow formationWindowDefender;
	
	private boolean redisplay = false;
	private boolean hiding = false;
	
	private Battle displayed = null;
	private Window main;
	
	private Window infoBox;
	private Label attackerName;
	private Label defenderName;
	private Label phaseLabel;
	
	public static final ScaledVector2 INFO_BOX_SIZE = new ScaledVector2(HoldingGUI.WINDOW_SIZE.x* 0.98f, HoldingGUI.WINDOW_SIZE.y * 1,2);
	public static final ScaledVector2 INFO_BOX_POS = new ScaledVector2(HoldingGUI.WINDOW_SIZE.x*0.01f, 0,2);
	
	public static final ScaledVector2 ATTACKER_NAME_POS = new ScaledVector2(ATTACKER_FORMATION_POS.x, 0.03f, 2);
	public static final ScaledVector2 DEFENDER_NAME_POS = new ScaledVector2(DEFENDER_FORMATION_POS.x + FormationWindow.ICON_SIZE_SMALL.x, 0.03f, 2);
	
	public static final ScaledVector2 PHASE_LABEL_POS = new ScaledVector2(HoldingGUI.WINDOW_SIZE.x / 2, 0.03f, 2);
	
	public BattleGUI(Window main) {
		this.main = main;
		
		infoBox = WindowManager.getInstance().createImageBox("BATTLE/INFO_BOX",  INFO_BOX_POS, "MAIN_GUI", "PAPER2",  INFO_BOX_SIZE);
		main.addChild(infoBox);
		
		formationWindowDefender = new FormationWindow(DEFENDER_FORMATION_POS, infoBox, infoBox.getName() + "/Defender", Direction4.RIGHT, FormationWindow.ICON_SIZE_SMALL.get());
		formationWindowAttacker = new FormationWindow(ATTACKER_FORMATION_POS, infoBox, infoBox.getName() + "/Attacker", Direction4.LEFT, FormationWindow.ICON_SIZE_SMALL.get());
		
		attackerName = (Label) WindowManager.getInstance().createLabel("BATTLE/INFO_BOX/ATTACKER_NAME", ATTACKER_NAME_POS, "");
		infoBox.addChild(attackerName);
		attackerName.setAlignment(ALIGNMENT.CENTER);
		attackerName.setFont(SceneManager.getInstance().getFont("FONT_16"));
		attackerName.setColor(Color.BLACK);
		
		defenderName = (Label) WindowManager.getInstance().createLabel("BATTLE/INFO_BOX/DEFENDER_NAME", DEFENDER_NAME_POS, "");
		infoBox.addChild(defenderName);
		defenderName.setAlignment(ALIGNMENT.CENTER);
		defenderName.setFont(SceneManager.getInstance().getFont("FONT_16"));
		defenderName.setColor(Color.BLACK);
		
		phaseLabel = (Label) WindowManager.getInstance().createLabel("BATTLE/INFO_BOX/PHASE_LABEL", PHASE_LABEL_POS, "");
		infoBox.addChild(phaseLabel);
		phaseLabel.setAlignment(ALIGNMENT.CENTER);
		phaseLabel.setFont(SceneManager.getInstance().getFont("FONT_16"));
		phaseLabel.setColor(Color.BLACK);
		
		main.setVisible(false);
	}
	
	public void show(Battle battle) {
		
		main.setVisible(true);
		
		if (displayed == null) {
			World.getInstance().getUpdater().addItem(this);
			displayed = battle;
			displayBattle();
		} else if (displayed != battle) {
			displayed = battle;
		}
		
	}
	
	public void hide() {
		if (main.isVisible()) {
			World.getInstance().getMainGUI().pickerGUI.unhighlight();
			World.getInstance().getUpdater().removeItem(this);
			World.getInstance().getMainGUI().pickerGUI.pickedArmy = null;
			
			if (displayed != null) {
				displayed.setView(null);
			}
		}
	}
	
	public void displayBattle() {
		formationWindowAttacker.setArmy(displayed.getAttacker(), true);
		formationWindowDefender.setArmy(displayed.getDefender(), true);
		displayed.setView(this);
		
		attackerName.setText(displayed.getAttacker().getOwner().getName());
		defenderName.setText(displayed.getDefender().getOwner().getName());
	}

	public void displayResult(Army winner) {
		if (winner.getOwner().getController() == World.getInstance().getPlayerController()) {
			phaseLabel.setText("VICTORY!");
		} else {
			phaseLabel.setText("DEFEAT!");
		}
	}
	
	@Override
	public void onUpdate(float time) {
		if (displayed != null) {
			formationWindowAttacker.setArmy(displayed.getAttacker(), false);
			formationWindowDefender.setArmy(displayed.getDefender(), false);
		}
	}

	@Override
	public boolean isFinished() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void displaySkirmishs(List<Skirmish> skirmishs) {
		if (displayed != null) {
			if (skirmishs.size() > 0) { 
				FormationWindow formationWindowAttacker = this.formationWindowAttacker;
				FormationWindow formationWindowDefender = this.formationWindowDefender;
				int direction = -1;
				
				if (displayed.getPhase().isDefPhase()) {
					formationWindowAttacker = this.formationWindowDefender;
					formationWindowDefender = this.formationWindowAttacker;
					direction = 1;
				}
				
				for (int i = 0; i < skirmishs.size(); ++i) {
					Skirmish skirmish = skirmishs.get(i);
					Regiment mainDefender = skirmish.defenders.get(0);
					int mainDefenderFormationPos = mainDefender.formationPos;
					ImageBox iconDefender = formationWindowDefender.getIcon(mainDefenderFormationPos);
					Vector2 targetPos = iconDefender.getRelativePos();
					targetPos.x += direction * iconDefender.getSize().x;
					
					for (int j = 0; j < skirmish.attackers.size(); ++j) {
						int formationPos = skirmish.attackers.get(j).formationPos;
						ImageBox icon = formationWindowAttacker.getIcon(formationPos);
						targetPos.y = icon.getRelativePos().y;
						icon.moveTo(targetPos, Battle.PHASE_PREP);
					}
				}
			}
			
			highlightAttackers(skirmishs);
		}
	}
	
	public void displayRegimentRetreat(List<Skirmish> skirmishs) {
		
		if (displayed != null) {
			if (skirmishs.size() > 0) { 
				FormationWindow formationWindowAttacker = this.formationWindowAttacker;
				
				if (displayed.getPhase().isDefPhase()) {
					formationWindowAttacker = this.formationWindowDefender;
				}
				
				for (int i = 0; i < skirmishs.size(); ++i) {
					Skirmish skirmish = skirmishs.get(i);				
					for (int j = 0; j < skirmish.attackers.size(); ++j) {
						int formationPos = skirmish.attackers.get(j).formationPos;
						ImageBox icon = formationWindowAttacker.getIcon(formationPos);
						icon.moveTo(formationWindowAttacker.getIconPos(formationPos), Battle.PHASE_PREP);
					}
				}
			}
		}
		
		unhighlightAttackers();
	}
	
	public void displayFlanking(List<Skirmish> skirmishs) {
		if (skirmishs.size() > 0) { 
			FormationWindow formationWindowAttacker = this.formationWindowAttacker;
			FormationWindow formationWindowDefender = this.formationWindowDefender;
			int direction = 1;
			
			if (displayed.getPhase().isDefPhase()) {
				formationWindowAttacker = this.formationWindowDefender;
				formationWindowDefender = this.formationWindowAttacker;
			}
			
			if (skirmishs.get(0).defenders.get(0).formationPos % 2 == 1) {
				direction = -1;
			}
			
			for (int i = 0; i < skirmishs.size(); ++i) {
				Skirmish skirmish = skirmishs.get(i);
				Regiment mainDefender = skirmish.defenders.get(0);
				int mainDefenderFormationPos = mainDefender.formationPos;
				ImageBox iconDefender = formationWindowDefender.getIcon(mainDefenderFormationPos);
				Vector2 targetPos = iconDefender.getRelativePos();
				targetPos.y -= direction * iconDefender.getSize().y;
				
				for (int j = 0; j < skirmish.attackers.size(); ++j) {
					int formationPos = skirmish.attackers.get(j).formationPos;
					ImageBox icon = formationWindowAttacker.getIcon(formationPos);
					icon.moveTo(targetPos, Battle.PHASE_PREP);
				}
			}
		}
		
		highlightAttackers(skirmishs);
	}
	
	public void displayPhase() {
		if (displayed != null) {
			phaseLabel.setText(displayed.getPhase().displayName() + "!");
		}
	}
	
	public void highlightAttackers(List<Skirmish> skirmishs) {
		
		if (hiding || redisplay || displayed == null) return;
		
		FormationWindow formationWindowAttacker = this.formationWindowAttacker;
		FormationWindow formationWindowDefender = this.formationWindowDefender;
		
		if (displayed.getPhase().isDefPhase()) {
			formationWindowAttacker = this.formationWindowDefender;
			formationWindowDefender = this.formationWindowAttacker;
		}
		
		for (int i = 0; i < skirmishs.size(); ++i) {
			Skirmish skirmish = skirmishs.get(i);
			for (int j = 0; j < skirmish.attackers.size(); ++j) {
				formationWindowAttacker.setAttacking(skirmish.attackers.get(j).formationPos, true);
			}
			for (int j = 0; j < skirmish.defenders.size(); ++j) {
				formationWindowDefender.setDefending(skirmish.defenders.get(j).formationPos, true);
			}
		}
	}
	
	private void unhighlightAttackers() {
		unhighlightAttackers(formationWindowAttacker);
		unhighlightAttackers(formationWindowDefender);
	}
	
	private void unhighlightAttackers(FormationWindow window) {
		for (int i = 0; i < Army.MAX_REGIMENTS; ++i) {
			window.setAttacking(i, false);
		}
	}
	
	public void updateFormation() {
		if (displayed != null) {
			formationWindowAttacker.setArmy(displayed.getAttacker(), false);
			formationWindowDefender.setArmy(displayed.getDefender(), false);
		}
	}
}
