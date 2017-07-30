package com.tyrfing.games.id17.trade;

import gnu.trove.list.array.TIntArrayList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.world.World;

public abstract class Good implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3964416703649624850L;

	public static final int COUNT_GOODS = 9;
	public static final int[] FOOD_IDS = { Grain.ID, Flour.ID, Meat.ID, Bread.ID };
	
	static {
		Arrays.sort(FOOD_IDS);
	}
	
	protected String name;
	protected float baseValue;
	protected int quantity;
	protected List<Holding> producers = new ArrayList<Holding>(10);
	protected TIntArrayList quantities = new TIntArrayList();
	
	protected final int id;

	public Good(String name, float baseValue, int id) {
		this.name = name;
		this.baseValue = baseValue;
		this.id = id;
	}
	
	public Good(String name, float baseValue, int quantity, List<Holding> producers, TIntArrayList quantities, int id) {
		this.name = name;
		this.baseValue = baseValue;
		this.quantity = quantity;
		for (int i = 0, countProducers = producers.size(); i < countProducers; ++i) {
			this.producers.add(producers.get(i));
			this.quantities.add(quantities.getQuick(i));
		}
		this.id = id;
	}
	
	public Good(String name, float baseValue, int quantity, Holding producer, int id) {
		this.name = name;
		this.baseValue = baseValue;
		this.quantity = quantity;
		this.producers.add(producer);
		this.quantities.add(quantity);
		this.id = id;
	}
	
	public Good(String name, float baseValue, int quantity, int id) {
		this.name = name;
		this.baseValue = baseValue;
		this.quantity = quantity;
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public float getBaseValue() {
		return baseValue;
	}
	
	public float getQuantity() {
		return (quantity * World.getInstance().getGoodMult(id));
	}
	
	public int getQuantity(int index) {
		if (index < quantities.size()) {
			return quantities.getQuick(index);
		} else {
			return 0;
		}
	}
	
	public void changeQuantity(int change, int index) {
		quantity += change;
		int producerQuantity = quantities.getQuick(index);
		quantities.set(index, producerQuantity  + change);
	}
	
	public void changeQuantity(int change, Holding producer) {
		int index = producers.indexOf(producer);
		if (index == -1) {
			producers.add(producer);
			quantities.add(change);
		} else {
			changeQuantity(change, index);
		}
		
	}
	
	public abstract Good copy();
	public abstract Good create();
	
	public Good copy(int producerID) {
		Good good = this.create();
		Holding p = producers.get(producerID);
		good.producers.add(p);
		good.quantities.add(quantities.getQuick(producerID));
		good.quantity = good.quantities.getQuick(0);
		return good;
	}
	
	public float getValue(Holding h) {
		
		float res = 0;
		float demand = h.getDemand(id)+1;
		
		if (Arrays.binarySearch(FOOD_IDS, id) >= 0) {
			demand += h.getFoodDemand();
		}
		
		demand = demand / (h.getSupply(id)+1);
		
		if (demand < 1) {
			demand = demand*demand;
		}
		
		for (int i = 0; i < producers.size(); ++i) {
			Holding p = producers.get(i);
			res += 	  demand * baseValue * quantities.getQuick(i) 
					* p.getOwner().goodsMult[id][1] 
					* p.holdingData.tradeBonus
					* World.getInstance().getGoodMult(id)
					* (h.holdingData.goodsMult[id]+1);
		}	
		
		return ((int)(res*10))/10.f;
	}
	
	public void onAddSupply(Holding holding) {
		
		float base = baseValue;
		
		float demand = holding.getDemand(id)+1;
		
		if (Arrays.binarySearch(FOOD_IDS, id) >= 0) {
			demand += holding.getFoodDemand();
		}
		
		demand = demand / (holding.getSupply(id)+1);
		if (demand < 1) {
			demand = demand*demand;
		}
		
		for (int i = 0, countProducers = producers.size(); i < countProducers; ++i) {
			Holding p = producers.get(i);
			float ownerMult = p.getOwner().goodsMult[id][1];
			float worldMult = World.getInstance().getGoodMult(id);
			p.holdingData.tradeIncome += demand * base * quantities.getQuick(i) * ownerMult * p.holdingData.tradeBonus * worldMult;
		}
	}
	public void onRemoveSupply(Holding holding) {
		float base = baseValue;
		
		float demand = holding.getDemand(id)+1;
		
		if (Arrays.binarySearch(FOOD_IDS, id) >= 0) {
			demand += holding.getFoodDemand();
		}
		
		demand = demand / (holding.getSupply(id)+1);
		if (demand < 1) {
			demand = demand*demand;
		}
		
		for (int i = 0, countProducers = producers.size(); i < countProducers; ++i) {
			Holding p = producers.get(i);
			float ownerMult = p.getOwner().goodsMult[id][1];
			float worldMult = World.getInstance().getGoodMult(id);
			p.holdingData.tradeIncome -= demand * base * quantities.getQuick(i) * ownerMult * p.holdingData.tradeBonus * worldMult;
		}
	}

	public List<Holding> getProducers() {
		return producers;
	}
	
	public int getProducentID(Holding holding) {
		return producers.indexOf(holding);
	}
	
	public void addProducent(int quantity, Holding holding) {
		producers.add(holding);
		quantities.add(quantity);
		this.quantity += quantity;
	}
	
	
	
	public static Good createGood(String name, int q, Holding h) {
		if (name.equals("Flour")) {
			return new Flour(q, h);
		} else if (name.equals("Grain")) {
			return new Grain(q, h);
		} else if (name.equals("Horse")) {
			return new Horse(q, h);
		} else if (name.equals("Iron")) {
			return new Iron(q, h);
		} else if (name.equals("Meat")) {
			return new Meat(q, h);
		} else if (name.equals("Wood")) {
			return new Wood(q, h);
		} else if (name.equals("Bread")) {
			return new Bread(q, h);
		} else if (name.equals("Weaponry")) {
			return new Weaponry(q, h);
		}
		
		return null;
	}
	
	public static Good createGood(String name, int q) {
		if (name.equals("Flour")) {
			return new Flour(q, new TIntArrayList(), new ArrayList<Holding>());
		} else if (name.equals("Grain")) {
			return new Grain(q, new TIntArrayList(), new ArrayList<Holding>());
		} else if (name.equals("Horse")) {
			return new Horse(q, new TIntArrayList(), new ArrayList<Holding>());
		} else if (name.equals("Iron")) {
			return new Iron(q, new TIntArrayList(), new ArrayList<Holding>());
		} else if (name.equals("Meat")) {
			return new Meat(q, new TIntArrayList(), new ArrayList<Holding>());
		} else if (name.equals("Wood")) {
			return new Wood(q, new TIntArrayList(), new ArrayList<Holding>());
		} else if (name.equals("Bread")) {
			return new Bread(q, new TIntArrayList(), new ArrayList<Holding>());
		} else if (name.equals("Weaponry")) {
			return new Weaponry(q, new TIntArrayList(), new ArrayList<Holding>());
		} else if (name.equals("Stone")) {
			return new Stone(q, new TIntArrayList(), new ArrayList<Holding>());
		}
		
		return null;
	}
	
	public abstract String getTooltip(Holding holding);

	public static Good createGood(int goodProd, int q, Holding holding) {
		List<Holding> producers = new ArrayList<Holding>();
		TIntArrayList quantities = new TIntArrayList();
		
		producers.add(holding);
		quantities.add(q);
		
		if (goodProd == 0) {
			return new Flour(q, quantities, new ArrayList<Holding>());
		} else if (goodProd == 1) {
			return new Grain(q, quantities, producers);
		} else if (goodProd == 2) {
			return new Horse(q, quantities, producers);
		} else if (goodProd == 3) {
			return new Iron(q, quantities, producers);
		} else if (goodProd == 4) {
			return new Meat(q, quantities, producers);
		} else if (goodProd == 5) {
			return new Wood(q, quantities, producers);
		} else if (goodProd == 6) {
			return new Bread(q, quantities, producers);
		} else if (goodProd == 7) {
			return new Weaponry(q, quantities, producers);
		} else if (goodProd == 8) {
			return new Stone(q, quantities, producers);
		}
		
		return null;
	}
	
	public static String getName(int id) {
		switch (id) {
		case Bread.ID:
			return Bread.NAME;
		case Flour.ID:
			return Flour.NAME;
		case Grain.ID:
			return Grain.NAME;
		case Horse.ID:
			return Horse.NAME;
		case Iron.ID:
			return Iron.NAME;
		case Meat.ID:
			return Meat.NAME;
		case Stone.ID:
			return Stone.NAME;
		case Wood.ID:
			return Wood.NAME;
		default:
			return "UNDEFINED";
		}
	}

	private void writeObject(ObjectOutputStream stream) throws IOException {
		stream.writeObject(name);
		stream.writeInt(producers.size());
		for (int i = 0; i < producers.size(); ++i) {
			stream.writeObject(producers.get(i));
			stream.writeInt(quantities.getQuick(i));
		}

	}
	
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		name = (String) stream.readObject();
		int countProducers = stream.readInt();
		producers = new ArrayList<Holding>(countProducers);
		quantities = new TIntArrayList();
		quantity = 0;
		for (int i = 0; i < countProducers; ++i) {
			producers.add((Holding)stream.readObject());
			int q = stream.readInt();
			quantities.add(q);
			quantity += q;
		}

	}
	
	public static float getMult(Holding holding, int ID) {
		return holding.getOwner().goodsMult[ID][0] * World.getInstance().getGoodMult(ID);
	}

	public float getNecessaryQuantity() {
		return quantity;
	}
	
	public int getID() {
		return id;
	}
}
