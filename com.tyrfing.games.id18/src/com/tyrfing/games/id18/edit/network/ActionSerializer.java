package com.tyrfing.games.id18.edit.network;

import java.util.UUID;

import com.tyrfing.games.id18.edit.battle.action.EndTurnAction;
import com.tyrfing.games.id18.edit.unit.action.ApplyAffectorAction;
import com.tyrfing.games.id18.edit.unit.action.MoveAction;
import com.tyrfing.games.id18.model.battle.Battle;
import com.tyrfing.games.id18.model.network.NetworkActionMessage;
import com.tyrfing.games.id18.model.network.NetworkMessage;
import com.tyrfing.games.id18.model.unit.Affector;
import com.tyrfing.games.id18.model.unit.Unit;
import com.tyrfing.games.tyrlib3.edit.action.IAction;
import com.tyrfing.games.tyrlib3.model.math.Vector2I;

public class ActionSerializer {
	
	private Battle battle;
	
	public ActionSerializer(Battle battle) {
		this.battle = battle;
	}
	
	public NetworkMessage toNetworkMessage(IAction action) {
		if (action instanceof EndTurnAction) {
			return NetworkActionMessage.IConstantMessages.END_TURN_ACTION;
		} else if (action instanceof MoveAction) {
			MoveAction moveAction = (MoveAction) action;
			NetworkMessage networkMessage = new NetworkActionMessage(NetworkActionMessage.MESSAGE_MOVE_ACTION);
			networkMessage.getParams().add(moveAction.getUnit().getUUID());
			networkMessage.getParams().add(moveAction.getTargetPosition());
			networkMessage.getParams().add(moveAction.isReduceRemainingMove());
		} else if (action instanceof ApplyAffectorAction) {
			ApplyAffectorAction affectorAction = (ApplyAffectorAction) action;
			NetworkMessage networkMessage = new NetworkMessage(NetworkActionMessage.MESSAGE_APPLY_AFFECTOR_ACTION);
			networkMessage.getParams().add(affectorAction.getUnit().getUUID());
			networkMessage.getParams().add(affectorAction.getAffector().getUUID());
			networkMessage.getParams().add(affectorAction.getTarget());
			networkMessage.getParams().add(affectorAction.isReduceRemainingActions());
		}
		
		return null;
	}
	
	public IAction toAction(NetworkMessage networkMessage) {
		if (networkMessage.getMessage() == NetworkActionMessage.MESSAGE_END_TURN_ACTION) {
			return new EndTurnAction(battle);
		} else if (networkMessage.getMessage() == NetworkActionMessage.MESSAGE_MOVE_ACTION) {
			UUID unitID = (UUID) networkMessage.getParams().get(0);
			Unit unit = (Unit) battle.getUnitByUUID(unitID);
			Vector2I targetPosition = (Vector2I) networkMessage.getParams().get(1);
			boolean reduceRemainingMove = (Boolean) networkMessage.getParams().get(2);
			return new MoveAction(unit, targetPosition, reduceRemainingMove);
		} else if (networkMessage.getMessage() == NetworkActionMessage.MESSAGE_APPLY_AFFECTOR_ACTION) {
			UUID unitID = (UUID) networkMessage.getParams().get(0);
			Unit unit = (Unit) battle.getUnitByUUID(unitID);
			UUID affectorID = (UUID) networkMessage.getParams().get(1);
			Affector affector = (Affector) unit.getAffectorByUUID(affectorID);
			Vector2I targetPosition = (Vector2I) networkMessage.getParams().get(2);
			boolean reduceRemainingMove = (Boolean) networkMessage.getParams().get(3);
			return new ApplyAffectorAction(unit, affector, targetPosition, reduceRemainingMove);
		} 
		
		return null;
	}
}
