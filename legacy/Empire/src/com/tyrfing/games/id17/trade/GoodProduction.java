package com.tyrfing.games.id17.trade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.world.World;

public class GoodProduction implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8383287597129167345L;
	
	private List<Good> in = new ArrayList<Good>();
	private List<Good> out = new ArrayList<Good>();
	
	public final boolean[] producesInSeason = new boolean[4];
	
	public GoodProduction() {
		for (int i = 0; i < 4; ++i) {
			producesInSeason[i] = true;
		}
	}
	
	public void addInputGood(Good good) {
		in.add(good);
	}
	
	public void addOutputGood(Good good) {
		out.add(good);
	}
	
	public void checkProduction(Holding holding) {

		if (!producesInSeason[World.getInstance().getSeason()]) return;
		if (holding.isPillaged()) return;
		
		boolean add = true;
		
		for (int i = 0; i < in.size(); ++i) {
			if (!holding.hasGood(in.get(i))) {
				add = false;
				break;
			}
		}
		
		if (add) {
			for (int i = 0; i < out.size(); ++i) {
				holding.addGood(out.get(i).copy(), 0);
			}
			
		}
			
	}
	
	public int getCountInputGoods() {
		return in.size();
	}
	
	public int getCountOutputGoods() {
		return out.size();
	}
	
	public Good getInputGood(int index) {
		return in.get(index);
	}
	
	public Good getOutputGood(int index) {
		return out.get(index);
	}
	
	public static GoodProduction createProduction(String[] in, int[] qIn, String[] out, int[] qOut, Holding h) {
		GoodProduction p = new GoodProduction();
		for (int i = 0; i < in.length; ++i) {
			p.addInputGood(Good.createGood(in[i], qIn[i], h));
		}
		
		for (int i = 0; i < out.length; ++i) {
			p.addOutputGood(Good.createGood(out[i], qOut[i], h));
		}
		
		return p;
	}
	
	public static GoodProduction createProduction(ProdData d, Holding h) {
		return createProduction(d.in, d.qIn, d.out, d.qOut, h);
	}
	
	public static GoodProduction createProduction(String[] in, int[] qIn, String[] out, int[] qOut) {
		GoodProduction p = new GoodProduction();
		for (int i = 0; i < in.length; ++i) {
			p.addInputGood(Good.createGood(in[i], qIn[i]));
		}
		
		for (int i = 0; i < out.length; ++i) {
			p.addOutputGood(Good.createGood(out[i], qOut[i]));
		}
		
		return p;
	}
	
	public static GoodProduction createProduction(ProdData d) {
		return createProduction(d.in, d.qIn, d.out, d.qOut);
	}
	

}
