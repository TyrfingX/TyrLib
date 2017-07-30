package com.tyrfing.games.id17.houses;

import java.io.Serializable;

import com.tyrfing.games.id17.world.World;

public class Loan implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1011440738395796918L;
	
	public static final int YEARS = 5;
	public static final float PAYBACK_FACTOR = 1.05f;
	public static final float SEASONAL_INTEREST = 0.01f;
	
	public final int loanSize;
	public final int payback;
	public final int interest;
	public final House giver;
	public final House taker;
	public final float endDate;
	
	public boolean ended;
	
	public Loan(House giver, House taker, int loanSize) {
		this.loanSize = loanSize;
		payback = getPayback(loanSize);
		interest = getInterest(payback);
		
		this.giver = giver;
		this.taker = taker;
		
		endDate = World.getInstance().getWorldTime() + World.DAYS_PER_YEAR * World.SECONDS_PER_DAY * YEARS;
	}
	
	public Loan(House giver, House taker, int loanSize, float endDate) {
		this.loanSize = loanSize;
		payback = getPayback(loanSize);
		interest = getInterest(payback);
		
		this.giver = giver;
		this.taker = taker;
		
		this.endDate = endDate;
	}
	
	public static int getLoanSize(House requester, House giver) {
		return (int) (requester.getIncome()+giver.getIncome()) * 5;
	}
	
	public static int getInterest(int payback) {
		return (int) (payback * SEASONAL_INTEREST);
	}

	public static int getPayback(int loanSize) {
		return (int) (loanSize * PAYBACK_FACTOR);
	}

	public House getOther(House house) {
		return house == giver ? taker : giver;
	}
}
