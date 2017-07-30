package com.tyrfing.games.id17.holdings;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.tyrfing.games.id17.trade.Good;
import com.tyrfing.games.id17.war.UnitType;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.graphics.renderables.Entity;

public class HoldingData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8484279926073892288L;
	
	public float[] population = new float[PopulationType.VALUES.length];
	public float inhabitants;
	public float growth;
	public String name;
	public String typeName;
	public Barony barony;
	public transient Entity worldEntity;
	public int objectNo;
	
	public float incomeMult = 1;
	public float incomeBonus;
	public float tradeIncome;
	public float prodMult=1;
	
	public float prodBuildings;
	public float prodPop;
	public float prodTrade;
	
	public int storeGrainMax = 0;
	public int storeGrain = 0;
	public int storedGrain = 0;
	
	public float research;
	public float researchMult=1;
	public float researchBonus;
	public float researchBuildings;
	public float researchPop;
	
	public float income;
	public float prod;
	public int supplies = 0;
	public float baseSupplies = 4000;
	public float tradeSupplies = 0;
	public float revoltRisk;
	
	public float atkMult = 1f;
	public float defMult = 1f;
	public float[] typeMult = new float[UnitType.values().length];

	public float tradeBonus = 1f;
	
	public float trade;
	public float taxes;

	public float maint;
	public float buildingMaint;
	
	public short index;

	public float troopRevoltStop;

	public float education;
	
	public float[] goodsMult = new float[Good.COUNT_GOODS];
	public float[] prodPerGood = new float[Good.COUNT_GOODS];
	
	public float accRevoltRisk;
	
	private void writeObject(ObjectOutputStream stream) throws IOException {
		stream.writeObject(population);
		stream.writeObject(goodsMult);
		stream.writeObject(prodPerGood);
		stream.writeFloat(inhabitants);
		stream.writeFloat(growth);
		stream.writeObject(name);
		stream.writeObject(typeName);
		stream.writeInt(barony.getIndex());
		stream.writeInt(objectNo);
		stream.writeFloat(incomeMult);
		stream.writeFloat(incomeBonus);
		stream.writeFloat(tradeIncome);
		stream.writeFloat(prodBuildings);
		stream.writeFloat(prodMult);
		stream.writeInt(storeGrainMax);
		stream.writeInt(storeGrain);
		stream.writeFloat(research);
		stream.writeFloat(researchMult);
		stream.writeFloat(researchBonus);
		stream.writeFloat(income);
		stream.writeFloat(prod);
		stream.writeInt(supplies);
		stream.writeFloat(baseSupplies);
		stream.writeFloat(revoltRisk);
		stream.writeFloat(atkMult);
		stream.writeFloat(defMult);
		stream.writeObject(typeMult);
		stream.writeFloat(tradeBonus);
		stream.writeFloat(trade);
		stream.writeFloat(taxes);
		stream.writeFloat(maint);
		stream.writeFloat(buildingMaint);
		stream.writeShort(index);
		stream.writeFloat(education);
		stream.writeFloat(accRevoltRisk);
	}
	
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		population = (float[]) stream.readObject();
		goodsMult = (float[]) stream.readObject();
		prodPerGood = (float[]) stream.readObject();
		inhabitants = stream.readFloat();
		growth = stream.readFloat();
		name = (String) stream.readObject();
		typeName = (String) stream.readObject();
		barony = World.getInstance().getBarony(stream.readInt());
		objectNo = stream.readInt();
		incomeMult = stream.readFloat();
		incomeBonus = stream.readFloat();
		tradeIncome = stream.readFloat();
		prodBuildings = stream.readFloat();
		prodMult = stream.readFloat();
		storeGrainMax = stream.readInt();
		storeGrain = stream.readInt();
		research = stream.readFloat();
		researchMult = stream.readFloat();
		researchBonus = stream.readFloat();
		income = stream.readFloat();
		prod = stream.readFloat();
		supplies = stream.readInt();
		baseSupplies = stream.readFloat();
		revoltRisk = stream.readFloat();
		atkMult = stream.readFloat();
		defMult = stream.readFloat();
		typeMult  = (float[]) stream.readObject();
		tradeBonus = stream.readFloat();
		trade = stream.readFloat();
		taxes = stream.readFloat();
		maint = stream.readFloat();
		buildingMaint = stream.readFloat();
		index = stream.readShort();
		education = stream.readFloat();
		accRevoltRisk = stream.readFloat();
	}
	
	public void changePop(int popType, float amount) {
		if (Float.isNaN(amount) || Float.isInfinite(amount)) {
			System.out.println("sd");
		}
		population[popType] += amount;
		if (population[popType] < 0) {
			population[popType] = 0;
		}
	}
}
