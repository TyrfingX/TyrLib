package com.tyrfing.games.id17.networking;

import java.util.List;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.holdings.UnrestSource;
import com.tyrfing.games.id17.holdings.projects.BuildingProject;
import com.tyrfing.games.id17.holdings.projects.IProject;
import com.tyrfing.games.id17.holdings.projects.RoadProject;
import com.tyrfing.games.id17.holdings.projects.UpgradeRegimentProject;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.HouseController;
import com.tyrfing.games.id17.houses.Loan;
import com.tyrfing.games.id17.intrigue.Intrigue;
import com.tyrfing.games.id17.war.War;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.networking.Connection;
import com.tyrlib2.networking.Network;


public class RequestWorld extends NetworkMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2098434868230812207L;

	@Override
	public void process(Connection con) {
		List<House> houses = World.getInstance().getHouses();
		List<Holding> holdings = World.getInstance().getHoldings();
		List<House> rankedHouses = World.getInstance().getRankedHouses();
		List<HouseController> players = World.getInstance().players;

		
		con.openToBroadcasts = true;
		Network network = EmpireFrameListener.MAIN_FRAME.getNetwork();
		
		int houseID = 0;
		if (!EmpireFrameListener.MAIN_FRAME.randomJoin) {
			search: for (int i = 0; i < rankedHouses.size(); ++i) {
				if (rankedHouses.get(i).getHoldings().size() == 0) continue search;
				houseID = rankedHouses.get(i).id;
				for (int j = 0; j < players.size(); ++j) {
					if (players.get(j).getHouse().id == houseID) {
						continue search;
					}
				}
				break;
			}
		} else {
			 while (houseID == 0) {
				houseID = World.getInstance().getRandomHouse();
				for (int j = 0; j < players.size(); ++j) {
					if (players.get(j).getHouse().id == houseID) {
						houseID = 0;
						break;
					}
				}
			}
		}
		
		int playerID = -1;
		
		nextPlayer: for (int i = 0; i < World.getInstance().nextPlayerID; ++i) {
			for (int j = 0; j < players.size(); ++j) {
				if (players.get(j).playerID == i) {
					continue nextPlayer;
				}
			}
			
			playerID = i;
			break;
		}
		
		if (playerID == -1) {
			playerID = World.getInstance().nextPlayerID++;
		}
		
		network.send(new NewConnection(houseID, playerID), con);
		
		for (int i = 0; i < players.size(); ++i) {
			network.send(new NewPlayer(players.get(i).getHouse().id, players.get(i).playerID, players.get(i).hasJoined), con);
		}
		
		final NetworkController nc = new NetworkController(con);
		nc.playerID = playerID;
		nc.control(World.getInstance().getHouses().get(houseID));
		players.add(nc);
		
		EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new NewPlayer(nc.getHouse().id, nc.playerID, false));
		
		for (int i = 1; i < houses.size(); ++i) {
			House owner = houses.get(i);
			if (owner.isRebel()) {
				network.send(new Revolt(owner.getTotalTroops(),
										owner.id), 
										con.ID);
			} else if (owner.isMarauder()) {
				network.send(new MaraudingArmy(owner.armies.get(0)), 
							 con.ID);
			}
		}
		
		for (short i = 0; i < holdings.size(); ++i) {
			Holding h = holdings.get(i);
			network.send(new HoldingState(h), con.ID);
			
			for (short j =  0; j < h.getCountUnrestSources(); ++j) {
				UnrestSource src = h.getUnrestSource(j);
				network.send(new UnrestSourceChange(
						i, src, UnrestSourceChange.ADD
					), con.ID
				);
			}
		}
		
		for (int i = 0; i < houses.size(); ++i) {
			House house = houses.get(i);
			network.send(World.getInstance().getHouseState(house.id), con.ID);
			if (house.intrigueProject != null && house.intrigueProject.sender == house) {
				int index = Intrigue.actions.indexOf(house.intrigueProject.action);
				NetworkAction na = new NetworkAction(	NetworkAction.INTRIGUE_ID,
														index, 
														house.id, 
														house.intrigueProject.receiver.id,
														house.intrigueProject.options, 0);
				EmpireFrameListener.MAIN_FRAME.getNetwork().send(na, con.ID);
			}
			if (house.techProject != null) {
				EmpireFrameListener.MAIN_FRAME.getNetwork().send(new TechnologyEvent(house.techProject.tech.ID, house.id, -1), con.ID);
			}
			
			for (int j = 0; j < house.loans.size(); ++j) {
				Loan loan = house.loans.get(j);
				EmpireFrameListener.MAIN_FRAME.getNetwork().send(new LoanChange(loan, LoanChange.ADD), con.ID);
			}
		}
		
		for (int i = 0; i < houses.size(); ++i) {
			House house = houses.get(i);
			if (house.intrigueProject != null && house.intrigueProject.sender != house) {
				network.send(new JoinIntrigue(house.id, house.intrigueProject.sender.id), con.ID);
			}
			
			for (int j = 0; j < house.getCountWars(); ++j) {
				War war = house.getWar(j);
				if (war.defender == house) {
					EmpireFrameListener.MAIN_FRAME.getNetwork().send(new NewWar(war),con.ID);
				}
			}
			
			for (int j = 0; j < house.getCountVisibleBaronies(); ++j) {
				EmpireFrameListener.MAIN_FRAME.getNetwork().send(
					new AddVisibleBarony(	
						(short) house.id, 
						(short) house.getVisibleBarony(j),
						false),
					con.ID
				);
			
			}
		}
		
		for (int i = 0; i < houses.size(); ++i) {
			House house = houses.get(i);
			for (int j = 0; j < house.statModifiers.size(); ++j) {
				network.send(new HouseStatChange(house.getStatModifier(j), HouseStatChange.ADD), con.ID);
			}
		}
		
		for (short i = 0; i < holdings.size(); ++i) {
			Holding h = holdings.get(i);
			
			IProject p = h.getProject();
			if (p != null) {
				if (p instanceof BuildingProject) {
					BuildingProject bp = (BuildingProject) p;
					network.send(bp.getPacket(), con.ID);
				} else if (p instanceof UpgradeRegimentProject) {
					UpgradeRegimentProject urp = (UpgradeRegimentProject) p;
					network.send(urp.getPacket(), con.ID);
				} else if (p instanceof RoadProject) {
					RoadProject rp = (RoadProject) p;
					network.send(rp.getPacket(), con.ID);
				}
			}
		}
		
		network.send(World.getInstance().getLevyState(), con.ID);
		network.send(World.getInstance().getWorldState(nc.playerID), con.ID);
	}
}
