package com.tyrfing.games.id17.save;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.holdings.XMLBaronySetFactory;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.XMLHouseSetFactory;
import com.tyrfing.games.id17.world.MapFile;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.game.ILink;
import com.tyrlib2.game.LinkManager;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.scene.SceneManager;

public class WorldFactoryMapFile extends WorldFactoryFromFile {

	private String mapName;
	
	public WorldFactoryMapFile(String mapName) {
		this.mapName = mapName;
	}

	@Override
	public World create() {
		
		System.out.println("(Executing WorldFactoryMapFile)");
		
		init();
		
		System.out.println("(Initialized World Construction Tools)");
		
		MapFile mapFile = new MapFile(mapName);
		
		World world = new World(mapFile);
		
		XMLBaronySetFactory baronySetFactory = new XMLBaronySetFactory(mapFile);
		baronySetFactory.create();
		
		XMLHouseSetFactory houseSetFactory = new XMLHouseSetFactory(mapFile.houseData);
		houseSetFactory.create();

		System.out.println("(Loaded World Data)");
		
		if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
			for (int i = 0; i < World.getInstance().getCountBaronies(); ++i) {
				Barony barony = World.getInstance().getBarony(i);
				barony.getWorldChunk().build();
			}
		}
		
		System.out.println("(Built World Chunks)");
		
		initWorldMap();
		
		world.finishBuild();
		world.checkProductions();
		
		System.out.println("(Built World Map)");
		
		if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
			fillInRenderables();
		}
		
		System.out.println("(Dealt with Renderables)");
		
		for (int i = 0; i < world.getHouses().size(); ++i) {
			final House house = world.getHouses().get(i);
			house.updateNeighbours();
			World.getInstance().getUpdater().addItem(house);
			
			if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
				LinkManager.getInstance().registerLink(new ILink() {
					@Override
					public void onCall() {
						if (house.getHoldings().size() > 0) {
							EmpireFrameListener.MAIN_FRAME.camController.focus(house.getHoldings().get(0).holdingData.worldEntity.getParent());
							World.getInstance().getMainGUI().hideAllSubGUIs();
							World.getInstance().getMainGUI().pickerGUI.holdingGUI.hide();
							World.getInstance().getMainGUI().houseGUI.show(house);
						}
					}
				}, house.getName());
			}
		}
		
		System.out.println("(Setup family structures)");
		
		if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
			setupWiki();
			if (SceneManager.getInstance().getRenderer().getOctree(OpenGLRenderer.DEFAULT_CHANNEL).checkDuplicates()) {
				throw new RuntimeException("Duplicate in Octree detected!");
			}
		}
		
		System.out.println("(Completed Sanity Checks)");
		
		return world;
	}
	

}
