package com.tyrfing.games.id17.holdings;

import java.io.Serializable;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.networking.TravellerMessage;
import com.tyrfing.games.id17.war.Army;
import com.tyrfing.games.id17.world.RoadMap;
import com.tyrfing.games.id17.world.RoadNode;
import com.tyrfing.games.id17.world.SeasonMaterial;
import com.tyrfing.games.id17.world.Tile;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.game.IUpdateable;
import com.tyrlib2.graphics.materials.DefaultMaterial3;
import com.tyrlib2.graphics.renderables.Entity;
import com.tyrlib2.graphics.renderer.ProgramManager;
import com.tyrlib2.graphics.renderer.UParam1f;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.math.Quaternion;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.movement.DirectMovement;
import com.tyrlib2.movement.IMovementListener;
import com.tyrlib2.movement.Speed;
import com.tyrlib2.util.Valuef;

public class Traveller implements IMovementListener, Serializable, IUpdateable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2445971691124455602L;

	public static final float DEFAULT_SPEED = 1;
	public static final float ANIM_SPEED = 2.5f;
	public static final Vector3 SCALE = new Vector3(-0.925f, -0.925f, -0.925f);
	
	private Speed SPEED = new Speed(DEFAULT_SPEED);
	
	private DirectMovement movement;
	private SceneNode node;
	private Entity entity;
	
	private Vector3 forward = new Vector3(0,-1,0);
	private Valuef ownerValue = new Valuef(SeasonMaterial.NONE);
	
	public Traveller(int srcHolding, int dstHolding) {
		RoadMap map = World.getInstance().getMap().getRoadMap();
		RoadNode srcNode = map.getRoadNode(srcHolding);
		
		node = SceneManager.getInstance().getRootSceneNode().createChild();
		node.setRelativePos(new Vector3(srcNode.getTargetPos()));
		movement = new DirectMovement(node, SPEED);
		movement.addMovementListener(this);
		
		map.moveFromTo(movement, srcHolding, dstHolding);
		
		if (!movement.isFinished()) {
			World.getInstance().getUpdater().addItem(this);
			
			entity = SceneManager.getInstance().createEntity("entities/aldeano.iqm");
			
			DefaultMaterial3 mat = (DefaultMaterial3)entity.getSubEntity(0).getMaterial();
			mat = (DefaultMaterial3) mat.copy();
			entity.getSubEntity(0).setMaterial(mat);
			mat.setProgram(ProgramManager.getInstance().getProgram(DefaultMaterial3.PER_PIXEL_PROGRAM_NAME+"_ANIMATED"));
			mat.addParam(new UParam1f("u_Owner", ownerValue));
			
			node.attachSceneObject(entity);
			
			node.scale(SCALE);
			
			entity.playAnimation("WALK");
		}
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new TravellerMessage(srcHolding, dstHolding));
		}
		
	}

	@Override
	public void onTargetReached() {
		if (movement.isFinished()) {
	 		World.getInstance().getUpdater().removeItem(this);
			SceneManager.getInstance().getRenderer().removeRenderable(entity);
			node.detach();
		} else {
			Vector3 pos = entity.getParent().getCachedAbsolutePos();
			Vector3 targetPos =  movement.peekNextTargetProvider().getTargetPos();
			pos.z = targetPos.z;
			
			Vector3 vectorTo = pos.vectorTo(targetPos);
			vectorTo.normalize();
			Vector3 dep = forward.cross(vectorTo);
			forward.normalize();
			
			if (Math.abs(dep.x) > Army.EPS || Math.abs(dep.y) > Army.EPS || Math.abs(dep.z) > Army.EPS) {
				Quaternion quat = Quaternion.rotateTo(forward, vectorTo);
				rotate(quat);
			} else if (Math.abs(forward.x + vectorTo.x) < Army.EPS && Math.abs(forward.y + vectorTo.y) < Army.EPS && Math.abs(forward.z + vectorTo.z) < Army.EPS) {
				rotate(Quaternion.fromAxisAngle(new Vector3(0,0,1), 180));
			}
		}
	}
	
	public void rotate(Quaternion rot) {
		forward = rot.multiply(forward);
		node.rotate(rot);
	}

	@Override
	public void onUpdate(float time) {
		time = (float) (time*Math.sqrt(World.getInstance().getPlaySpeed()));
		
		movement.onUpdate(time);
		
		if (entity.isVisible()) {
			entity.onUpdate(time);
		}
		
		Tile currentTile = World.getInstance().getMap().getTileFromWorldCoord(node.getCachedAbsolutePos());
		if (currentTile != null) {
			Barony currentBarony = World.getInstance().getHolding(currentTile.chunk.getCastleEntity()).holdingData.barony;
			if (currentBarony.isExplored()) {
				entity.setVisible(true);
			} else {
				entity.setVisible(false);
			}
			
			ownerValue.value = currentBarony.getWorldChunk().getOwnerValue();
		}
	}

	@Override
	public boolean isFinished() {
		return false;
	}
}
