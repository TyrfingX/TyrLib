package com.tyrfing.games.id17.gui.technology;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.gui.TabGUI;
import com.tyrfing.games.id17.gui.holding.OverviewGUI;
import com.tyrfing.games.id17.gui.war.ArmyBuilderGUI;
import com.tyrfing.games.id17.gui.war.FormationWindow;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.networking.RequestDisplayData;
import com.tyrfing.games.id17.technology.Technology;
import com.tyrfing.games.id17.technology.TechnologyTree;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.gui.IEventListener;
import com.tyrlib2.gui.ImageBox;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.Window;
import com.tyrlib2.gui.WindowEvent;
import com.tyrlib2.gui.WindowEvent.WindowEventType;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Color;

public class TechnologyGUI extends TabGUI<House> {

	private ImageBox[] icons;
	private TechArrow[][] techArrows;
	
	
	
	public TechnologyGUI() {
		super("TECH");
		
		Window bg = WindowManager.getInstance().createImageBox("TECH_TREE", OverviewGUI.HOLDING_INFO_POS, "MAIN_GUI", "PAPER2", ArmyBuilderGUI.FORMATION_SIZE);
		main.addChild(bg);
		
		TechnologyTree t = World.getInstance().techTreeSet.trees[0];
		String treeName = "TECH/" + t.name;
		icons = new ImageBox[t.techs.length];
		techArrows = new TechArrow[t.techs.length][];
		
		for (int i = 0; i < t.techs.length; ++i) {
			final Technology tech = t.techs[i];
			icons[i] = (ImageBox) WindowManager.getInstance().createImageBox(treeName + tech.name, new Vector2((tech.iconPos.get().x+0.05f) * WINDOW_SIZE.x, tech.iconPos.get().y * WINDOW_SIZE.y), "TECH", tech.name, FormationWindow.ICON_SIZE_SMALL);
			icons[i].setSizeRelaxation(new ScaledVector2(2.5f, 2.5f, 2).get());
			WindowManager.getInstance().addTextTooltip(icons[i], tech.name);
			icons[i].addEventListener(WindowEventType.TOUCH_DOWN, new IEventListener() {
				@Override
				public void onEvent(WindowEvent event) {
					TechMail.createMail(tech, displayed);
				}
			});
			
			if (tech.pre != null) {
				techArrows[i] = new TechArrow[tech.pre.length];
				for (int j = 0; j < tech.pre.length; ++j) {
					try {
					TechArrow arrow = new TechArrow(icons[tech.pre[j].ID], icons[i], bg, this, tech, tech.pre[j]);
					techArrows[i][j] = arrow;
					World.getInstance().getUpdater().addItem( arrow );
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			} else {
				techArrows[i] = new TechArrow[1];
				TechArrow arrow = new TechArrow(icons[i], bg, this, tech);
				techArrows[i][0] = arrow;
				World.getInstance().getUpdater().addItem( arrow );
			}
		}
		
		for (int i = 0; i < t.techs.length; ++i) {
			bg.addChild(icons[i]);
		}
	}
	
//_ /\ 
//:/  \)


	@Override
	public void display() {
		//sigil.setAtlasRegion(displayed.getName());
		TechnologyTree t = World.getInstance().techTreeSet.trees[0];
		
		for (int i = 0; i < t.techs.length; ++i) {
			icons[i].setReceiveTouchEvents(true);
		}
		
		for (int i = 0; i < t.techs.length; ++i) {
			boolean reqMet = true;
			if (t.techs[i].pre != null) {
				for (int j = 0; j < t.techs[i].pre.length; ++j) {
					if (!displayed.hasResearched(t.techs[i].pre[j])) {
						reqMet = false;
						break;
					}
				}
			}
			
			if (t.techs[i].pre != null) {
				if (displayed.hasResearched(t.techs[i])) {
					for (int j = 0; j < t.techs[i].pre.length; ++j) {
						techArrows[i][j].mat.setProgress(1);
					}
				} else if (displayed.techProject != null && displayed.techProject.tech == t.techs[i]) {
					for (int j = 0; j < t.techs[i].pre.length; ++j) {
						techArrows[i][j].mat.setProgress(displayed.techProject.getProgress());
					}
				} else {
					for (int j = 0; j < t.techs[i].pre.length; ++j) {
						techArrows[i][j].mat.setProgress(0);
					}
				}
			}
				
			if (reqMet) {
				icons[i].getMaterial().setColor(new Color(1, 1, 1, 1));
			} else {
				icons[i].getMaterial().setColor(new Color(0.5f, 0.5f, 0.5f, 1));
			}
		}
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isClient()) {
			EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new RequestDisplayData(RequestDisplayData.TECH_DATA, 
																						 (short) displayed.id));
		}
		
	}
	
	@Override
	public void hide() {
		super.hide();
		
		TechnologyTree t = World.getInstance().techTreeSet.trees[0];
		for (int i = 0; i < t.techs.length; ++i) {
			icons[i].setReceiveTouchEvents(false);
		}
		
	}
	
	public House getDisplayed() {
		return displayed;
	}

	@Override
	public boolean isFinished() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

}
