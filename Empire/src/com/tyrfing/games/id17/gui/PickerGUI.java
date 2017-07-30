package com.tyrfing.games.id17.gui;

import java.util.Collections;
import java.util.List;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.gui.holding.HoldingGUI;
import com.tyrfing.games.id17.gui.war.ArmyGUI;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.networking.ChooseHouse;
import com.tyrfing.games.id17.networking.LevyAction;
import com.tyrfing.games.id17.war.Army;
import com.tyrfing.games.id17.war.Battle;
import com.tyrfing.games.id17.world.World;
import com.tyrfing.games.id17.world.WorldChunk;
import com.tyrfing.games.id17.world.WorldObject;
import com.tyrlib2.graphics.materials.OutlineMaterial;
import com.tyrlib2.graphics.renderables.Entity;
import com.tyrlib2.graphics.renderables.Outline;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.scene.SceneObject;
import com.tyrlib2.input.IMotionEvent;
import com.tyrlib2.input.ITouchListener;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Color;
import com.tyrlib2.util.Raycast;
import com.tyrlib2.util.RaycastResult;

public class PickerGUI implements ITouchListener {

	private Vector2 initPoint = new Vector2();
	private boolean active = true;
	private static final float MAX_DISTANCE = 0.02f;
	
	public final HoldingGUI holdingGUI;
	public final ArmyGUI armyGUI;
	
	private Holding highlighted;
	public Army pickedArmy;
	private Entity selectedEntity;
	
	private OutlineMaterial highlightOutline1 = new OutlineMaterial(new Color(0.85f,0.85f,0.15f,0.60f), false);
	private OutlineMaterial highlightOutlineAnimated1 = new OutlineMaterial(new Color(0.85f,0.85f,0.15f,0.60f), true);
	private OutlineMaterial highlightOutline2 = new OutlineMaterial(new Color(0.85f,0.85f,0.15f,0.60f), false);
	private OutlineMaterial highlightOutlineAnimated2 = new OutlineMaterial(new Color(0.85f,0.85f,0.15f,0.60f), true);
	private Outline highlight;
	private Outline highlight2;
	
	public PickerGUI() {
		holdingGUI = new HoldingGUI();
		armyGUI = new ArmyGUI();
	}
	
	@Override
	public long getPriority() {
		return 0;
	}

	@Override
	public boolean onTouchDown(Vector2 point, IMotionEvent event, int fingerId) {
		
		initPoint = point;
		active = true;
		
		if (highlight2 != null) {
			SceneManager.getInstance().destroyRenderable(highlight2);
			highlight2 = null;
		}
		
		
		if (event.getButton() != 0 || EmpireFrameListener.BUILD_TARGET == EmpireFrameListener.ANDROID_TARGET) {
			highlightMoveTarget(point);
		}
		
		if (highlighted == null) {
			Raycast raycast = Raycast.fromScreen(initPoint);
			List<RaycastResult> results = raycast.performRaycast();
			Collections.sort(results);
			
//			Line3 line = new Line3(raycast.getRay().origin, raycast.getRay().origin.add(raycast.getRay().direction.multiply(1000)), Color.GREEN, 10);
//			SceneManager.getInstance().getRootSceneNode().createChild().attachSceneObject(line);
//			SceneManager.getInstance().getRenderer().addRenderable(line);
			
			for (int i = 0; i < results.size(); ++i) {
				RaycastResult result = results.get(i);
				SceneObject object = result.sceneObject;
				if (object.getMask() == WorldChunk.HOLDING_MASK) {
					Entity entity = (Entity) object;
					Holding holding = World.getInstance().getHolding(entity);
					if (holding != null && holding.holdingData.barony.isExplored()) {
						boolean animated = entity.getSkeleton().getCountBones() > 0;

						OutlineMaterial mat = animated ? highlightOutlineAnimated2 : highlightOutline2;
						mat.setVertexLayout(entity.getSubEntity(0).getMaterial().getVertexLayout());
						Outline outline = new Outline(entity.getSubEntity(0).getMesh(), 
													  animated ? entity.getSkeleton() : null,
													  mat);
						SceneManager.getInstance().getRenderer().addRenderable(outline);
						entity.getParent().attachSceneObject(outline);
						selectedEntity = entity;
						
						highlight2 = outline;
						
						return false;
					}
				} else if (object.getMask() == WorldChunk.ARMY_MASK) {
					
					Entity entity = (Entity) object;
					if (entity.getParent() != null && entity.isVisible()) {
						
						highlightOutlineAnimated2.setVertexLayout(entity.getSubEntity(0).getMaterial().getVertexLayout());
						Outline outline = new Outline(	entity.getSubEntity(0).getMesh(), 
														entity.getSkeleton(),
								  						highlightOutlineAnimated2,
								  						0.4f);
						SceneManager.getInstance().getRenderer().addRenderable(outline);
						entity.getParent().attachSceneObject(outline);
						selectedEntity = entity;
						
						highlight2 = outline;
						return false;
					}
					
				} 
			}
		
		}
		
		if (event.getButton() == 0 && EmpireFrameListener.BUILD_TARGET == EmpireFrameListener.PC_TARGET) {
			if (pickedArmy != null && pickedArmy.getOwner() == World.getInstance().getPlayerController().getHouse()) {
				
			} else {
				hideAll();
			}
		}
		
		return false;
	}

	@Override
	public boolean onTouchUp(Vector2 point, IMotionEvent event, int fingerId) {
		
		if (initPoint == null) return false;
		
		if (active && highlighted == null && selectedEntity != null) {
			
			if (selectedEntity.getMask() == WorldChunk.HOLDING_MASK) {
				Entity entity = selectedEntity;
				Holding holding = World.getInstance().getHolding(entity);
				if (holding != null) {
					
					holdingGUI.show(holding);
					World.getInstance().getMainGUI().hideAllSubGUIs();

					if (	EmpireFrameListener.state == EmpireFrameListener.GameState.SELECT) {
						boolean pickable = true;
						for (int i = 0; i < World.getInstance().players.size(); ++i) {
							if (World.getInstance().players.get(i).getHouse() == holding.getOwner()) {
								pickable = false;
							}
							
						}
						
						if (pickable) {
							World.getInstance().getPlayerController().control(holding.getOwner());
							EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new ChooseHouse(World.getInstance().getPlayerController().getHouse().id));
						}
					}
					
					if (highlight != null) {
						SceneManager.getInstance().destroyRenderable(highlight);
						highlight = null;
					}
					
					highlight = highlight2;
					highlight2 = null;

					OutlineMaterial tmp = highlightOutlineAnimated1;
					highlightOutlineAnimated1 = highlightOutlineAnimated2;
					highlightOutlineAnimated2 = tmp;
					
					tmp = highlightOutline1;
					highlightOutline1 = highlightOutline2;
					highlightOutline2 = tmp;
				}

			} else if (selectedEntity.getMask() == WorldChunk.ARMY_MASK) {
				Entity entity = selectedEntity;
				if (entity.getParent() != null) {
					if (pickedArmy != null) {
						pickedArmy.deselect();
					}

					pickedArmy = World.getInstance().getArmy(entity);
					pickedArmy.select();
					armyGUI.show(pickedArmy);
					
					holdingGUI.hide();
					World.getInstance().getMainGUI().houseGUI.hide();
					
					if (highlight != null) {
						SceneManager.getInstance().destroyRenderable(highlight);
						highlight = null;
					}
					
					highlight = highlight2;
					highlight2 = null;
					
					OutlineMaterial tmp = highlightOutlineAnimated1;
					highlightOutlineAnimated1 = highlightOutlineAnimated2;
					highlightOutlineAnimated2 = tmp;
					
					tmp = highlightOutline1;
					highlightOutline1 = highlightOutline2;
					highlightOutline2 = tmp;

				}

			} 
		} else if (		highlighted != null 
					&& 	pickedArmy != null 
					&& 	pickedArmy.getOwner().getController() == World.getInstance().getPlayerController() ) {
			if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
				pickedArmy.moveTo(highlighted);
			} else {
				EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new LevyAction(	pickedArmy.id, 
																						LevyAction.MOVETO,
																						highlighted.getHoldingID()));	
			}
			highlighted = null;
		}
		
		if (highlight2 != null) {
			SceneManager.getInstance().destroyRenderable(highlight2);
			highlight2 = null;
		}
		
		initPoint = null;
		selectedEntity = null;
		
		return false;
	}

	@Override
	public boolean onTouchMove(Vector2 point, IMotionEvent event, int fingerId) {
		
		if (initPoint == null) return false;
		
		if (point.vectorTo(initPoint).length() > MAX_DISTANCE && active) {
			
			if (highlight2 != null) {
				SceneManager.getInstance().destroyRenderable(highlight2);
				highlight2 = null;
			}
			
			active = false;
			highlighted = null;
		} 
		
		
		return false;
	}
	
	private void highlightMoveTarget(Vector2 point) {
		if (pickedArmy != null && pickedArmy.getOwner().getController() == World.getInstance().getPlayerController()) {
			Raycast raycast = Raycast.fromScreen(point);
			List<RaycastResult> results = raycast.performRaycast(World.getInstance().getOctree());
			Collections.sort(results);
			for (int i = 0; i < results.size(); ++i) {
				RaycastResult result = results.get(i);
				Entity entity = ((WorldObject)result.sceneObject).getEntity();
	
				Holding holding = World.getInstance().getHolding(entity);
				if (holding != null && highlighted != holding) {
					
					if (highlight2 != null) {
						SceneManager.getInstance().destroyRenderable(highlight2);
						highlight2 = null;
					}
  
					boolean animated = entity.getSkeleton().getCountBones() > 0;
					
					if (holding.holdingData.barony.isExplored()) {
						OutlineMaterial mat = animated ? highlightOutlineAnimated2: highlightOutline2;
						mat.setVertexLayout(entity.getSubEntity(0).getMaterial().getVertexLayout());
						highlight2 = new Outline(entity.getSubEntity(0).getMesh(), 
												 animated ? entity.getSkeleton() : null,
											     mat);
						SceneManager.getInstance().getRenderer().addRenderable(highlight2);
						entity.getParent().attachSceneObject(highlight2);
					}
					
					highlighted = holding;
					
					break;
				}
			}
		}
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
	public void showBattle(Battle battle) {
		holdingGUI.hide();
		World.getInstance().getMainGUI().houseGUI.hide();
	}
	
	public void hideAll() {
		armyGUI.hide();
		holdingGUI.hide();
	}
	
	public void unhighlight() {
		if (highlight != null) {
			SceneManager.getInstance().destroyRenderable(highlight);
			highlight = null;
		}
	}

}
