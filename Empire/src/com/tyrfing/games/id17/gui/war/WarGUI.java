package com.tyrfing.games.id17.gui.war;

import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.war.War;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.graphics.renderables.FormattedText2.ALIGNMENT;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.gui.DestroyOnEvent;
import com.tyrlib2.gui.IEventListener;
import com.tyrlib2.gui.ImageBox;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.WindowEvent;
import com.tyrlib2.gui.WindowEvent.WindowEventType;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Color;

public class WarGUI {
	
	private War war;
	private House playerHouse;
	
	private ImageBox warGUIOpener;
	private Label progress;
	
	public static final ScaledVector2 WAR_GUI_OPENER_SIGIL_SIZE = new ScaledVector2(0.075f, 0.12f);
	public static final ScaledVector2 WAR_GUI_OPENER_SIGIL_POS = new ScaledVector2(1-WAR_GUI_OPENER_SIGIL_SIZE.x, -WAR_GUI_OPENER_SIGIL_SIZE.y/2);
	
	public static final ScaledVector2 WAR_GUI_OPENER_SIZE = new ScaledVector2(WAR_GUI_OPENER_SIGIL_SIZE.x*1.1f, WAR_GUI_OPENER_SIGIL_SIZE.y*1.1f,0);
	public static final ScaledVector2 WAR_GUI_OPENER_POS = new ScaledVector2(-WAR_GUI_OPENER_SIGIL_SIZE.x*0.05f, -WAR_GUI_OPENER_SIGIL_SIZE.y*0.05f);
	
	public static final ScaledVector2 WAR_GUI_OPENER_LABEL_POS = new ScaledVector2(WAR_GUI_OPENER_SIGIL_SIZE.x/2, WAR_GUI_OPENER_SIGIL_SIZE.y*0.25f, 0);
	
	public static final Color COLOR_WINNING = new Color(0.3f, 0.9f, 0.3f, 1);
	public static final Color COLOR_LOOSING = new Color(0.6f, 0.2f, 0.1f, 1);
	
	private int warID;
	private long timestamp;
	
	public WarGUI(War war, House playerHouse, final House enemyHouse) {
		this.war = war;
		this.playerHouse = playerHouse;
		this.warID = playerHouse.wars.indexOf(war) + 1;
		
		timestamp = System.currentTimeMillis();
		
		warGUIOpener = (ImageBox) WindowManager.getInstance().createImageBox("WAR_OPENER_SIGIL" + warID + "/" +timestamp, new Vector2(WAR_GUI_OPENER_SIGIL_POS.x - warID * WAR_GUI_OPENER_SIZE.get().x, WAR_GUI_OPENER_SIGIL_POS.y), "SIGILS1", enemyHouse.getSigilName(), WAR_GUI_OPENER_SIGIL_SIZE);
		WindowManager.getInstance().createImageBox("WAR_OPENER_SIGIL/BORDER" + warID, WAR_GUI_OPENER_POS, "MAIN_GUI", "SMALL_CIRCLE_BORDER", WAR_GUI_OPENER_SIZE);
		warGUIOpener.addChild(WindowManager.getInstance().getWindow("WAR_OPENER_SIGIL/BORDER" + warID));
	
		progress = (Label) WindowManager.getInstance().createLabel("WAR_OPENER_SIGIL/PROGRESS" + warID, WAR_GUI_OPENER_LABEL_POS, "+" + (int)(war.getProgress()*100) + "%");
		progress.setFont(SceneManager.getInstance().getFont("FONT_16"));
		progress.setAlignment(ALIGNMENT.CENTER);
		progress.setColor(COLOR_LOOSING);
		warGUIOpener.addChild(progress);
		
		WindowManager.getInstance().addTextTooltip(warGUIOpener, "");
		warGUIOpener.setReceiveTouchEvents(true);
		
		warGUIOpener.addEventListener(WindowEventType.TOUCH_DOWN, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				World.getInstance().getMainGUI().hideAllSubGUIs();
				World.getInstance().getMainGUI().pickerGUI.holdingGUI.hide();
				World.getInstance().getMainGUI().houseGUI.show(enemyHouse);
			}
		});
	}
	
	public void show() {
		
	}
	
	public void hide() {
		
	}
	
	public void destroy() {
		warGUIOpener.fadeOut(0, 0.5f);
		warGUIOpener.addEventListener(WindowEventType.FADE_OUT_FINISHED, new DestroyOnEvent());
		
		House playerHouse = World.getInstance().getPlayerController().getHouse();
		int countWars = playerHouse.getCountWars();
		
		for (int i = warID; i < countWars; ++i) {
			WarGUI gui = playerHouse.getWar(i).getGUI();
			if (gui != null) {
				gui.warGUIOpener.moveBy(new Vector2(-WAR_GUI_OPENER_SIZE.get().x, 0), 0.5f);
			}
		}
	}
	
	public void updateProgress() {
		int progress = Math.round(war.getProgress()*100);
		
		int factor = 1;
		
		if (war.defenders.contains(playerHouse)) {
			progress *= -1;
			factor = -1;
		}
		
		String progressStr = Util.getSignedText(progress) + "%";
		this.progress.setText(progressStr);
		
		Label tooltipWindow = (Label) WindowManager.getInstance().getWindow(warGUIOpener.getName() + "/TooltipText");
		if (tooltipWindow != null) {
			String tooltip = "Contribution +" + Math.round(war.getWarContribution(playerHouse) * 100) + "%\n" + 
							 "Progress " + Util.getFlaggedText(progressStr, progress > 0) + "\n" + 
							 "---------------";
		
			if (Math.round(war.battleProgress*100) != 0) {
				tooltip += "\nBattle " + Util.getFlaggedText(Util.getSignedText(Math.round(war.battleProgress*100*factor)) + "%", war.battleProgress*factor > 0);
			}
			
			if (Math.round(war.occupyProgress*100) != 0) {
				tooltip += "\nOccupation " + Util.getFlaggedText(Util.getSignedText(Math.round(war.occupyProgress*100*factor))+ "%", war.occupyProgress*factor > 0);
			}
			
			if (Math.round(war.pillageProgress*100) != 0) {
				tooltip += "\nPillaging " + Util.getFlaggedText(Util.getSignedText(Math.round(war.pillageProgress*100*factor))+"%", war.pillageProgress*factor > 0);
			}
		
			tooltipWindow.setText(tooltip);
		}
	}
}
