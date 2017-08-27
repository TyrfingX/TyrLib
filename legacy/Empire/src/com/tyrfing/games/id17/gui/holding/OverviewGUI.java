package com.tyrfing.games.id17.gui.holding;

import com.tyrfing.games.id17.gui.MenuPoint;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.Window;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.util.Color;

public class OverviewGUI extends MenuPoint {
	
	public static final ScaledVector2 HOLDING_NAME_POS = new ScaledVector2(0.03f, 0.04f, 2);
	
	public static final ScaledVector2 HOLDING_INFO_POS = new ScaledVector2(0.005f, 0.004f, 2);
	public static final ScaledVector2 HOLDING_INFO_SIZE = new ScaledVector2(1.1f * HoldingGUI.WINDOW_SIZE.x / 2, HoldingGUI.WINDOW_SIZE.y * 0.99f, 2);
	
	public static final ScaledVector2 HOLDING_STATS_POS = new ScaledVector2(HOLDING_INFO_POS.x + HOLDING_INFO_SIZE.x * 0.925f, HOLDING_INFO_POS.y, 2);
	public static final ScaledVector2 HOLDING_STATS_SIZE = new ScaledVector2(0.95f * HoldingGUI.WINDOW_SIZE.x / 2, HoldingGUI.WINDOW_SIZE.y * 0.99f, 2);

	public static final float INFO_PADDING_Y = 0.05f;
	public static final ScaledVector2 INCOME_LABEL_POS = new ScaledVector2(0.03f, 0.03f, 2);
	public static final ScaledVector2 PRODUCTIVITY_LABEL_POS = new ScaledVector2(0.03f,  INCOME_LABEL_POS.y + INFO_PADDING_Y, 2);
	public static final ScaledVector2 RESEARCH_LABEL_POS = new ScaledVector2(0.03f,  INCOME_LABEL_POS.y + INFO_PADDING_Y * 2, 2);
	public static final ScaledVector2 REVOLT_RISK_LABEL_POS = new ScaledVector2(0.03f,  INCOME_LABEL_POS.y + INFO_PADDING_Y * 3, 2);
	public static final ScaledVector2 SUPPLIES_LABEL_POS = new ScaledVector2(0.03f,  INCOME_LABEL_POS.y + INFO_PADDING_Y * 4, 2);
	
	public static final float FADE_TIME = 0.5f;
	
//	private Label popBreakdown;
	
	private Label holdingName;
	private Label incomeLabel;
	private Label prodLabel;
	private Label researchLabel;
	private Label revoltLabel;
	private Label suppliesLabel;
	
	public OverviewGUI(Window parent) {
		WindowManager.getInstance().createImageBox("HOLDING/INFO", HOLDING_INFO_POS, "MAIN_GUI", "PAPER", HOLDING_INFO_SIZE);
		mainElements.add(WindowManager.getInstance().getWindow("HOLDING/INFO"));
		parent.addChild(mainElements.get(0));

		holdingName = (Label) WindowManager.getInstance().createLabel("HOLDING/NAME", HOLDING_NAME_POS, "Test, Population: 1000");
		holdingName.setColor(Color.BLACK);
		parent.addChild(WindowManager.getInstance().getWindow("HOLDING/NAME"));
		mainElements.add(holdingName);
		
		WindowManager.getInstance().createImageBox("HOLDING/STATS", HOLDING_STATS_POS, "MAIN_GUI", "PAPER", HOLDING_STATS_SIZE);
		mainElements.add(WindowManager.getInstance().getWindow("HOLDING/STATS"));
		parent.addChild(mainElements.get(2));
 
		incomeLabel = (Label) WindowManager.getInstance().createLabel("HOLDING/INCOME", INCOME_LABEL_POS, "");
		incomeLabel.setColor(Color.BLACK);
		mainElements.get(2).addChild(incomeLabel);
		mainElements.add(incomeLabel);
		
		prodLabel = (Label) WindowManager.getInstance().createLabel("HOLDING/PROD", PRODUCTIVITY_LABEL_POS, "");
		prodLabel.setColor(Color.BLACK);
		mainElements.get(2).addChild(prodLabel);
		mainElements.add(prodLabel);
		
		researchLabel = (Label) WindowManager.getInstance().createLabel("HOLDING/REASEARCH", RESEARCH_LABEL_POS, "");
		researchLabel.setColor(Color.BLACK);
		mainElements.get(2).addChild(researchLabel);
		mainElements.add(researchLabel);
		
		revoltLabel = (Label) WindowManager.getInstance().createLabel("HOLDING/REVOLT", REVOLT_RISK_LABEL_POS, "");
		revoltLabel.setColor(Color.BLACK);
		mainElements.get(2).addChild(revoltLabel);
		mainElements.add(revoltLabel);
		
		suppliesLabel = (Label) WindowManager.getInstance().createLabel("HOLDING/SUPPLIES", SUPPLIES_LABEL_POS, "");
		suppliesLabel.setColor(Color.BLACK);
		mainElements.get(2).addChild(suppliesLabel);
		mainElements.add(suppliesLabel);
		
		
//		popBreakdown = (Label) WindowManager.getInstance().createLabel("HOLDING/POP_BREAKDOWN", new Vector2(0.05f + 0.1f, 0.08f), "DEFAULT"); 
//		popBreakdown.setColor(Color.BLACK);
//		parent.addChild(popBreakdown);
		

		for (int i = 0; i < mainElements.size(); ++i) {
			mainElements.get(i).setAlpha(0);
		}
	}
	
	public void show(Holding holding) {
		update();
			
//		String breakdown = 	"Scholars:    <#3090d0>" + scholars + 
//							"\\#\nWorkers:     <#d07040>" + workers + 
//							"\\#\nPeasants:   <#804040>" + peasants + 
//							"\\#\nMerchants: <#ffff70>" + merchants + "\\#";
//		popBreakdown.setText(breakdown);
		
		super.show();
	}

	@Override
	public void update() {

	}
}
