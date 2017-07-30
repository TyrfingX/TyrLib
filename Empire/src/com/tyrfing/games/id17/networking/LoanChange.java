package com.tyrfing.games.id17.networking;

import java.util.List;

import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.Loan;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.networking.Connection;

public class LoanChange extends NetworkMessage {
	/**
	 * 
	 */
	private static final long serialVersionUID = 219108792461241444L;
	
	public static final byte ADD = 0;
	public static final byte REMOVE = 1;
	
	public final short giverID;
	public final short takerID;
	public final int loanSize;
	public final float endDate;
	public final byte action;
	
	public LoanChange(Loan loan, byte action) {
		this.giverID = loan.giver.id;
		this.takerID = loan.taker.id;
		this.loanSize = loan.loanSize;
		this.endDate = loan.endDate;
		this.action = action;
	}
	
	@Override
	public void process(Connection c) {
		List<House> houses = World.getInstance().getHouses();
		
		House giver = houses.get(giverID);
		House taker = houses.get(takerID);
		
		Loan loan = new Loan(giver, taker, loanSize, endDate);
		
		switch (action) {
		case ADD:
			giver.addLoan(loan);
			taker.addLoan(loan);
			break;
		case REMOVE:
			giver.removeLoan(loan);
			taker.removeLoan(loan);
			break;
		}
	}
	
	
}
