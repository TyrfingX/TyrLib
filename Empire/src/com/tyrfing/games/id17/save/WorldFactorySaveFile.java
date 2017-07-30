package com.tyrfing.games.id17.save;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.ai.AIController;
import com.tyrfing.games.id17.ai.AIThread;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.war.Army;
import com.tyrfing.games.id17.world.World;
import com.tyrfing.games.id17.world.WorldMap;
import com.tyrlib2.game.ILink;
import com.tyrlib2.game.LinkManager;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.scene.SceneManager;

public class WorldFactorySaveFile extends WorldFactoryFromFile {

	private String saveName;
	
	public WorldFactorySaveFile(String saveName) {
		this.saveName = saveName;
	}
	
	@Override
	public World create() {
		
		init();
		
		World world = World.loadFrom("save");
		
		EmpireFrameListener.MAIN_FRAME.loadWorld(world.getMapFile().mapName);
		
		WorldMap map = world.getMap();
		map.createFogmap();
		map.createArrays();	
		
		for (int i = 0; i < World.getInstance().getCountBaronies(); ++i) {
			Barony barony = World.getInstance().getBarony(i);
			barony.build();
		}
		
		for (int i = 0; i < world.getHouses().size(); ++i) {
			House house = World.getInstance().getHouses().get(i);
			house.holdings = new ArrayList<Holding>();
			house.baronies = new ArrayList<Barony>();
			house.subHouses = new ArrayList<House>();
			house.neighbours = new LinkedHashSet<Barony>();
			house.houseNeighbours = new ArrayList<House>();
			house.supremeOverlord = house;
			for (int j = 0; j < house.holdingIDs.size(); ++j){
				Integer holdingID = house.holdingIDs.get(j);
				Holding h = World.getInstance().getHolding(holdingID);
				house.holdings.add(h);
				if (h instanceof Barony) {
					Barony b = (Barony) h;
					house.baronies.add(b);
					h.holdingData.worldEntity = b.getWorldChunk().getCastleEntity();
				} else {
					h.holdingData.worldEntity = h.holdingData.barony.getWorldChunk().getMainObject(h.holdingData.objectNo);
				}
				h.initOffsets();

				world.getUpdater().addItem(h);
				world.addHoldingEntity(h);
				h.controleBy(house);
			}
		}
		
		for (int i = 0; i < world.getHouses().size(); ++i) {
			map.insertHouse(world.getHouses().get(i));
		}
		
		world.finishBuild();
		
		initWorldMap();
		
		for (int i = 0; i < world.getHouses().size(); ++i) {
			House house = world.getHouses().get(i);
			
			if (house.getOverlord() != null) {
				house.getOverlord().addSubHouse(house);
			}
		}
		
		AIThread.create();
		
		for (int i = 0; i < world.getHouses().size(); ++i) {
			final House house = World.getInstance().getHouses().get(i);
			
			if (house.intrigueProject != null) {
				world.getUpdater().addItem(house.intrigueProject);
			}
			
			if (house.getController() instanceof AIController) {
				AIThread.getInstance().addAI((AIController)house.getController());
			} 
			

			house.updateNeighbours();
			
			LinkManager.getInstance().registerLink(new ILink() {
				@Override
				public void onCall() {
					EmpireFrameListener.MAIN_FRAME.camController.focus(house.getHoldings().get(0).holdingData.worldEntity.getParent());
					World.getInstance().getMainGUI().hideAllSubGUIs();
					World.getInstance().getMainGUI().pickerGUI.holdingGUI.hide();
					World.getInstance().getMainGUI().houseGUI.show(house);
				}
			}, house.getName());
			
			World.getInstance().getUpdater().addItem(house);
						
			for (int j = 0; j < house.getCountWars(); ++j) {
				if (house.getWar(j).defender == house) {
					World.getInstance().getUpdater().addItem(house.getWar(j));
				}
			}
		}
		
		fillInRenderables();
		
		for (int i = 0; i < world.getHouses().size(); ++i) {
			House house = World.getInstance().getHouses().get(i);
			house.rebuild();
		}
		
		world.checkProductions();
		
		for (int i = 0; i < world.armies.size(); ++i) {
			Army army = world.armies.get(i);
			World.getInstance().getUpdater().addItem(army);
			if (army.isRaised()) {
				army.createOn(army.getCurrentHolding());
				army.reposition();
				
				world.addArmyMapEntry(army, army.getEntity());
			} else if (army.isFighting() && !army.isRaised()) {
				army.setBesieged(true);
			}
			
			if (army.isFighting()) {
				if (!World.getInstance().getUpdater().hasItem(army.getBattle())) {
					World.getInstance().getUpdater().addItem(army.getBattle());
				}
			}
			
			army.createArrowPaths();
		}
		if (SceneManager.getInstance().getRenderer().getOctree(OpenGLRenderer.DEFAULT_CHANNEL).checkDuplicates()) {
			throw new RuntimeException("Duplicate in Octree detected!");
		}

		for (int i = 0; i < world.getCountBaronies(); ++i) {
			Barony b = world.getBarony(i);
			boolean explored = b.isExplored();
			if (!explored)  {
				b.explored = !explored;
				b.setExplored(explored);
			}
		}
		
		world.getMap().getFogMap().build();
		
		return world;
	}
}
