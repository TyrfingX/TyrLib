package com.tyrfing.games.id17.save;

import java.util.List;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.WikiReader;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.holdings.HoldingTypes;
import com.tyrfing.games.id17.world.Ocean;
import com.tyrfing.games.id17.world.World;
import com.tyrfing.games.id17.world.WorldChunk;
import com.tyrfing.games.id17.world.WorldFactory;
import com.tyrfing.games.id17.world.WorldMap;
import com.tyrlib2.game.ILink;
import com.tyrlib2.game.LinkManager;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.math.Vector3;

public abstract class WorldFactoryFromFile implements WorldFactory {
	protected void init() {
		WorldChunk.sInit();
	}
	
	protected void fillObjectType(int id) {
		for (int i = 0; i < World.getInstance().getCountBaronies(); ++i) {
			Barony barony = World.getInstance().getBarony(i);
			barony.getWorldChunk().fillInObjects(id);
		}
	}
	
	protected void initWorldMap() {
		WorldMap map =  World.getInstance().getMap();
		map.constructEdgeList();
		map.constructShortestPaths();	
		
		if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
			map.constructWaterfalls();
			map.constructWaterFlow();
			map.getFogMap().init();
		}
	}
	
	protected void fillInRenderables() {
		World world = World.getInstance();
		
		fillObjectType(0);
		fillObjectType(1);
		fillObjectType(6);
		fillObjectType(9);
		fillObjectType(2);
		fillObjectType(3);
		fillObjectType(4);
		fillObjectType(5);
		fillObjectType(7);
		fillObjectType(11);
		fillObjectType(8);
		fillObjectType(10);
		
		for (int i = 0; i < world.getCountBaronies(); ++i) {
			Barony barony =  world.getBarony(i);
			for (int j = 0; j < barony.getWorldChunk().blockTypes.length; ++j) {
				if (j != 6) {
					barony.getWorldChunk().fillInChunks(j);
				}
			}
		}
		
		for (int j = 0; j < 2; ++j) {
			for (int i = 0; i < World.getInstance().getCountBaronies(); ++i) {
				Barony barony = World.getInstance().getBarony(i);
				barony.getWorldChunk().fillInGrass(j);
			}
		}
		
		
		for (int i = 0; i < world.getCountBaronies(); ++i) {
			Barony barony =  world.getBarony(i);
			barony.getWorldChunk().fillInChunks(6);
		}
		
		for (int i = 0; i < world.getCountBaronies(); ++i) {
			Barony barony =	 world.getBarony(i);
			barony.getWorldChunk().getBorder().setMainCoords(world.getMap().getVisibleBorders(barony));
			barony.getWorldChunk().getBorder().rebuild();
			SceneManager.getInstance().getRenderer().addRenderable(barony.getWorldChunk().getBorder());
		}
		
		Ocean ocean = new Ocean(world.getMap());
		SceneManager.getInstance().getRenderer().addRenderable(ocean);
	}
	
	protected void setupWiki() {
		
		// Cycle through types of the specified holding
		for (final String holdingType : HoldingTypes.holdingStats.keySet()) {
			LinkManager.getInstance().registerLink(new ILink() {
				int holdingFocus = 0;
				@Override
				public void onCall() {
					Holding h = nextHolding();
					if (h == null) return;
					Vector3 pos = h.getHoldingData().worldEntity.getParent().getCachedAbsolutePos();
					EmpireFrameListener.MAIN_FRAME.camController.focus(pos);
				}
				
				private Holding nextHolding() {
					List<Holding> holdings = World.getInstance().getPlayerController().getHouse().getHoldings();
					if (holdings.size() == 0) return null ;
					int oldFocus = holdingFocus;
					Holding h = holdings.get(holdingFocus);
					holdingFocus = (holdingFocus+1) % holdings.size();
					while (!h.holdingData.typeName.equals(holdingType)) {
						if (oldFocus == holdingFocus) return null;
						h = holdings.get(holdingFocus);
						holdingFocus = (holdingFocus+1) % holdings.size();
					}
					return h;
				}
				
			}, "Cycle" + holdingType);
		}
		
		for (final Holding holding : World.getInstance().getHoldings()) {
			LinkManager.getInstance().registerLink(new ILink() {
				@Override
				public void onCall() {
					Vector3 pos = holding.getHoldingData().worldEntity.getParent().getCachedAbsolutePos();
					EmpireFrameListener.MAIN_FRAME.camController.focus(pos);
					World.getInstance().getMainGUI().hideAllSubGUIs();
					World.getInstance().getMainGUI().pickerGUI.holdingGUI.show(holding);
				}
				
			}, holding.getFullName());
		}
		
		
		WikiReader reader = new WikiReader();
		reader.read("help/wiki.xml");
	}
}
